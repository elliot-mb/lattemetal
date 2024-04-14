

public class FetchUnit extends Unit {

    private static final int FETCH_LATENCY = 1;
    
    private Integer whatBranchID = null; //

    private final InstructionCache ic;

    private final ProgramCounter pc;

    private final Durate counter = new Durate(FETCH_LATENCY);

    FetchUnit(InstructionCache ic, ProgramCounter pc, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.ic = ic;
        this.pc = pc;
    }

    @Override
    protected void procInstruction() {
        if(pc.getCount() == ic.numInstrs()) {
            currentOp = Utils.opFactory.new No();
            counter.finish();
            return;
        }
        counter.decr();
        currentOp = ic.getInstruction(pc.getCount());
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        counter.rst();
        pc.set(pcVal + 1);
    }
//
//    @Override
//    protected void writeOnPipeline() {
//        //incr then write the incremented value on pipeline
//        currentOp.visit(this);
//        super.writeOnPipeline();
//    }

    @Override
    public void flush(){
        super.flush();
        whatBranchID = null;
    }
//
//    // returns null if this is not a branch
//    public Integer getBranch(){
//        return isBranch ? whatBranchID : null;
//    }

//    private boolean isBranch(){
//        return whatBranchID != null;
//    }
    
    @Override
    protected boolean isUnfinished() {
        return !counter.isDone();// && !isBranch();
    }


    //nothing happens in visitation because fetching happens in procInstruction
    @Override
    public void accept(Op.Add op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.AddI op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.Mul op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.MulI op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.Cmp op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.Ld op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.LdC op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.St op) {
        pc.set(pcVal + 1);
    }

    @Override
    public void accept(Op.BrLZ op) {
        pc.set(pcVal + 1);
        if(STATIC_PREDICT_BR_TAKEN){
            //next.setPcVal(op.getImVal());
            pc.set(op.getImVal()); //static prediciton
        }
        op.setResult(pcVal);
    }

    @Override
    public void accept(Op.JpLZ op) {
        pc.set(pcVal + 1);
        if(STATIC_PREDICT_BR_TAKEN){
            //next.setPcVal(op.getImVal());
            pc.set(pcVal + 1 + op.getImVal());
        }
        op.setResult(pcVal);
    }

    @Override
    public void accept(Op.Br op) {
//        next.setPcVal(op.getImVal());
        pc.set(op.getImVal());
        op.setResult(pcVal);
    }

    @Override
    public void accept(Op.Jp op) {
//        next.setPcVal(op.getImVal());
        pc.set(pcVal + 1 + op.getImVal());
        op.setResult(pcVal);
    }

    protected String showUnit(){
        return "FE";
    }
}
