public class ArithmeticLogicUnit implements InstructionVoidVisitor, Ticks {

    private Instruction currentOp;
    private boolean done;

    ArithmeticLogicUnit(){
        this.currentOp = null;
    }

    @Override //overriding because we need to delegate to whats inside
    public void clk() { //effectively delegates to the instruction clk
        if(currentOp != null) currentOp.visit(this);
    }

    public void loadFilledOp(Instruction op){
        currentOp = op;
        done = false;
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
            op.setRdVal();
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.AddI op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() + op.getImVal();
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Mul op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() * op.getRtVal();
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.MulI op) {
        if(op.isDone()){
            // modify register value = op.getRsVal() * op.getImVal();
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Cmp op) {
        if(op.isDone()){
            // modify rd value
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Ld op) {
        if(op.isDone()){
            //set register value to load from addr
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.LdC op) {
        if(op.isDone()){
            //set register value to load from addr
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.St op) {
        if(op.isDone()){
            //store register value at offset address
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(op.isDone()){
            //modify program counter
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(op.isDone()){
            //modify program counter
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Br op) {
        if(op.isDone()){
            //modify program counter
            done = true;
            return;
        }
        op.clk();
    }

    @Override
    public void accept(Op.Jp op) {
        if(op.isDone()){
            //modify program counter
            done = true;
            return;
        }
        op.clk();
    }

}
