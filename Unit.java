public abstract class Unit implements InstructionVoidVisitor {

    /**
     * for any building block that reads from one pipeline register and writes to another
     */

    protected final PipelineRegister last;
    protected final PipelineRegister next;

    protected Instruction currentOp;
    protected int pcVal;
    protected boolean flag;

    Unit(PipelineRegister last, PipelineRegister next){
        this.last = last;
        this.next = next;
    }

    //default implementations
    protected void readOffPipeline(){
        pcVal = last.getPc();
        flag = last.isFlag();
        currentOp = last.pull();
    }
    protected void writeOnPipeline(){
        next.setFlag(flag);
        next.setPc(pcVal); //just pass it through
        next.push(currentOp);
    }
    protected boolean isDone(){
        return currentOp == null;
    }

    protected abstract void procInstruction(); //process instruction
    protected abstract boolean isUnfinished(); //when we need to count down instruction

    public void clk(){
        //if we have finished processing this instruction but cant pull from last, we stall one cycle
        if(isDone() && !last.canPull()) return; //stall a clock cycle
        if(isDone()) readOffPipeline(); //dont re-copy if we are mid-processing
        if(isUnfinished()) procInstruction();
        if(isUnfinished()) return;

        currentOp.visit(this); //process operation
        if(!next.canPush()) return; //stall a clock cycle if we cant push the result
        writeOnPipeline();
        currentOp = null; //empty out our intermediate storage to accept the next one
    }

    @Override
    public void accept(Op.No op) {
        //this method is uniform for all units because it does nothing
    }

}
