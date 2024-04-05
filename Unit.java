import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Unit implements InstructionVoidVisitor {

    /**
     * for any building block that reads from one pipeline register and writes to another
     */

    protected static final boolean STATIC_PREDICT_BR_TAKEN = true;

    protected final PipelineRegister[] ins;
    protected final PipelineRegister[] outs;

    protected final boolean[] outsChoice;

    protected Instruction currentOp;
    protected int pcVal;
    protected boolean flag;

    Unit(PipelineRegister[] ins, PipelineRegister[] outs){
        if(ins.length == 0 || outs.length == 0) throw new RuntimeException("Unit: provide at least one input and at least one output pipereg");
        this.ins = ins;
        this.outs = outs;
        this.outsChoice = new boolean[this.outs.length];
        rstChosenOuts();
    }

    protected List<Integer> getActiveIn(){
        List<Integer> inActive = new ArrayList<Integer>();
        int i = 0;
        for(PipelineRegister in : ins){
            if(in.canPull()) {
                inActive.add(i);
            }
            i++;
        }
        return inActive;
    }

    private int oldestInInstr(){
        int lowest = Integer.MAX_VALUE;
        int i = 0;
        int res = -1;
        for(PipelineRegister in : ins){
            if(in.canPull()) {
                lowest = Math.min(in.peek().getOp().getId(), lowest);
                res = i;
            }
            i++;
        }
        return res;
    }

    protected int selectionPriority(){
        return oldestInInstr();
    }

    //default implementations
    protected void readOffPipeline(){
        PipelineRegister in = ins[selectionPriority()];
        PipeRegEntry e = in.pull();
        pcVal = e.getPcVal();
        flag = e.getFlag();
        currentOp = e.getOp();
    }
    protected void writeOnPipeline(){
        if(areOutsUnchosen()) throw new RuntimeException("writeOnPipeline: choose outputs before writing on the pipeline");
        int i = 0;
        for(PipelineRegister out : outs){
            if(outsChoice[i]){
                if(!out.canPush()) throw new RuntimeException("writeOnPipeline: chosen output cannot be written to, please check before calling");
                PipeRegEntry e = new PipeRegEntry(currentOp, pcVal, flag);
                out.push(e);
            }
            i++;
        }
        rstChosenOuts();
    }
    protected boolean isDone(){
        return currentOp == null;
    }

    protected abstract void procInstruction(); //process instruction
    protected abstract boolean isUnfinished(); //when we need to count down instruction

    protected boolean canPullOffActiveIn(){
        if(getActiveIn().isEmpty()) return false;
        int activeIn = selectionPriority();
        return ins[activeIn].canPull();
    }

    //if all of our chosen outputs can be pushed onto, this returns true. otherwise we should stall
    //run chooseOuts() before this; its not run here because we might decide some time before this function
    private boolean canPushOnChosenOuts(){
        int i = 0;
        boolean ok = false;
        for(PipelineRegister out : outs){
            if(outsChoice[i] && !out.canPush()) return false;
            if(outsChoice[i]) ok = true; // we must pick at least somewhere to write the output
            i++;
        }
        //ok check stops it being vacuously true
        if(!ok) throw new RuntimeException("canPushOnChosenOuts: at least one output pipeline register must be chosen");

        return true;
    }

    private boolean areOutsUnchosen(){
        int i = 0;
        for(PipelineRegister out : outs) {
            if (outsChoice[i]) return false; // we must pick at least somewhere to write the output
            i++;
        }
        return true;
    }

    private void rstChosenOuts(){
        Arrays.fill(outsChoice, false);
    }

    public void clk(){
        // we have processed this op, and if its not the case that we can pull in and push out to chosen outs, we stall (return)
        if(isDone() && !(canPullOffActiveIn()))/* && (areOutsUnchosen() || canPushOnChosenOuts())))*/ return;
        if(isDone()) readOffPipeline(); //dont re-copy if we are mid-processing
        if(isUnfinished()) {
            procInstruction(); //always run at least once if isUnfinished is ever false
                               //some unit stall with this instruction like the ALU while
                               //while RSs wait for deps
        }
        if(isUnfinished()) return;
        currentOp.visit(this); // /!\ main processing happens here /!\ (forced to be implementation-defined)
        chooseOuts(); //implementation defined unless has one output
        if(!canPushOnChosenOuts()) return; //stall a clock cycle if we cant push the result
        writeOnPipeline();
        currentOp = null; //empty out our intermediate storage to accept the next one
    }

    @Override
    public void accept(Op.No op) {
        //this method is uniform for all units because it does nothing
    }

    abstract protected String showUnit();

    public String toString(){
        return showUnit() + (currentOp == null ? "\s_" : isUnfinished() ? "\s↘" : "\s|");//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
    }

    public void flush(){
        currentOp = null;
        pcVal = 0;
        flag = false;

    }

    protected void chooseOuts() {
        outsChoice[0] = true; //default selection of the first output, OVERRIDE THIS WHERE NEEDED
    }

}
