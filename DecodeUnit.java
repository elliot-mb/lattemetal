

public class DecodeUnit extends Unit{

    private final RegisterFile rf;

    private final CircluarQueue<RegisterName> physicalRegisters; //added to each time we make an instruction
    private final CircluarQueue<Integer> physicalRobEntries;

    private final Processor proc;

    DecodeUnit(RegisterFile rf, Processor proc, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.proc = proc;
        this.physicalRegisters = new CircluarQueue<RegisterName>(proc.PHYSICAL_REGISTER_COUNT); //added to each time we make an instruction
        this.physicalRobEntries = new CircluarQueue<Integer>(proc.PHYSICAL_REGISTER_COUNT);

        this.rf = rf;
    }

    public void usePrfs(Instruction op, int robEntry){
        for(int i = 0; i < op.getRegsNeeded(); i++){
            physicalRegisters.push(op.getIthReg(i));
            physicalRobEntries.push(robEntry);
        }
    }

    public void freePrfs(Instruction op){
        for(int i = 0; i < op.getRegsNeeded(); i++){
            physicalRegisters.pop();
            physicalRobEntries.pop();
        }
    }

    public boolean noPrfsFree(){
        return physicalRegisters.getElementsIn() > physicalRegisters.getSize() - 2;
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
        return false;//physicalRegisters.getElementsIn() > physicalRegisters.getSize() - 3; //block when we run out of prf, at least three physical registers must be free
    }

    @Override
    public void accept(Op.Add op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.AddI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Mul op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.MulI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Cmp op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.Ld op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.LdC op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.LdI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.St op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.StI op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.BrLZ op) {
        currentOp = op;
    }

    @Override
    public void accept(Op.JpLZ op) {
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
