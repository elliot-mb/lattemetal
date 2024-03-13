public class DecodeUnit extends Unit{

    private final RegisterFile rf;

    private boolean hasRun;

    DecodeUnit(RegisterFile rf, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.rf = rf;
    }

    @Override
    public void readOffPipeline(){
        hasRun = false;
        pcVal = last.getPcVal();
        flag = last.isFlag();
        currentOp = last.pull();
    }

    @Override
    protected void procInstruction() {
        hasRun = true;
    }

    @Override
    protected boolean isUnfinished() {
        return !hasRun;
    }

    @Override
    public void flush() {
        super.flush();
        hasRun = false;
    }

    @Override
    public void accept(Op.Add op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentOp = op;
    }

    @Override
    public void accept(Op.AddI op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentOp = op;
    }

    @Override
    public void accept(Op.Mul op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentOp = op;
    }

    @Override
    public void accept(Op.MulI op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentOp = op;
    }

    @Override
    public void accept(Op.Cmp op) {
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentOp = op;
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRsVal(rf.getReg(op.getRs()));
        currentOp = op;
    }

    @Override
    public void accept(Op.LdC op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.St op) {
        op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentOp = op;
    }

    @Override
    public void accept(Op.BrLZ op) {
        op.setRdVal(rf.getReg(op.getRd()));
        currentOp = op;
    }

    @Override
    public void accept(Op.JpLZ op) {
        op.setRdVal(rf.getReg(op.getRd()));
        currentOp = op;
    }

    @Override
    public void accept(Op.Br op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Jp op) {
        currentOp = op;
    }


}
