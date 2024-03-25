
public class WriteBackUnit extends Unit{

    private static final int REG_LATENCY = 1;
    private final RegisterFile rf;
    private final Scoreboard sb;
    private final Durate counter = new Durate(REG_LATENCY);

    WriteBackUnit(RegisterFile rf, Scoreboard sb, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.rf = rf;
        this.sb = sb;
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
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

    private void setRdToRes(RegisterName rd, int result){
        rf.setReg(rd, result);
        sb.validateReg(rd);
    }

    // all the below methods write back to the registers correctly
    @Override
    public void accept(Op.Add op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.AddI op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Mul op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.MulI op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Cmp op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Ld op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.LdC op) {
        setRdToRes(op.getRd(), op.getResult());
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
