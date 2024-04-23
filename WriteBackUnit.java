import java.util.*;

public class WriteBackUnit extends Unit{

    private static final int REG_LATENCY = 1;
    private final RegisterFile rf;

    private final ReorderBuffer rob;
    private final RegisterAliasTable prf;
    private final Durate counter = new Durate(REG_LATENCY);

    private final Map<Integer, List<Integer>> cdb;


    WriteBackUnit(RegisterFile rf, ReorderBuffer rob, RegisterAliasTable prf, Map<Integer, List<Integer>> cdb, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.rf = rf;
        this.rob = rob;
        this.cdb = cdb;
        this.prf = prf;
    }

    @Override
    protected void readOffPipeline(){
        PipeLike in = ins[selectionPriority()];
        PipelineEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
        currentRobEntry = e.getEntry();
        counter.rst();
        //System.out.println("writeback reads " + currentOp);
    }

//    @Override
//    protected void writeOnPipeline(){
//        next.push();
//    }

    @Override
    protected void procInstruction() {
        counter.decr();
    }

    @Override
    protected boolean isUnfinished() {
        return !counter.isDone();
    }

    private void setRdToRes(RegisterName rd, int result){
//        rf.setReg(rd, result); change this to setting it on the common data bus!
        

        //cdb.put(currentRobEntry, List.of(result)); do this earlier! at like after the lsu and wbu complete
    }

    // all the below methods write back to the registers correctly
    @Override
    public void accept(Op.Add op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
    }

    @Override
    public void accept(Op.AddI op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
    }

    @Override
    public void accept(Op.Mul op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
    }

    @Override
    public void accept(Op.MulI op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
    }

    @Override
    public void accept(Op.Cmp op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(currentOp.getResult()));
    }

    @Override
    public void accept(Op.Ld op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(op.getRdVal()));
    }

    @Override
    public void accept(Op.LdC op) {
        //setRdToRes(op.getRd(), op.getResult());
        cdb.put(currentRobEntry, Collections.singletonList(op.getRdVal()));
    }

    @Override
    public void accept(Op.St op) {
        //stores' result is set to the location in memory
        cdb.put(currentRobEntry, List.of(op.getRdVal())); //rdval is where we store the register value taht is what we write
    }

    @Override
    public void accept(Op.BrLZ op) {
        cdb.put(currentRobEntry, List.of(flag ? BranchUnit.TAKEN : BranchUnit.NOT_TAKEN)); //to ready up branches we still need to broadcast something on to the bus, yea!
    }

    @Override
    public void accept(Op.JpLZ op) {
        cdb.put(currentRobEntry, List.of(flag ? BranchUnit.TAKEN : BranchUnit.NOT_TAKEN));
    }

    @Override
    public void accept(Op.Br op) {
        cdb.put(currentRobEntry, List.of()); //to ready up branches we still need to broadcast something on to the bus, yea!
    }

    @Override
    public void accept(Op.Jp op) {
        cdb.put(currentRobEntry, List.of()); //to ready up branches we still need to broadcast something on to the bus, yea! 
    }

    protected String showUnit(){
        return "WB";
    }
}
