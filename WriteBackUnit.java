public class WriteBackUnit extends Unit{

    private static final int REG_LATENCY = 1;
    private final RegisterFile rf;

    private final Durate counter = new Durate(REG_LATENCY);

    WriteBackUnit(RegisterFile rf, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.rf = rf;
    }

    @Override
    protected void readOffPipeline(){
        currentOp = last.pull();
        counter.rst();
    }

//    @Override
//    protected void writeOnPipeline(){
//        next.push();
//    }

    @Override
    protected void procInstruction() {
        counter.decr();
    }

    @Override
    protected boolean isUnfinished() {
        return !counter.isDone();
    }

    // all the below methods write back to the registers correctly
    @Override
    public void accept(Op.Add op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.AddI op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Mul op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.MulI op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Cmp op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Ld op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.LdC op) {
        rf.setReg(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.St op) {
        //nothing
    }

    @Override
    public void accept(Op.BrLZ op) {
        //nothing
    }

    @Override
    public void accept(Op.JpLZ op) {
        //nothing
    }

    @Override
    public void accept(Op.Br op) {
        //nothing
    }

    @Override
    public void accept(Op.Jp op) {
        //nothing
    }

}
