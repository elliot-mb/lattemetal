public class LoadStoreUnit implements InstructionVoidVisitor, Ticks{

    private Instruction currentOp;
    
    private final Memory mem;
    
    private boolean done;

    LoadStoreUnit(Memory mem){
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
            op.setRdVal(mem.read(op.getRdVal()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.LdC op) {
        if(op.isDone()){
            op.setRdVal(mem.read(op.getImVal()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.St op) {
        if(op.isDone()){
            mem.set(op.getRdVal(), op.getRsVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.BrLZ op) {
        done = true;
        //nothing
    }

    @Override
    public void accept(Op.JpLZ op) {
        done = true;
        //nothing
    }

    @Override
    public void accept(Op.Br op) {
        done = true;
        //nothing
    }

    @Override
    public void accept(Op.Jp op) {
        done = true;
        //nothing
    }
    
}
