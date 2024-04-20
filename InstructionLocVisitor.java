import java.util.List;

public interface InstructionLocVisitor {

    public List<Integer> accept(Op.Add op);

    public List<Integer> accept(Op.AddI op);

    public List<Integer> accept(Op.Mul op);

    public List<Integer> accept(Op.MulI op);

    public List<Integer> accept(Op.Cmp op);

    public List<Integer> accept(Op.Ld op);

    public List<Integer> accept(Op.LdC op);

    public List<Integer> accept(Op.St op);

    public List<Integer> accept(Op.BrLZ op);

    public List<Integer> accept(Op.JpLZ op);

    public List<Integer> accept(Op.Br op);

    public List<Integer> accept(Op.Jp op);

    public List<Integer> accept(Op.No op);
}
