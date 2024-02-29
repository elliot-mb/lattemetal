

public class ArithmeticLogicUnit extends Unit {

    private boolean branchTaken = false;

    ArithmeticLogicUnit(PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.currentOp = null;
    }

    @Override
    protected void readOffPipeline() {
        currentOp = last.pull();
    }

    @Override
    protected void writeOnPipeline() {
        next.push(currentOp);
        next.setFlag(branchTaken);
    }

    @Override
    protected void procInstruction() {
        currentOp.clk();
    }

    @Override
    protected boolean isUnfinished() {
        return !isDone() && !currentOp.isDone(); //if we arent done with the inner up, and its not blank
    }

    //    @Override
//    public void clk() {
//        //if we have finished processing this instruction but cant pull from last, we stall one cycle
//        if(isDone() && !last.canPull()) return; //stall a clock cycle
//        if(isDone()) currentOp = last.pull(); //dont re-copy if we are mid-processing
//        if(!isDone() && !currentOp.isDone()) {
//            currentOp.clk();
//            return; //use up a clock cycle
//        }
//        currentOp.visit(this); //process operation
//        if(!next.canPush()) return; //stall a clock cycle if we cant push the result
//
//        next.push(currentOp);
//        next.setFlag(branchTaken);
//
//        currentOp = null; //empty out our intermediate storage to accept the next one
//    }

    //should be done just if op is 'done'
    public Instruction requestOp(){
        return currentOp;
    }

    @Override
    public boolean isDone() {
        return currentOp == null;
    }

    //visitation

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        op.setResult(op.getRsVal() + op.getRtVal());
    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        op.setResult(op.getRsVal() + op.getImVal());
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        op.setResult(op.getRsVal() * op.getRtVal());
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        op.setResult(op.getRsVal() * op.getImVal());
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
    }

    @Override
    public void accept(Op.Ld op) {
        op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
    }

    @Override
    public void accept(Op.LdC op) {
        op.setResult(op.getImVal());
    }

    @Override
    public void accept(Op.St op) {
        //store register value at offset address
        op.setResult(op.getRsVal() + op.getImVal()); //calculate offset, store it in RS
    }

    @Override
    public void accept(Op.BrLZ op) {
        //pc.set(op.getImVal());
        //pc.incr();
        branchTaken = op.getRdVal() <= 0;
        op.setResult(op.getImVal());
    }

    @Override
    public void accept(Op.JpLZ op) {
        branchTaken = op.getRdVal() <= 0;
        op.setResult(last.getPc() + op.getImVal());
    }

    @Override
    public void accept(Op.Br op) {
        op.setResult(op.getImVal());
    }

    @Override
    public void accept(Op.Jp op) {
        op.setResult(last.getPc() + op.getImVal());
    }

}
