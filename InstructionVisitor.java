public interface InstructionVisitor {

    public Opcode accept(Op.Add op);

    public Opcode accept(Op.AddI op);

    public Opcode accept(Op.Mul op);

    public Opcode accept(Op.MulI op);

    public Opcode accept(Op.Cmp op);

    public Opcode accept(Op.Ld op);

    public Opcode accept(Op.LdC op);

    public Opcode accept(Op.St op);

    public Opcode accept(Op.BrLZ op);

    public Opcode accept(Op.JpLZ op);

    public Opcode accept(Op.Br op);

    public Opcode accept(Op.Jp op);
}
