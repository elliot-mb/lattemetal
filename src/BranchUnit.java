public class BranchUnit extends Unit{

    private final ProgramCounter pc;

    private final FetchUnit feu; //to tell it we branched

    public static final int TAKEN = 1;
    public static final int NOT_TAKEN = 0;

    BranchUnit(ProgramCounter pc, FetchUnit feu, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.pc = pc;
        this.feu = feu;
    }

    @Override
    protected void readOffPipeline(){
        PipeLike in = ins[selectionPriority()];
        PipelineEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
        currentRobEntry = e.getEntry(); //requires rob entry number otherwise they crash
    }

    @Override
    protected void procInstruction() {
        //nothing
    }

    @Override
    protected boolean isUnfinished() {
        return false;
    }

    @Override
    protected String showUnit() {
        return "BR";
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){
        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    @Override
    public void accept(Op.Add op) {
        throw new RuntimeException("BranchUnit.accept: add instruction not valid");
    }

    @Override
    public void accept(Op.AddI op) {
        throw new RuntimeException("BranchUnit.accept: add instruction not valid");
    }

    @Override
    public void accept(Op.Mul op) {
        throw new RuntimeException("BranchUnit.accept: mul instruction not valid");
    }

    @Override
    public void accept(Op.MulI op) {
        throw new RuntimeException("BranchUnit.accept: mul instruction not valid");
    }

    @Override
    public void accept(Op.Cmp op) {
        throw new RuntimeException("BranchUnit.accept: cmp instruction not valid");
    }

    @Override
    public void accept(Op.Ld op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.LdC op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.LdI op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.St op) {
        throw new RuntimeException("BranchUnit.accept: store instruction not valid");
    }

    @Override
    public void accept(Op.StI op) {
        throw new RuntimeException("BranchUnit.accept: store instruction not valid");
    }

    @Override
    public void accept(Op.BrLZ op) {
        flag = op.getRdVal() <= 0;
    }

    @Override
    public void accept(Op.JpLZ op) {
        flag = op.getRdVal() <= 0;
    }

    @Override
    public void accept(Op.Br op) {
        //pc.set(op.getImVal()); //these are not speculative so we dont set the pc!
        //feu.yesBruDidSetPC();
    }

    @Override
    public void accept(Op.Jp op) {
        //pc.set(op.getResult() + op.getImVal());  //these are not speculative so we dont set the pc!
        //feu.yesBruDidSetPC();
    }


}
