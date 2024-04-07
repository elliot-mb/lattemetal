import java.util.List;
import java.util.Map;

public class WriteBackUnit extends Unit{

    private static final int REG_LATENCY = 1;
    private final RegisterFile rf;

    private final ReorderBuffer rob;
    private final Durate counter = new Durate(REG_LATENCY);

    private final Map<Integer, List<Integer>> cdb;

    private int currentRobEntry;

    WriteBackUnit(RegisterFile rf, ReorderBuffer rob, Map<Integer, List<Integer>> cdb, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.rf = rf;
        this.rob = rob;
        this.cdb = cdb;
    }

    @Override
    protected void readOffPipeline(){
        PipelineRegister in = ins[selectionPriority()];
        PipelineEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
        currentRobEntry = e.getEntry().get(0);
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
        cdb.put()
    }

    // all the below methods write back to the registers correctly
    @Override
    public void accept(Op.Add op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.AddI op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Mul op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.MulI op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Cmp op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.Ld op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.LdC op) {
        setRdToRes(op.getRd(), op.getResult());
    }

    @Override
    public void accept(Op.St op) {
        //nothing
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
