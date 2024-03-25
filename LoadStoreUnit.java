

public class LoadStoreUnit extends Unit{

    private static final int L1_LATENCY = 3;
    private static final int NOP_LATENCY = 1;
    private final Memory mem;

    private Durate counter = new Durate(L1_LATENCY);
    private Durate counterNop = new Durate(NOP_LATENCY);

    LoadStoreUnit(Memory mem, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.mem = mem;
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        counter.rst();
        counterNop.rst();
    }

    @Override
    protected void procInstruction() {
        counter.decr();

        if(!counterNop.isDone()) counterNop.decr();
    }

    @Override
    protected boolean isUnfinished() {
        return (!counter.isDone() && Utils.isLoadStore(currentOp)) || !counterNop.isDone(); //if its not a load/store we're finished
    }

    //visitation

    @Override
    public void accept(Op.Add op) {
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.AddI op) {
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.Mul op) {
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.MulI op) {
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.Cmp op) {
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.Ld op) {
        op.setResult(op.getRsVal() + op.getImVal()); //copied from old ALU
        op.setResult(mem.read(op.getResult()));
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.LdC op) {
        op.setResult(op.getImVal()); //copied from old ALU
        op.setResult(mem.read(op.getResult()));
        //pc.set(pcVal);
    }

    @Override
    public void accept(Op.St op) {
        op.setResult(op.getRsVal() + op.getImVal()); //copied from old ALU
        mem.set(op.getRdVal(), op.getResult());
        //pc.set(pcVal);
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
