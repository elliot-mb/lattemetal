public class BranchUnit extends Unit{

    private boolean shouldFlush = false;

    private final ProgramCounter pc;

    private final FetchUnit feu; //to tell it we branched

    private int currentRobEntry;

    private Integer flushAt;

    BranchUnit(ProgramCounter pc, FetchUnit feu, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.pc = pc;
        this.flushAt = null;
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

    public void doneFlushing(){
        shouldFlush = false; //once we flush we dont want to flush again next cycle
        flushAt = null;
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
        flag = op.getRdVal() <= 0;
        //if we got it wrong we flush
//        if(flag){
//            //we only need to reset to destination if we didnt set it correctly
//            //(if we predicted wrong)
//            feu.yesBruDidSetPC();
//            pc.set(op.getImVal());
//        }else{
//            //pc.set(pcVal);
//        }
        if(flag != STATIC_PREDICT_BR_TAKEN){
            if(flag){
                pc.set(op.getImVal());
                feu.yesBruDidSetPC();
            }else{
                pc.set(op.getResult()); // untaken
                feu.yesBruDidSetPC();
            }
            shouldFlush = true;
            flushAt = currentRobEntry + 1; //after the current rob entry because we need to maintain program order
        }
    }

    @Override
    public void accept(Op.JpLZ op) {
        flag = op.getRdVal() <= 0;
//        if(flag){
//            pc.set(op.getImVal());
//            feu.yesBruDidSetPC();
//        }else{
//            //pc.set(pcVal);
//        }
        if(flag != STATIC_PREDICT_BR_TAKEN){
            if(flag){
                pc.set(op.getImVal());
                feu.yesBruDidSetPC();
            }else{
                pc.set(op.getResult()); // untaken
                feu.yesBruDidSetPC();
            }
            shouldFlush = true;
            flushAt = currentRobEntry + 1; //after the current rob entry because we need to maintain program order
        }
    }

    @Override
    public void accept(Op.Br op) {
        pc.set(op.getImVal());
    }

    @Override
    public void accept(Op.Jp op) {
        pc.set(op.getImVal());
    }

    public boolean needsFlushing(){
        return shouldFlush;
    }

    public int whereFlushAt(){
        if(flushAt == null) throw new RuntimeException("whereFlushAt: flush was not requested so flushAt is null");
        return flushAt;
    }

}
