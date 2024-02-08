public class OpCoder implements InstructionCodeVisitor { //just returns the opcode (identity function?)


    @Override
    public OpCode accept(Op.Add op) {
        return OpCode.add;
    }

    @Override
    public OpCode accept(Op.AddI op) {
        return OpCode.addi;
    }

    @Override
    public OpCode accept(Op.Mul op) {
        return OpCode.mul;
    }

    @Override
    public OpCode accept(Op.MulI op) {
        return OpCode.muli;
    }

    @Override
    public OpCode accept(Op.Cmp op) {
        return OpCode.cmp;
    }

    @Override
    public OpCode accept(Op.Ld op) {
        return OpCode.ld;
    }

    @Override
    public OpCode accept(Op.LdC op) {
        return OpCode.ldc;
    }

    @Override
    public OpCode accept(Op.St op) {
        return OpCode.st;
    }

    @Override
    public OpCode accept(Op.BrLZ op) {
        return OpCode.brlz;
    }

    @Override
    public OpCode accept(Op.JpLZ op) {
        return OpCode.jplz;
    }

    @Override
    public OpCode accept(Op.Br op) {
        return OpCode.br;
    }

    @Override
    public OpCode accept(Op.Jp op) {
        return OpCode.jp;
    }
}
