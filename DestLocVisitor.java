import java.util.ArrayList;
import java.util.List;


public class DestLocVisitor implements InstructionLocVisitor {

    private static final List<Integer> NO_DEST = new ArrayList<Integer>();

    @Override
    public List<Integer> accept(Op.Add op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.AddI op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Mul op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.MulI op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Cmp op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.Ld op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.LdC op) {
        return List.of(op.getRd().ordinal());
    }

    @Override
    public List<Integer> accept(Op.St op) {
        return NO_DEST;
    }

    @Override
    public List<Integer> accept(Op.BrLZ op) {
        return NO_DEST;
    }

    @Override
    public List<Integer> accept(Op.JpLZ op) {
        return NO_DEST;
    }

    @Override
    public List<Integer> accept(Op.Br op) {
        return NO_DEST;
    }

    @Override
    public List<Integer> accept(Op.Jp op) {
        return NO_DEST;
    }

    @Override
    public List<Integer> accept(Op.No op) {
        return NO_DEST;
    }
}
