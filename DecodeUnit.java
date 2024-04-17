

public class DecodeUnit extends Unit{

    private final RegisterFile rf;

    private boolean hasRun;

    DecodeUnit(RegisterFile rf, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.rf = rf;
    }

    @Override
    protected void procInstruction() {
        //doesnt take any time to proc
    }

    @Override
    protected boolean isUnfinished() {
        return false;
    }

    @Override
    public void flush() {
        super.flush();
        hasRun = false;
    }

    @Override
    public void accept(Op.Add op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.AddI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Mul op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.MulI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Cmp op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Ld op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.LdC op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.St op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.BrLZ op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.JpLZ op) {
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

    protected String showUnit(){
        return "DE";
    }
}
