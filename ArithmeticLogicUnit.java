import java.util.*;

public class ArithmeticLogicUnit extends Unit {

    private final Map<Integer, List<Integer>> cdb;

    private final RegisterFile rf;

    private final ReorderBuffer rob;

    private final PhysicalRegFile prf;

    private int currentRobEntry;

    ArithmeticLogicUnit(Map<Integer, List<Integer>> cdb, ReorderBuffer rob, RegisterFile rf, PhysicalRegFile prf, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.currentOp = null;
        this.rf = rf;
        this.cdb = cdb;
        this.prf = prf;
        this.rob = rob;
    }

    @Override
    protected void procInstruction() {

        if(currentOp != null && !currentOp.isDone()) {
            currentOp.decr();
        }
    }

    @Override
    protected boolean isUnfinished() {
        return (currentOp == null || !currentOp.isDone()); //if we arent done with the inner up, and its not blank
    }

    @Override
    public void flush(){
        super.flush();
    }

    @Override
    protected void readOffPipeline(){
        PipeLike in = ins[selectionPriority()];
        PipelineEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
        currentRobEntry = e.getEntry();
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){
        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        int res = op.getRdVal() + op.getRtVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);

    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        int res = op.getRsVal() + op.getImVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        int res = op.getRsVal() * op.getRtVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        int res = op.getRsVal() * op.getImVal();
        op.setResult(res);
        op.setRdVal(res);
        //rob.setValOfEntry(currentRobEntry, res);
        //prf.pointRegAtRobEntry(op.getRd(), currentRobEntry);
    }

    @Override
    public void accept(Op.Cmp op) {
        // modify rd value
        final int a = op.getRsVal();
        final int b = op.getRtVal();
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
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.JpLZ op) {
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");

    }

    @Override
    public void accept(Op.Br op) {
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");

    }

    @Override
    public void accept(Op.Jp op) {
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");

    }

    protected String showUnit(){
        return "EX";
    }
}
