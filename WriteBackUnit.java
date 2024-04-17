import java.util.*;

public class WriteBackUnit extends Unit{

    private static final int REG_LATENCY = 1;
    private final RegisterFile rf;

    private final ReorderBuffer rob;
    private final RegisterAliasTable prf;
    private final Durate counter = new Durate(REG_LATENCY);

    private final Map<Integer, List<Integer>> cdb;

    private int currentRobEntry;

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
        int val;
//        if (prf.isRegValUnmapped(op.getRd())) {
//            val = rf.getReg(op.getRd()); //this happens when nobody is busy with this register
//            cdb.put(currentRobEntry, List.of(val));
//        }
        cdb.put(currentRobEntry, List.of(rf.getReg(op.getRd())));

//        } else {
//            //not entirely sure if this 'else' case is necessary because we have clk() in ROB doing this
//            int resultEntryLoc = prf.whereRegInRob(op.getRd());
//            val = rob.getValOfEntry(resultEntryLoc); //copy reg value from elsewhere in the rob to the result of the store
//        }
//        cdb.put(currentRobEntry, List.of(val)); //if this doesnt happen, it will later get broadcast in another instr
    }

    @Override
    public void accept(Op.BrLZ op) {
        //nothing
    }

    @Override
    public void accept(Op.JpLZ op) {
        //nothing
    }

    @Override
    public void accept(Op.Br op) {
        //nothing
    }

    @Override
    public void accept(Op.Jp op) {
        //nothing
    }

    protected String showUnit(){
        return "WB";
    }
}
