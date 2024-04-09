import java.util.List;
import java.util.Map;

public class LoadStoreUnit extends Unit{

    private static final int L1_LATENCY = 3;
    private static final int NOP_LATENCY = 1;
    private final Memory mem;

    private final ReorderBuffer rob;
    private int currentRs;
    private final int baseRs;

    private final RegisterFile rf;

    private final Map<Integer, List<Integer>> cdb;
    private final List<ReservationStation> rss;

    private int currentRobEntry;
    private final PhysicalRegFile prf;
    private Durate counter = new Durate(L1_LATENCY);

    LoadStoreUnit(Memory mem, List<ReservationStation> rs, RegisterFile rf, PhysicalRegFile prf, Map<Integer, List<Integer>> cdb, ReorderBuffer rob, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.mem = mem;
        this.baseRs = rs.get(0).getId();
        this.rss = rs;
        this.rf = rf;
        this.cdb = cdb;
        this.prf = prf;
        this.rob = rob;
    }

    @Override
    protected void readOffPipeline(){
        for(ReservationStation rs : rss){
            if(canPullOffActiveIn() && !rs.isBusy() && ins[selectionPriority()].canPull()){ //can read multiple times off the queue
                PipelineRegister in = ins[selectionPriority()];
                PipelineEntry e = in.pull();
                pcVal = e.getPcVal();
                flag = e.getFlag();
                rs.set(e, prf, rf, e.getEntry());
            }
        }
//        super.readOffPipeline();
//        counter.rst();
//        counterNop.rst();
    }

    @Override
    protected void procInstruction() {
        for(ReservationStation rs : rss){
            if(rs.isBusy()) rs.update(); //dont update those with no instruction inside
        }
        if(currentOp == null){
            int oldestInstructionId = Integer.MAX_VALUE;
            int i = 0;
            int index = -1;
            for(ReservationStation rs : rss){
                if(rs.isBusy() && rs.isReady() && oldestInstructionId > rs.getOp().getId()) {
                    index = i;
                    oldestInstructionId = rs.getOp().getId();
                }
                i++;
            }
            if(index != -1) {
                ReservationStation rs = rss.get(index);
                currentRobEntry = rs.robEntry;
                currentOp = rs.getOp();
                counter.rst();
                currentRs = index; //should only be reset after we finish processing stuff
            }
        }

        if(currentOp != null && !counter.isDone()) {
            counter.decr();
        }
    }

    @Override
    protected boolean isUnfinished() {
        return currentOp == null || !counter.isDone();//(!counter.isDone() && Utils.isLoadStore(currentOp)) || !counterNop.isDone(); //if its not a load/store we're finished
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){
        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    @Override
    protected void writeOnPipeline(){
        super.writeOnPipeline();
        rss.get(currentRs).setIsBusy(false);
    }

    @Override
    protected boolean isDone(){ //it is done when not-any are busy (none are busy)
        boolean anyBusy = false;
        for(ReservationStation rs : rss){
            anyBusy = rs.isBusy() || anyBusy;
        }
        return !anyBusy;
    }
    //visitation

    @Override
    protected boolean attemptToRead(){
        return true; //the case where we cannot read is handled by this readoffpipeline
    }

    @Override
    public void accept(Op.Add op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.AddI op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Mul op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.MulI op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Cmp op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Ld op) {
        int addr = rss.get(currentRs).getvJ() + op.getImVal(); //copied from old ALU
        int res;
        if(prf.isMemValUnmapped(addr)){ //from mem
            res = mem.get(addr);
        }else{// from rob
            res = rob.getValOfEntry(prf.whereMemInRob(addr));
        }
        op.setResult(res);
        op.setRdVal(res);
//        rob.setValOfEntry(currentRobEntry, res);
    }

    @Override
    public void accept(Op.LdC op) {
        int addr = op.getImVal(); //either we get from mem or get from the rob
        int res;
        if(prf.isMemValUnmapped(addr)){ //from mem
            res = mem.get(addr);
        }else{// from rob
            res = rob.getValOfEntry(prf.whereMemInRob(addr));
        }
        op.setResult(res); //this must be ready otherwise the reservation station wouldnt have released the instruction
        op.setRdVal(res);
//        rob.setValOfEntry(currentRobEntry, res);
    }

    @Override
    public void accept(Op.St op) {
        int addr = rss.get(currentRs).getvJ() + op.getImVal();
        op.setResult(addr); //copied from old ALU
        int res;
        if(prf.isMemValUnmapped(addr)){ //from mem
            res = mem.get(addr);
            prf.pointMemAtRobEntry(addr, currentRobEntry);
        }else{// from rob
            res = rob.getValOfEntry(prf.whereMemInRob(addr));
        }
        cdb.put(currentRobEntry, List.of(res));
        //show the updated val in prf

        //this goes in write result now
//        if(prf.isRegValAtRobAndReady(op.getRd().ordinal())){
//            int resultEntryLoc = prf.whereInRob(op.getRd().ordinal());
//            rob.setValOfEntry(currentRobEntry, rob.getValOfEntry(resultEntryLoc)); //copy reg value from elsewhere in the rob to the result of the store
//        }
    }

    @Override
    public void accept(Op.BrLZ op) {

    }

    @Override
    public void accept(Op.JpLZ op) {

    }

    @Override
    public void accept(Op.Br op) {

    }

    @Override
    public void accept(Op.Jp op) {

    }

    protected String showUnit(){
        return (rss.get(0).isBusy() ? "" + rss.get(0).getOp().getId() : "_") + "," + (rss.get(1).isBusy() ? "" + rss.get(1).getOp().getId() : "_") + "LS";
    }
}
