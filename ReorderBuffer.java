import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReorderBuffer implements InstructionVoidVisitor{

    public static final int NO_DEST = -1;
    private CircluarQueue<ReorderEntry> buffer;
    private final Map<Integer, List<Integer>> cdb;
    private final RegisterFile rf;
    private RegisterAliasTable rat;
    private final Memory mem;

    private ReorderEntry currentCommit;
    private boolean ratSet = false;

    private final int size;

    private CircluarQueue<ReorderEntry> lsq;

    private final ProgramCounter pc;

    private boolean shouldFlush = false;
    private static final int FLUSH_NOWHERE = -1;
    private int shouldFlushWhere;

    private int committed;
    private List<Instruction> committedInstrs;

    ReorderBuffer(int size, Map<Integer, List<Integer>> cdb, RegisterFile rf, Memory mem, ProgramCounter pc){
        this.buffer = new CircluarQueue<ReorderEntry>(size);
        this.cdb = cdb;
        this.rf = rf;
        this.currentCommit = null;
        this.mem = mem;
        this.size = size;
        this.lsq = new CircluarQueue<ReorderEntry>(size);
        this.pc = pc;
        this.committed = 0;
        this.committedInstrs = new ArrayList<Instruction>();
        this.shouldFlushWhere = FLUSH_NOWHERE;
    }

//    /**
//     * it doesnt matter if there can only ever be one of these, there is only ever one rob
//     */
//    public class LoadStoreQueue{
//
//        private final CircluarQueue<ReorderEntry> buffer;
//
//        LoadStoreQueue(){
//            this.buffer = new CircluarQueue<ReorderEntry>(size); //always big enough to fit all the loads and stores in the rob
//        }
//
//        public void add(ReorderEntry re){
//            buffer.push(re);
//        }
//
//    }

    //avoids circular dep
    public void setRat(RegisterAliasTable rat){
        this.rat = rat;
        ratSet = true;
    }

    public boolean needsFlushing(){
        return shouldFlush;
    }

    public void doneFlushing(){
        shouldFlush = false;
        shouldFlushWhere = FLUSH_NOWHERE;
    }

    /**
     * the queue is like
     *   head v           v tail
     * [_, _, e, d, c, b, a] and we say 'flush from c'
     * [_, _, _, _, _, b, a] note it is inclusive
     * @param id the item to start flushing at, flushes this item too
     */
    public void flushFrom(int id){
        //System.out.println("rob flushes from " + id);
        CircluarQueue<ReorderEntry> newBuffer = new CircluarQueue<ReorderEntry>(size);
        CircluarQueue<ReorderEntry> newLsq = new CircluarQueue<ReorderEntry>(size);
        ReorderEntry peel = buffer.pop();
        while(!buffer.isEmpty() && peel.getId() != id){
            if(!lsq.isEmpty() && peel.getId() == lsq.peek().getId()){
                newLsq.push(lsq.pop()); //should include just the same instructions as the main rob
            }
            newBuffer.push(peel);
            peel = buffer.pop();
        }
        buffer = newBuffer;
        lsq = newLsq;
    }

    public void add(ReorderEntry re){
        buffer.push(re);
        if(Utils.isLoadStore(re.getOp())) lsq.push(re);
    }

    public boolean isFull(){
        return buffer.isFull();
    }

    private String appendComma(String s){
        return s + ",";
    }

    public String contents() { String items = "";
        for(ReorderEntry re : buffer.peekXs()){
            items += ",[" + re.getId() + "]" + Utils.twoDigitInstrId(re.getOp());
        }
        return items;
    }

    public String toString(){
        return "[" + contents() + "]\t" +  lsq;
    }

    private ReorderEntry precedingLoadOrNull(){
        List<ReorderEntry> xs = lsq.peekXs();
        for(int i = xs.size() - 1; i >= 0; i--){
            //find the closest preceding __ready__ load
            ReorderEntry x = xs.get(i);
            if(x.getOp().visit(new ConcreteCodeVisitor()) == Opcode.ld){
                return x;
            }
        }
        return null;
    }

    private int locationOfEntryId(int id, CircluarQueue<ReorderEntry> buff){
        List<ReorderEntry> re = buff.peekXs();
        int index = -1;
        int i = 0;
        for(ReorderEntry r : re){
            if(r.id == id) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    private void setValOfQueueEntry(int id, int val, CircluarQueue<ReorderEntry> buff){
        List<ReorderEntry> re = buff.peekXs();
        int index = locationOfEntryId(id, buff);
        if(index == -1) throw new RuntimeException("setValOfEntry: there is no such entry with id '" + id + "' in buffer");
        ReorderEntry rChange = re.get(index);
        rChange.setValue(val);
        rChange.readyUp();
        buff.setElement(index, rChange);
    }

    public void setValOfEntry(int id, int val){
        setValOfQueueEntry(id, val, buffer);
        setValOfQueueEntry(id, val, lsq);
    }

    public void setEntryReady(int id){
        getEntry(id).readyUp();
        if(Utils.isLoadStore(getEntry(id).getOp())) getLSQEntry(id).readyUp();
    }
//    public void setValOfLSQEntry(int id, int val){
//        setValOfQueueEntry(id, val, lsq);
//    }

    public int getValOfEntry(int id){
        List<ReorderEntry> re = buffer.peekXs();
        int index = locationOfEntryId(id, buffer);
        if(index == -1) throw new RuntimeException("getValOfEntry: there is no such entry with id '" + id + "' in reorder buffer");;
        return re.get(index).getValue();
    }

    public ReorderEntry getLSQEntry(int id){
        List<ReorderEntry> re = lsq.peekXs();
        int index = locationOfEntryId(id, lsq);
        if(index == -1) throw new RuntimeException("getValOfEntry: there is no such entry with id '" + id + "' in lsq");;
        return re.get(index);
    }

    public ReorderEntry getEntry(int id){
        List<ReorderEntry> re = buffer.peekXs();
        int index = locationOfEntryId(id, buffer);
        if(index == -1) throw new RuntimeException("getValOfEntry: there is no such entry with id '" + id + "' in reorder buffer");;
        return re.get(index);
    }

    private void readOffCDB(CircluarQueue<ReorderEntry> buff){
        List<ReorderEntry> re = buff.peekXs();
        // read off the cbd and into the robber
        for(ReorderEntry r: re){
//            if(r.isReady()) System.out.println("entry " + r.getId() + " is ready!");
            if(cdb.containsKey(r.getId())){
                List<Integer> payload = cdb.get(r.getId());
                //if its a fixed branch (non-conditional) it has no value and no payload so i dont write any value on the cdb
                if(!payload.isEmpty()) r.setValue(payload.get(0)); //read from the map if the id matches this rese entry (value is already in the entry before we commit!)
                r.readyUp();
            }
        }
    }

    private void popReadyInBuffer(CircluarQueue<ReorderEntry> buff){
        List<ReorderEntry> re = buff.peekXs();
        if(!re.isEmpty()){
            ReorderEntry willPop = re.get(re.size() - 1);
            if(willPop.isReady()){
                //visitation to distinguish between stores, common instructions, and branches (incorrect)
                currentCommit = buff.pop(); //accessible in visitor
                Instruction poppedOp = currentCommit.getOp();
                poppedOp.visit(this);
            }
        }
    }

    public boolean isEmpty(){
        return buffer.isEmpty(); //exactly when lsq is empty
    }

    public void clk(){
        if(!ratSet) throw new RuntimeException("ReorderBuffer.clk: prf not set");

        readOffCDB(buffer);
        readOffCDB(lsq); //update both

        if(!buffer.isEmpty()){
            ReorderEntry willPop = buffer.peek();
            ReorderEntry lsqWillPop = null;
            if(Utils.isLoadStore(willPop.getOp())) lsqWillPop = lsq.peek();
            if(willPop.isReady() && (lsqWillPop == null || lsqWillPop.isReady())){
                currentCommit = buffer.pop(); //accessible in visitor
                Instruction poppedOp = currentCommit.getOp();
                if(lsqWillPop != null) lsq.pop();
                //visitation to distinguish between stores, common instructions, and branches (incorrect)
                poppedOp.visit(this);
                committedInstrs.add(poppedOp);
                committed++;
            }
        }
        //commit stage! (start with max one instruction per clock cycle)
        currentCommit = null;
    }

    public int getShouldFlushWhere(){
        return shouldFlushWhere;
    }
    public int getCommitted(){
        return committed;
    }

    public List<Instruction> getCommittedInstrs(){
        return committedInstrs;
    }

    @Override
    public void accept(Op.Add op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.AddI op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Mul op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.MulI op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Cmp op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Ld op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.LdC op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        rat.regValIsReady(op.getRd());
//        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.St op) {
//        System.out.println("STORING " + currentCommit.getValue() + " AT " + op.getResult());
        mem.set(currentCommit.getValue(), op.getResult()); //addr gets stored in result in the LSU!
//        cdb.remove(currentCommit.getId());
        ReorderEntry lastLoadOrNull = precedingLoadOrNull();
        if(lastLoadOrNull == null) return; //no load precedes

        //IF THE LOAD IS NOT COLLIDING AND NOT READY (NOT EVEN PROCESSED, so is not colliding by virtue of not having
        // worked out the effective address) we dont do anything and the
        if(!lastLoadOrNull.getOp().hasResult()) return;

        //CHECK IF THERE IS A COLLISION IN ADDRESSES
        boolean collide = lastLoadOrNull.getOp().getResult() == op.getResult();
        //CHECK IF THE LOAD IS READY
        // IF THE LOAD IS READY, FLUSH THE PIPELINE FROM HERE BACK AND RESET THE PC
        if(collide && lastLoadOrNull.isReady()){
            //flushhh
            pc.set(lastLoadOrNull.getPcVal());
            shouldFlush = true;
            flushFrom(lastLoadOrNull.getId());
        }else if(collide){ // OTHERWISE, JUST UPDATE THE VALUE OF THE LOAD
            lastLoadOrNull.setValue(op.getResult());
            lastLoadOrNull.readyUp(); //make sure its ready
        }
    }

    @Override
    public void accept(Op.BrLZ op) {
        boolean flag = currentCommit.getValue() == BranchUnit.TAKEN;
        if(flag != Unit.STATIC_PREDICT_BR_TAKEN){
            if(flag){
                pc.set(op.getImVal());
            }else{
                pc.set(op.getResult()); // untaken
            }
            shouldFlush = true;
            shouldFlushWhere = currentCommit.getId() + 1;
            flushFrom(currentCommit.getId() + 1);
//            flushAt = currentRobEntry + 1; //after the current rob entry because we need to maintain program order
        }
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.JpLZ op) {
        boolean flag = currentCommit.getValue() == BranchUnit.TAKEN;
        if(flag != Unit.STATIC_PREDICT_BR_TAKEN){
            if(flag){
                pc.set(op.getResult() + op.getImVal());
            }else{
                pc.set(op.getResult()); // untaken
            }
            shouldFlush = true;
            shouldFlushWhere = currentCommit.getId() + 1;
            flushFrom(currentCommit.getId() + 1);
        }
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Br op) {
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Jp op) {
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.No op) {

    }
}
