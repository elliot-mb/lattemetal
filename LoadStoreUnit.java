
public class LoadStoreUnit implements InstructionVoidVisitor, Ticks{

    private Instruction currentOp;
    
    private final Memory mem;

    private final ProgramCounter pc;

    private int pcVal;
    private boolean branchTaken;

    private boolean done;

    LoadStoreUnit(Memory mem, ProgramCounter pc){

        this.pc = pc;
        this.mem = mem;
    }

    public void loadFilledOp(Instruction op, int pcVal, boolean branchTaken){
        currentOp = op;
        done = false;
        this.pcVal = pcVal;
        this.branchTaken = branchTaken;
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
        pc.set(pcVal);
        //nothing
    }

    @Override
    public void accept(Op.AddI op) {
        //nothing
        pc.set(pcVal);
        done = true;
    }

    @Override
    public void accept(Op.Mul op) {
        //nothing
        pc.set(pcVal);
        done = true;
    }

    @Override
    public void accept(Op.MulI op) {
        //nothing
        pc.set(pcVal);
        done = true;
    }

    @Override
    public void accept(Op.Cmp op) {
        //nothing
        pc.set(pcVal);
        done = true;
    }

    @Override
    public void accept(Op.Ld op) {
        if(op.isDone()){
            op.setResult(mem.read(op.getResult()));
            pc.set(pcVal);
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.LdC op) {
        if(op.isDone()){
            op.setResult(mem.read(op.getResult()));
            pc.set(pcVal);
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.St op) {
        if(op.isDone()){
            mem.set(op.getRdVal(), op.getResult());
            pc.set(pcVal);
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(branchTaken){
            pc.set(op.getResult());
        }else{
            pc.set(pcVal);
        }
        done = true;
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(branchTaken){
            pc.set(op.getResult());
        } else{
            pc.set(pcVal);
        }//otherwise set it to the passed-through value
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
