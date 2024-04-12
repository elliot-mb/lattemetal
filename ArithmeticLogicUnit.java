import java.util.*;

public class ArithmeticLogicUnit extends Unit {

    private final Map<Integer, List<Integer>> cdb;
    private final List<ReservationStation> rss;
    private int currentRs;
    private final int baseRs;
    private final RegisterFile rf;

    private final ReorderBuffer rob;

    private final PhysicalRegFile prf;

    private int currentRobEntry;

    ArithmeticLogicUnit(Map<Integer, List<Integer>> cdb, ReorderBuffer rob, List<ReservationStation> rs, RegisterFile rf, PhysicalRegFile prf, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.currentOp = null;
        this.currentRs = 0;
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
                TubeLike in = ins[selectionPriority()];
                PipelineEntry e = in.pull();
                pcVal = e.getPcVal();
                flag = e.getFlag();
                rs.set(e, prf, rf); //entry is the rob tab/entry
            }
        }
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
                currentOp = rob.getEntry(currentRobEntry).getOp(); //get it right from the rob so its the same reference! (we need to modify op fields...)
                currentOp.rst();
                currentRs = index; //should only be reset after we finish processing stuff
            }
        }

        if(currentOp != null && !currentOp.isDone()) {
            currentOp.decr();
        }
    }

    @Override
    protected boolean isDone(){
        boolean anyBusy = false;
        for(ReservationStation rs : rss){
            anyBusy = rs.isBusy() || anyBusy;
        }
        return !anyBusy;
    }

    @Override
    protected boolean attemptToRead(){
        return true; //the case where we cannot read is handled by this readoffpipeline
    }

    @Override
    protected boolean isUnfinished() {
        return (currentOp == null || !currentOp.isDone()); //if we arent done with the inner up, and its not blank
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
        rss.get(currentRs).flush();
    }

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        int res = rss.get(currentRs).getvJ() + rss.get(currentRs).getvK();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);

    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        int res = rss.get(currentRs).getvJ() + op.getImVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        int res = rss.get(currentRs).getvJ() * rss.get(currentRs).getvK();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        int res = rss.get(currentRs).getvJ() * op.getImVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
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
        op.setRdVal(cmpResult);
        //rob.setValOfEntry(currentRobEntry, cmpResult);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
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
        System.out.println(op);
        System.out.println(op);
        System.out.println(op);
        System.out.println(op);
        System.out.println(op);
        System.out.println(op);System.out.println(op);System.out.println(op);System.out.println(op);System.out.println(op);System.out.println(op);






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
        return rss.get(0).toString() + "," + rss.get(1).toString() + ":EX";
    }
}
