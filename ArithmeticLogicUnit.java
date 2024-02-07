public class ArithmeticLogicUnit implements InstructionVoidVisitor, Ticks {

    private Instruction currentOp;
    private boolean done;
    private final Memory mem;
    private final ProgramCounter pc;

    ArithmeticLogicUnit(Memory mem, ProgramCounter pc){
        this.currentOp = null;
        this.mem = mem;
        this.pc = pc;
        this.done = true;
    }

    @Override //overriding because we need to delegate to whats inside
    public void clk() { //effectively delegates to the instruction clk
        if(!isDone()) currentOp.visit(this);
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
