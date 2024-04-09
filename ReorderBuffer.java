import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReorderBuffer implements InstructionVoidVisitor{

    public static final int NO_DEST = -1;
    private final CircluarQueue<ReorderEntry> buffer;
    private final Map<Integer, List<Integer>> cdb;
    private final RegisterFile rf;
    private PhysicalRegFile prf;
    private final Memory mem;

    private ReorderEntry currentCommit;
    private boolean prfSet = false;

    ReorderBuffer(int size, Map<Integer, List<Integer>> cdb, RegisterFile rf, Memory mem){
        this.buffer = new CircluarQueue<ReorderEntry>(size);
        this.cdb = cdb;
        this.rf = rf;
        this.currentCommit = null;
        this.mem = mem;
    }

    //avoids circular dep
    public void setPrf(PhysicalRegFile prf){
        this.prf = prf;
        prfSet = true;
    }

    public void add(ReorderEntry re){
        buffer.push(re);
    }

    public boolean isFull(){
        return buffer.isFull();
    }

    private String appendComma(String s){
        return s + ",";
    }

    public String toString(){
        String gaps = "";
        for(int i = 0; i < buffer.getSize() - buffer.getElementsIn(); i++ ){
            gaps += ",__";
        }
        return "[" + gaps + buffer.peekXs().stream().map(ReorderEntry::toString).map(this::appendComma).collect(Collectors.joining()) + "]";
    }

    private int locationOfEntryId(int id){
        List<ReorderEntry> re = buffer.peekXs();
        int index = -1;
        int i = buffer.getElementsIn() - 1;
        for(ReorderEntry r : re){
            if(r.id == id) {
                index = i;
                break;
            }
            i--;
        }
        return index;
    }

    public void setValOfEntry(int id, int val){
        List<ReorderEntry> re = buffer.peekXs();
        int index = locationOfEntryId(id);
        if(index == -1) throw new RuntimeException("setValOfEntry: there is no such entry with id '" + id + "' in reorder buffer");
        ReorderEntry rChange = re.get(index);
        rChange.setValue(val);
        buffer.setElement(index, rChange);
    }

    public Integer tailId(){
        if(buffer.isEmpty()) return null;
        List<ReorderEntry> re = buffer.peekXs();
        return re.get(re.size() - 1).id;
    }

    public int getValOfEntry(int id){
        List<ReorderEntry> re = buffer.peekXs();
        int index = locationOfEntryId(id);
        if(index == -1) throw new RuntimeException("getValOfEntry: there is no such entry with id '" + id + "' in reorder buffer");;
        return re.get(index).getValue();
    }

    public ReorderEntry getEntry(int id){
        List<ReorderEntry> re = buffer.peekXs();
        int index = locationOfEntryId(id);
        if(index == -1) throw new RuntimeException("getValOfEntry: there is no such entry with id '" + id + "' in reorder buffer");;
        return re.get(index);
    }

    public void clk(){
        if(!prfSet) throw new RuntimeException("ReorderBuffer.clk: prf not set");

        List<ReorderEntry> re = buffer.peekXs();
        // read off the cbd and into the robber
        for(ReorderEntry r: re){
            if(r.isReady()) System.out.println("entry " + r.getId() + " is ready!");
            if(cdb.containsKey(r.getId())){
                r.setValue(cdb.get(r.getId()).get(0)); //read from the map if the id matches this rese entry (value is already in the entry before we commit!)
            }
        }

        if(!re.isEmpty()){
            ReorderEntry willPop = re.get(re.size() - 1);
            if(willPop.isReady()){
                //visitation to distinguish between stores, common instructions, and branches (incorrect)
                currentCommit = buffer.pop(); //accessible in visitor
                Instruction poppedOp = currentCommit.getOp();
                poppedOp.visit(this);
            }
        }
        //commit stage! (start with max one instruction per clock cycle)
        currentCommit = null;
    }

    @Override
    public void accept(Op.Add op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.AddI op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
        System.out.println("remove id " + currentCommit.getId());
    }

    @Override
    public void accept(Op.Mul op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.MulI op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Cmp op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.Ld op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.LdC op) {
        rf.setReg(op.getRd(), currentCommit.getValue());
        prf.regValIsReady(op.getRd());
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.St op) {
        mem.set(currentCommit.getValue(), op.getResult()); //addr gets stored in result in the LSU!
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.BrLZ op) {
        cdb.remove(currentCommit.getId());
    }

    @Override
    public void accept(Op.JpLZ op) {
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
