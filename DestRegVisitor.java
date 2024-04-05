import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DestRegVisitor implements InstructionRegVisitor{

    private static final List<RegisterName> NO_DEST = new ArrayList<RegisterName>();

    @Override
    public List<RegisterName> accept(Op.Add op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.AddI op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.Mul op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.MulI op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.Cmp op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.Ld op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.LdC op) {
        return List.of(op.getRd());
    }

    @Override
    public List<RegisterName> accept(Op.St op) {
        return NO_DEST;
    }

    @Override
    public List<RegisterName> accept(Op.BrLZ op) {
        return NO_DEST;
    }

    @Override
    public List<RegisterName> accept(Op.JpLZ op) {
        return NO_DEST;
    }

    @Override
    public List<RegisterName> accept(Op.Br op) {
        return NO_DEST;
    }

    @Override
    public List<RegisterName> accept(Op.Jp op) {
        return NO_DEST;
    }

    @Override
    public List<RegisterName> accept(Op.No op) {
        return NO_DEST;
    }
}
