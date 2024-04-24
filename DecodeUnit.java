

public class DecodeUnit extends Unit{

    private final RegisterFile rf;

    public final CircluarQueue<RegisterName> physicalRegisters = new CircluarQueue<RegisterName>(Processor.PHYSICAL_REGISTER_COUNT); //added to each time we make an instruction
    public static CircluarQueue<Integer> physicalRobEntries = new CircluarQueue<Integer>(Processor.PHYSICAL_REGISTER_COUNT);

    DecodeUnit(RegisterFile rf, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.rf = rf;
    }

    @Override
    protected void procInstruction() {
        //doesnt take any time to proc
    }

    @Override
    public void flush(int fromRobEntry){
        super.flush(fromRobEntry);
        while(physicalRobEntries.peekHead() >= fromRobEntry) {
            physicalRobEntries.shift();
            physicalRegisters.shift();
        }
    }

    @Override
    protected boolean isUnfinished() {
        return physicalRegisters.getElementsIn() > physicalRegisters.getSize() - 3; //block when we run out of prf, at least three physical registers must be free
    }


    @Override
    public void accept(Op.Add op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        physicalRegisters.push(op.getRt());
        currentOp = op;
    }

    @Override
    public void accept(Op.AddI op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.Mul op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        physicalRegisters.push(op.getRt());
        currentOp = op;
    }

    @Override
    public void accept(Op.MulI op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.Cmp op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        physicalRegisters.push(op.getRt());
        currentOp = op;
    }

    @Override
    public void accept(Op.Ld op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.LdC op) {
        physicalRegisters.push(op.getRd());
        currentOp = op;
    }

    @Override
    public void accept(Op.LdI op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.St op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.StI op) {
        physicalRegisters.push(op.getRd());
        physicalRegisters.push(op.getRs());
        currentOp = op;
    }

    @Override
    public void accept(Op.BrLZ op) {
        physicalRegisters.push(op.getRd());
        currentOp = op;
    }

    @Override
    public void accept(Op.JpLZ op) {
        physicalRegisters.push(op.getRd());
        currentOp = op;
    }

    @Override
    public void accept(Op.Br op) {

        currentOp = op;
    }

    @Override
    public void accept(Op.Jp op) {

        currentOp = op;
    }

    protected String showUnit(){
        return "DE";
    }
}
