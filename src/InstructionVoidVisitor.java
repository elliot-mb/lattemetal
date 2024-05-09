
public interface InstructionVoidVisitor {

    public void accept(Op.Add op);

    public void accept(Op.AddI op);

    public void accept(Op.Mul op);

    public void accept(Op.MulI op);

    public void accept(Op.Cmp op);

    public void accept(Op.Ld op);

    public void accept(Op.LdC op);

    public void accept(Op.LdI op);

    public void accept(Op.St op);

    public void accept(Op.StI op);

    public void accept(Op.BrLZ op);

    public void accept(Op.JpLZ op);

    public void accept(Op.Br op);

    public void accept(Op.Jp op);

    public void accept(Op.No op);
}
