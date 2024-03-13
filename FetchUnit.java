

public class FetchUnit extends Unit {

    private static final int FETCH_LATENCY = 1;
    
    private Integer whatBranchID = null; //

    private final InstructionCache ic;

    private final Durate counter = new Durate(FETCH_LATENCY);

    FetchUnit(InstructionCache ic, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.ic = ic;
    }

    @Override
    protected void procInstruction() {
        counter.decr();
    }

    @Override
    protected void readOffPipeline(){
        pcVal = last.getPcVal();
        currentOp = last.pull();
        counter.rst();
        if(Utils.isBranch(currentOp)){
            whatBranchID = currentOp.getId();
        }else{
            whatBranchID = null;
        }

    }

    @Override
    protected void writeOnPipeline() {
        currentOp = ic.getInstruction(pcVal); //fetching happens finally in writeOnPipeline
        next.setPcVal(pcVal + 1); //INCREMENT HERE (adder like in the unit diagram!)
        next.push(currentOp);
    }

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

    }

    @Override
    public void accept(Op.AddI op) {

    }

    @Override
    public void accept(Op.Mul op) {

    }

    @Override
    public void accept(Op.MulI op) {

    }

    @Override
    public void accept(Op.Cmp op) {

    }

    @Override
    public void accept(Op.Ld op) {

    }

    @Override
    public void accept(Op.LdC op) {

    }

    @Override
    public void accept(Op.St op) {

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
}
