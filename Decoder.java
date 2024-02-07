public class Decoder implements InstructionVoidVisitor{

    private final RegisterFile rf;
    private Instruction currentInstruction;

    Decoder(RegisterFile rf){
        this.rf = rf;
    }

    public Instruction decode(Instruction op){
        currentInstruction = null;
        op.visit(this);
        return currentInstruction;
    }

    @Override
    public void accept(Op.Add op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.AddI op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.Mul op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.MulI op) {
        //op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.Cmp op) {
        op.setRsVal(rf.getReg(op.getRs()));
        op.setRtVal(rf.getReg(op.getRt()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRsVal(rf.getReg(op.getRs()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.LdC op) {
        currentInstruction = op;
    }

    @Override
    public void accept(Op.St op) {
        op.setRdVal(rf.getReg(op.getRd()));
        op.setRsVal(rf.getReg(op.getRs()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.BrLZ op) {
        op.setRdVal(rf.getReg(op.getRd()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.JpLZ op) {
        op.setRdVal(rf.getReg(op.getRd()));
        currentInstruction = op;
    }

    @Override
    public void accept(Op.Br op) {
        currentInstruction = op;
    }

    @Override
    public void accept(Op.Jp op) {
        currentInstruction = op;
    }
}
