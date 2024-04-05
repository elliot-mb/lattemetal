import java.util.List;

public interface InstructionRegVisitor {

    public List<RegisterName> accept(Op.Add op);

    public List<RegisterName> accept(Op.AddI op);

    public List<RegisterName> accept(Op.Mul op);

    public List<RegisterName> accept(Op.MulI op);

    public List<RegisterName> accept(Op.Cmp op);

    public List<RegisterName> accept(Op.Ld op);

    public List<RegisterName> accept(Op.LdC op);

    public List<RegisterName> accept(Op.St op);

    public List<RegisterName> accept(Op.BrLZ op);

    public List<RegisterName> accept(Op.JpLZ op);

    public List<RegisterName> accept(Op.Br op);

    public List<RegisterName> accept(Op.Jp op);

    public List<RegisterName> accept(Op.No op);
}
