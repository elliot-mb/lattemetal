public class LoadStoreUnit implements InstructionVoidVisitor{

    private final Memory mem;

    LoadStoreUnit(Memory mem){
        this.mem = mem;
    }

    public void go(Instruction op){
        op.visit(this);
    }

    @Override
    public void accept(Op.Add op) {
        //nothing
    }

    @Override
    public void accept(Op.AddI op) {
        //nothing
    }

    @Override
    public void accept(Op.Mul op) {
        //nothing
    }

    @Override
    public void accept(Op.MulI op) {
        //nothing
    }

    @Override
    public void accept(Op.Cmp op) {
        //nothing
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRdVal(mem.read(op.getRdVal()));
    }

    @Override
    public void accept(Op.LdC op) {
        op.setRdVal(mem.read(op.getImVal()));
    }

    @Override
    public void accept(Op.St op) {
        mem.set(op.getRdVal(), op.getRsVal());
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
