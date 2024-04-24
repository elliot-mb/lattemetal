import java.util.List;
import java.util.Map;

public class LoadStoreUnit extends Unit{

    private static final int L1_LATENCY = 3;
    private static final int NOP_LATENCY = 1;
    private final Memory mem;

    private final ReorderBuffer rob;

    private final RegisterFile rf;

    private final Map<Integer, List<Integer>> cdb;

    private final RegisterAliasTable prf;

    LoadStoreUnit(Memory mem, RegisterFile rf, RegisterAliasTable prf, Map<Integer, List<Integer>> cdb, ReorderBuffer rob, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.mem = mem;
        this.rf = rf;
        this.cdb = cdb;
        this.prf = prf;
        this.rob = rob;
    }

    @Override
    protected boolean isUnfinished() {
        return currentOp == null || !currentOp.isDone();//(!counter.isDone() && Utils.isLoadStore(currentOp)) || !counterNop.isDone(); //if its not a load/store we're finished
    }

    @Override
    protected void readOffPipeline(){
        PipeLike in = ins[selectionPriority()];
        PipelineEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
        currentRobEntry = e.getEntry(); //requires rob entry number otherwise they crash
        currentOp.rst();
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){
        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    @Override
    protected void procInstruction() {
        if(currentOp != null && !currentOp.isDone()) currentOp.decr();
    }

    //visitation

    @Override
    public void accept(Op.Add op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.AddI op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Mul op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.MulI op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Cmp op) {
        //pc.set(pcVal);
        throw new RuntimeException("LoadStoreUnit.accept: instruction not valid");
    }

    @Override
    public void accept(Op.Ld op) {
        int addr = op.getRsVal() + op.getImVal(); //copied from old ALU
        int res = mem.get(addr);
        op.setRsVal(addr); //the offset location
        op.setRdVal(res);
    }

    @Override
    public void accept(Op.LdC op) {
        int addr = op.getImVal(); //either we get from mem or get from the rob
        int res = mem.get(addr); //this isnt always right, exactly when there is a store ahead to the same address
        op.setRdVal(res);
    }

    @Override
    public void accept(Op.LdI op) {
        int res = mem.get(op.getRsVal());
        op.setRsVal(op.getRsVal() + op.getImVal()); //what we update rs to! broadcast this like [res, addr] at writeback
        op.setRdVal(res);
    }

    @Override
    public void accept(Op.St op) {
        op.setResult(op.getRsVal() + op.getImVal());
    }

    @Override
    public void accept(Op.StI op) {
        op.setResult(op.getRsVal() + op.getImVal()); //where we set to
    }

    @Override
    public void accept(Op.BrLZ op) {

    }

    @Override
    public void accept(Op.JpLZ op) {

    }

    @Override
    public void accept(Op.Br op) {

    }

    @Override
    public void accept(Op.Jp op) {

    }

    protected String showUnit(){
        return "LS";
    }
}
