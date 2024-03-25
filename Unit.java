import java.util.Arrays;

public abstract class Unit implements InstructionVoidVisitor {

    /**
     * for any building block that reads from one pipeline register and writes to another
     */

    protected static final boolean STATIC_PREDICT_BR_TAKEN = true;

    protected final PipelineRegister[] ins;
    protected final PipelineRegister[] outs;

    protected int inActive;
    protected final boolean[] outsChoice;

    protected Instruction currentOp;
    protected int pcVal;
    protected boolean flag;

    Unit(PipelineRegister[] ins, PipelineRegister[] outs){
        if(ins.length == 0 || outs.length == 0) throw new RuntimeException("Unit: provide at least one input and at least one output pipereg");
        this.ins = ins;
        this.outs = outs;
        this.outsChoice = new boolean[this.outs.length];
    }

    private void getActiveIn(){
        boolean hasSet = false;
        int i = 0;
        for(PipelineRegister in : ins){
            if(in.canPull() && !hasSet){
                hasSet = true; //this should only occur once
                inActive = i;
            }else if(in.canPull()){
                throw new RuntimeException("getActiveIn: this unit was sent more than one input instruction to process, should receive exactly one");
            }
            i++;
        }
    }

    //default implementations
    protected void readOffPipeline(){
        getActiveIn();
        PipelineRegister in = ins[inActive];
        pcVal = in.getPcVal();
        flag = in.isFlag();
        currentOp = in.pull();
    }
    protected void writeOnPipeline(){
        if(areOutsUnchosen()) throw new RuntimeException("writeOnPipeline: choose outputs before writing on the pipeline");
        int i = 0;
        for(PipelineRegister out : outs){
            if(outsChoice[i]){
                if(!out.canPush()) throw new RuntimeException("writeOnPipeline: chosen output cannot be written to, please check before calling");
                out.setFlag(flag);
                out.setPcVal(pcVal);
                out.push(currentOp);
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

    private boolean canPullOffActiveIn(){
        getActiveIn();
        return ins[inActive].canPull();
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
        if(isDone() && !(canPullOffActiveIn() && (areOutsUnchosen() || canPushOnChosenOuts()))) return;
        if(isDone()) readOffPipeline(); //dont re-copy if we are mid-processing
        if(isUnfinished()) {
            procInstruction(); //always run at least once if isUnfinished is ever false
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

    public String toString(){
        return currentOp == null ? " " : isUnfinished() ? "v" : "|";//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
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
