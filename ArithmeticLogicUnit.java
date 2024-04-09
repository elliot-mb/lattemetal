import java.util.*;

public class ArithmeticLogicUnit extends Unit {

    private final Map<Integer, List<Integer>> cdb;
    private final List<ReservationStation> rss;
    private int currentRs;
    private final int baseRs;
    private final RegisterFile rf;

    private final PhysicalRegFile prf;

    private int currentRobEntry;

    ArithmeticLogicUnit(Map<Integer, List<Integer>> cdb, List<ReservationStation> rs, RegisterFile rf, PhysicalRegFile prf, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.currentOp = null;
        this.currentRs = 0;
        this.baseRs = rs.get(0).getId();
        this.rss = rs;
        this.rf = rf;
        this.cdb = cdb;
        this.prf = prf;
    }

    @Override
    protected void readOffPipeline(){
        for(ReservationStation rs : rss){
            if(canPullOffActiveIn() && !rs.isBusy() && ins[selectionPriority()].canPull()){ //can read multiple times off the queue
                PipelineRegister in = ins[selectionPriority()];
                PipelineEntry e = in.pull();
                pcVal = e.getPcVal();
                flag = e.getFlag();
                rs.set(e, prf, rf, e.getEntry().get(0)); //entry is the rob tab/entry
            }
        }
    }

    @Override
    protected void procInstruction() {
        for(ReservationStation rs : rss){
            if(rs.isBusy()) rs.update(); //dont update those with no instruction inside
            //System.out.println("UPDATED AND NOW " + rs.isReady());
            if(currentOp == null && rs.isBusy() && rs.isReady()){
                currentRobEntry = rs.robEntry;
                currentOp = rs.getOp();
                currentOp.rst();
                currentRs = rs.getId() - baseRs; //should only be reset after we finish processing stuff
            }
        }
        if(currentOp != null && !currentOp.isDone()) currentOp.decr();
    }

    @Override
    protected boolean isDone(){
        boolean allBusy = true;
        for(ReservationStation rs : rss){
            allBusy = rs.isBusy() && allBusy;
        }
        return !allBusy;
    }

    @Override
    protected boolean isUnfinished() {
        return currentOp == null || !currentOp.isDone(); //if we arent done with the inner up, and its not blank
    }

    @Override
    public void flush(){
        super.flush();
        for(ReservationStation r : rss){
            r.flush();
        }
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){
        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    @Override
    protected void writeOnPipeline(){
        super.writeOnPipeline();
//        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
        rss.get(currentRs).setIsBusy(false);
    }

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        op.setResult(rss.get(currentRs).getvJ() + rss.get(currentRs).getvK());
        op.setRdVal(op.getResult());
        //prf.destValIsReady(currentOp.getRd().ordinal()); moved to rob

    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        op.setResult(rss.get(currentRs).getvJ() + op.getImVal());
        op.setRdVal(op.getResult());
//prf.destValIsReady(currentOp.getRd().ordinal()); moved to rob
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        op.setResult(rss.get(currentRs).getvJ() * rss.get(currentRs).getvK());
        op.setRdVal(op.getResult());
//prf.destValIsReady(currentOp.getRd().ordinal()); moved to rob
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        op.setResult(rss.get(currentRs).getvJ() * op.getImVal());
        op.setRdVal(op.getResult());
//prf.destValIsReady(currentOp.getRd().ordinal()); moved to rob
    }

    @Override
    public void accept(Op.Cmp op) {
        // modify rd value
        final int a = rss.get(currentRs).getvJ();
        final int b = rss.get(currentRs).getvK();
        int cmpResult;
        if(a < b) cmpResult = -1;
        else if(a == b) cmpResult = 0;
        else cmpResult = 1;
        op.setResult(cmpResult);
        op.setRdVal(op.getResult());
//prf.destValIsReady(currentOp.getRd().ordinal()); moved to rob
    }

    @Override
    public void accept(Op.Ld op) {
//        op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
//        fwd.setSlotReg(null); //doesnt correspond to a register because the result comes from the LSU
//        fwd.setSlot(currentOp.getResult());
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.LdC op) {
//        op.setResult(op.getImVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.St op) {
        //throw new RuntimeException("ArithmeticLogicUnit.accept: cannot accept a store instruction!");
        //  store register value at offset address
        //op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
        //fwd.setSlotReg(null); //doesnt correspond to a register
        throw new RuntimeException("ArithmeticLogicUnit.accept: store instruction not valid");

    }

    @Override
    public void accept(Op.BrLZ op) {
        //pc.set(op.getImVal());
        //pc.incr();
        flag = op.getRdVal() <= 0;
        op.setResult(op.getImVal()); //result is just set to imval

//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.JpLZ op) {
        flag = op.getRdVal() <= 0;
        op.setResult(pcVal + op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.Br op) {
        op.setResult(op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.Jp op) {
        op.setResult(pcVal + op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    protected String showUnit(){
        return (rss.get(0).isBusy() ? "" + rss.get(0).getOp().getId() : "_") + "," + (rss.get(1).isBusy() ? "" + rss.get(1).getOp().getId() : "_")  + "EX";
    }
}
