public abstract class Unit implements InstructionVoidVisitor, Ticks {

    /**
     * for any building block that reads from one pipeline register and writes to another
     */

    protected final PipelineRegister last;
    protected final PipelineRegister next;

    protected Instruction currentOp;

    Unit(PipelineRegister last, PipelineRegister next){
        this.last = last;
        this.next = next;
    }

    protected abstract void readOffPipeline();
    protected abstract void writeOnPipeline();
    protected abstract void procInstruction(); //process instruction
    protected abstract boolean isUnfinished(); //when we need to count down instruction

    public void clk(){
        //if we have finished processing this instruction but cant pull from last, we stall one cycle
        if(isDone() && !last.canPull()) return; //stall a clock cycle
        if(isDone()) readOffPipeline(); //dont re-copy if we are mid-processing
        if(isUnfinished()) {
            procInstruction();
            return; //use up a clock cycle
        }
        currentOp.visit(this); //process operation
        if(!next.canPush()) return; //stall a clock cycle if we cant push the result
        writeOnPipeline();
        currentOp = null; //empty out our intermediate storage to accept the next one
    }

}
