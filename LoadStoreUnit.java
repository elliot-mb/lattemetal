
public class LoadStoreUnit implements InstructionVoidVisitor, Ticks{

    private Instruction currentOp;
    
    private final Memory mem;

    private final ProgramCounter pc;

    private final PipelineRegister tempLast;

    private boolean done;

    LoadStoreUnit(Memory mem, ProgramCounter pc, PipelineRegister tempLast){
        this.tempLast = tempLast;
        this.pc = pc;
        this.mem = mem;
    }

    public void loadFilledOp(Instruction op){
        currentOp = op;
        done = false;
    }

    public Instruction requestOp(){
        return currentOp;
    }

    @Override
    public void clk() {
        if(!isDone()){
            currentOp.visit(this);
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    //visitation

    @Override
    public void accept(Op.Add op) {
        done = true;
        //nothing
    }

    @Override
    public void accept(Op.AddI op) {
        //nothing
        done = true;
    }

    @Override
    public void accept(Op.Mul op) {
        //nothing
        done = true;
    }

    @Override
    public void accept(Op.MulI op) {
        //nothing
        done = true;
    }

    @Override
    public void accept(Op.Cmp op) {
        //nothing
        done = true;
    }

    @Override
    public void accept(Op.Ld op) {
        if(op.isDone()){
            op.setResult(mem.read(op.getResult()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.LdC op) {
        if(op.isDone()){
            op.setResult(mem.read(op.getResult()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.St op) {
        if(op.isDone()){
            mem.set(op.getRdVal(), op.getResult());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(tempLast.isFlag()){
            pc.set(op.getResult());
        }
        done = true;
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(tempLast.isFlag()){
            pc.set(op.getResult());
        } //otherwise set it to the passed-through value + 1
        done = true;
    }

    @Override
    public void accept(Op.Br op) {
        pc.set(op.getResult());
        done = true;

    }

    @Override
    public void accept(Op.Jp op) {
        pc.set(op.getResult());
        done = true;
    }
    
}
