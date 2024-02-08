public class ArithmeticLogicUnit implements InstructionVoidVisitor, Ticks {

    private PipelineRegister pipeRegIn;
    private PipelineRegister pipeRegOut;
    private Instruction currentOp;
    private boolean done;
    private boolean pushed;


    ArithmeticLogicUnit(PipelineRegister pipeRegIn, PipelineRegister pipeRegOut){
        this.pipeRegIn = pipeRegIn;
        this.pipeRegOut = pipeRegOut;
        this.currentOp = null;
        this.done = true;
        this.pushed = false;
    }

    @Override //overriding because we need to delegate to whats inside
    public void clk() { //effectively delegates to the instruction clk
        if(done) { //all Ticks objects ought to have this!
            if(!pushed) pushed = pipeRegOut.push(currentOp);
            //if we could push, we can try to overwrite what we have
            if(pushed) currentOp = pipeRegIn.pull();
            //if pulling was successful, we set done false to begin working, else we try again
            done = currentOp == null;
        }
        else currentOp.visit(this);
    }

    public void loadFilledOp(Instruction op){
        currentOp = op;
        done = false;
    }

    //should be done just if op is 'done'
    public Instruction requestOp(){
        return currentOp;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void accept(Op.Add op) {
        if(op.isDone()){
            //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
            //and then create a writeback stage
            op.setRdVal(op.getRsVal() + op.getRtVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.AddI op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() + op.getImVal();
            op.setRdVal(op.getRsVal() + op.getImVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Mul op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() * op.getRtVal();
            op.setRdVal(op.getRsVal() * op.getRtVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.MulI op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() * op.getImVal();
            op.setRdVal(op.getRsVal() * op.getImVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Cmp op) {
        if(op.isDone()){
            // modify rd value
            final int a = op.getRsVal();
            final int b = op.getRtVal();
            int cmpResult;
            if(a < b) cmpResult = -1;
            else if(a == b) cmpResult = 0;
            else cmpResult = 1;
            op.setRdVal(cmpResult);
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Ld op) {
        if(op.isDone()){
            op.setRdVal(mem.read(op.getRsVal() + op.getImVal()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.LdC op) {
        if(op.isDone()){
            //set register value to load from addr
            op.setRdVal(mem.read(op.getImVal()));
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.St op) {
        if(op.isDone()){
            //store register value at offset address
            mem.set(op.getRdVal(), op.getRsVal() + op.getImVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(op.isDone()){
            //modify program counter
            if(op.getRdVal() <= 0) {
                pc.set(op.getImVal());
            }else{
                pc.incr();
            }
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(op.isDone()){
            //modify program counter
            if(op.getRdVal() <= 0){
                pc.set(pc.getCount() + op.getImVal());
            }else{
                pc.incr();
            }
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Br op) {
        if(op.isDone()){
            pc.set(op.getImVal());
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Jp op) {
        if(op.isDone()){
            //modify program counter
            pc.set(pc.getCount() + op.getImVal());
            done = true;
            return;
        }
        op.clk();
    }

}
