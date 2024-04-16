public class BranchUnit extends Unit{

    private boolean shouldFlush = false;

    private final ProgramCounter pc;

    private int currentRobEntry;

    BranchUnit(ProgramCounter pc, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.pc = pc;
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

    public void doneFlushing(){
        shouldFlush = false; //once we flush we dont want to flush again next cycle
    }

    @Override
    public void accept(Op.Add op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.AddI op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.Mul op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.MulI op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.Cmp op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
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
    public void accept(Op.St op) {
        throw new RuntimeException("BranchUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(flag){
            //we only need to reset to destination if we didnt set it correctly
            //(if we predicted wrong)
            pc.set(op.getResult());
        }else{
            pc.set(pcVal);
        }
        //if we got it wrong we flush
        if(flag != STATIC_PREDICT_BR_TAKEN){
            shouldFlush = true;
        }
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(flag){
            pc.set(op.getResult());
        }else{
            pc.set(pcVal);
        }
        if(flag != STATIC_PREDICT_BR_TAKEN){
            shouldFlush = true;
        }
    }

    @Override
    public void accept(Op.Br op) {
        pc.set(op.getResult());
    }

    @Override
    public void accept(Op.Jp op) {
        pc.set(op.getResult());
    }

    public boolean needsFlushing(){
        return shouldFlush;
    }

}
