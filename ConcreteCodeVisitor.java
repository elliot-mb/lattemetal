

public class ConcreteCodeVisitor implements InstructionCodeVisitor { //just returns the opcode (identity function?)


    @Override
    public Opcode accept(Op.Add op) {
        return Opcode.add;
    }

    @Override
    public Opcode accept(Op.AddI op) {
        return Opcode.addi;
    }

    @Override
    public Opcode accept(Op.Mul op) {
        return Opcode.mul;
    }

    @Override
    public Opcode accept(Op.MulI op) {
        return Opcode.muli;
    }

    @Override
    public Opcode accept(Op.Cmp op) {
        return Opcode.cmp;
    }

    @Override
    public Opcode accept(Op.Ld op) {
        return Opcode.ld;
    }

    @Override
    public Opcode accept(Op.LdC op) {
        return Opcode.ldc;
    }

    @Override
    public Opcode accept(Op.St op) {
        return Opcode.st;
    }

    @Override
    public Opcode accept(Op.BrLZ op) {
        return Opcode.brlz;
    }

    @Override
    public Opcode accept(Op.JpLZ op) {
        return Opcode.jplz;
    }

    @Override
    public Opcode accept(Op.Br op) {
        return Opcode.br;
    }

    @Override
    public Opcode accept(Op.Jp op) {
        return Opcode.jp;
    }

    @Override
    public Opcode accept(Op.No op) { return Opcode.no; }
}
