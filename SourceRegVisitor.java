import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceRegVisitor implements InstructionRegVisitor {

    private static final List<RegisterName> NO_SRCS = new ArrayList<RegisterName>();

    @Override
    public List<RegisterName> accept(Op.Add op) {
        return Arrays.asList(op.getRs(), op.getRt());
    }

    @Override
    public List<RegisterName> accept(Op.AddI op) {
        return Arrays.asList(op.getRs());
    }

    @Override
    public List<RegisterName> accept(Op.Mul op) {
        return Arrays.asList(op.getRs(), op.getRt());
    }

    @Override
    public List<RegisterName> accept(Op.MulI op) {
        return Arrays.asList(op.getRs());
    }

    @Override
    public List<RegisterName> accept(Op.Cmp op) {
        return Arrays.asList(op.getRs(), op.getRt());
    }

    @Override
    public List<RegisterName> accept(Op.Ld op) {
        return Arrays.asList(op.getRs());
    }

    @Override
    public List<RegisterName> accept(Op.LdC op) {
        return NO_SRCS;
    }

    @Override
    public List<RegisterName> accept(Op.St op) {
        return Arrays.asList(op.getRd(), op.getRs());
    }

    @Override
    public List<RegisterName> accept(Op.BrLZ op) {
        return Arrays.asList(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.JpLZ op) {
        return Arrays.asList(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.Br op) {
        return NO_SRCS;
    }

    @Override
    public List<RegisterName> accept(Op.Jp op) {
        return NO_SRCS;
    }

    @Override
    public List<RegisterName> accept(Op.No op) {
        return NO_SRCS;
    }
}
