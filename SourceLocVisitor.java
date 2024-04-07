import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceLocVisitor implements InstructionLocVisitor {

    private static final List<Integer> NO_SRCS = new ArrayList<Integer>();

    @Override
    public List<Integer> accept(Op.Add op) {
        return Arrays.asList(op.getRs().ordinal(), op.getRt().ordinal());
    }

    @Override
    public List<Integer> accept(Op.AddI op) {
        return Arrays.asList(op.getRs().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Mul op) {
        return Arrays.asList(op.getRs().ordinal(), op.getRt().ordinal());
    }

    @Override
    public List<Integer> accept(Op.MulI op) {
        return Arrays.asList(op.getRs().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Cmp op) {
        return Arrays.asList(op.getRs().ordinal(), op.getRt().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Ld op) {
        return Arrays.asList(op.getRs().ordinal());
    }

    @Override
    public List<Integer> accept(Op.LdC op) {
        return NO_SRCS;
    }

    @Override
    public List<Integer> accept(Op.St op) {
        return Arrays.asList(op.getRd().ordinal(), op.getRs().ordinal());
    }

    @Override
    public List<Integer> accept(Op.BrLZ op) {
        return Arrays.asList(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.JpLZ op) {
        return Arrays.asList(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Br op) {
        return NO_SRCS;
    }

    @Override
    public List<Integer> accept(Op.Jp op) {
        return NO_SRCS;
    }

    @Override
    public List<Integer> accept(Op.No op) {
        return NO_SRCS;
    }
}
