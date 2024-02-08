public interface InstructionCodeVisitor {

    public OpCode accept(Op.Add op);

    public OpCode accept(Op.AddI op);

    public OpCode accept(Op.Mul op);

    public OpCode accept(Op.MulI op);

    public OpCode accept(Op.Cmp op);

    public OpCode accept(Op.Ld op);

    public OpCode accept(Op.LdC op);

    public OpCode accept(Op.St op);

    public OpCode accept(Op.BrLZ op);

    public OpCode accept(Op.JpLZ op);

    public OpCode accept(Op.Br op);

    public OpCode accept(Op.Jp op);
}
