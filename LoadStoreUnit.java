import java.nio.channels.Pipe;

public class LoadStoreUnit extends Unit{
    
    private final Memory mem;

    private final ProgramCounter pc;

    private int pcVal;

    private int count;
    private final int L1_LATENCY = 4;

    LoadStoreUnit(Memory mem, ProgramCounter pc, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.pc = pc;
        this.mem = mem;
    }

    @Override
    protected void readOffPipeline(){
        pcVal = last.getPc();
        flag = last.isFlag();
        currentOp = last.pull();
        count = L1_LATENCY;
    }

    @Override
    protected void writeOnPipeline(){
        next.push(currentOp);
    }

    @Override
    protected void procInstruction() {
        count--;
    }

    @Override
    protected boolean isUnfinished() {
        return count > 0 && Utils.isLoadStore(currentOp); //if its not a load/store we're finished
    }

    //visitation

    @Override
    public void accept(Op.Add op) {
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.AddI op) {
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Mul op) {
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.MulI op) {
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Cmp op) {
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Ld op) {
        op.setResult(mem.read(op.getResult()));
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.LdC op) {
        op.setResult(mem.read(op.getResult()));
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.St op) {
        mem.set(op.getRdVal(), op.getResult());
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(flag){
            pc.set(op.getResult());
        }else{
            pc.set(pcVal);
        }
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(flag){
            pc.set(op.getResult());
        } else{
            pc.set(pcVal);
        }//otherwise set it to the passed-through value
    }

    @Override
    public void accept(Op.Br op) {
        pc.set(op.getResult());
    }

    @Override
    public void accept(Op.Jp op) {
        pc.set(op.getResult());
    }
    
}
