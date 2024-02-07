public class WriteBackUnit implements InstructionVoidVisitor{

    private final RegisterFile rf;

    WriteBackUnit(RegisterFile rf){
        this.rf = rf;
    }

    public void go(Instruction op){
        op.visit(this);
    }

    // all the below methods write back to the registers correctly
    @Override
    public void accept(Op.Add op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.AddI op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.Mul op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.MulI op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.Cmp op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.Ld op) {
        rf.setReg(op.getRd(), op.getRdVal());
    }

    @Override
    public void accept(Op.LdC op) {
        rf.setReg(op.getRd(), op.getRdVal());
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
