

public class FetchUnit extends Unit {

    private static final int FETCH_LATENCY = 1;

    private final InstructionCache ic;
    private final BranchTargetBuffer btb;

    private final ProgramCounter pc;

    private final Durate counter = new Durate(FETCH_LATENCY);

    FetchUnit(InstructionCache ic, ProgramCounter pc, BranchTargetBuffer btb, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.ic = ic;
        this.pc = pc;
        this.btb = btb;
    }
    @Override
    protected void procInstruction() {
        if(pc.getCount() == ic.numInstrs()) {
            currentOp = Utils.opFactory.new No();
            counter.finish();
            return;
        }
        counter.decr();
        currentOp = ic.getInstruction(pc.getCount());
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        counter.rst();
//        pc.set(pcVal + 1);
    }
//

    @Override
    protected void writeOnPipeline() {
        //incr then write the incremented value on pipeline
        //if(!bruSetPC) pc.set(pcVal);
        super.writeOnPipeline();
    }
//
//    // returns null if this is not a branch
//    public Integer getBranch(){
//        return isBranch ? whatBranchID : null;
//    }

//    private boolean isBranch(){
//        return whatBranchID != null;
//    }
    
    @Override
    protected boolean isUnfinished() {
        return !counter.isDone();// && !isBranch();
    }


    //nothing happens in visitation because fetching happens in procInstruction
    @Override
    public void accept(Op.Add op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.AddI op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Mul op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.MulI op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Cmp op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Ld op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.LdC op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.LdI op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.St op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.StI op) {
        pcVal++;
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.BrLZ op) {
        op.setResult(pcVal + 1); //branch untaken!
        if(Processor.BR_PREDICTOR_IS_FIXED){
            if(FIXED_PREDICTOR_SET_TAKEN){
                //next.setPcVal(op.getImVal());
                pcVal = op.getImVal(); //pc.set(op.getImVal()); //static prediciton
            }else{
                pcVal++;
            }
        }else{ //use btb
            if(btb.hasEntry(pcVal)){
                pcVal = btb.getPrediction(pcVal);
                flag = true;
            }else{
                pcVal++;
                flag = false;
            }
        }

        pc.set(pcVal);
    }

    @Override
    public void accept(Op.JpLZ op) {
        op.setResult(pcVal + 1); //branch untaken!
        if(Processor.BR_PREDICTOR_IS_FIXED) {
            if (FIXED_PREDICTOR_SET_TAKEN) {
                //next.setPcVal(op.getImVal());
                pcVal += op.getImVal();
            } else {
                pcVal++;
            }
        }else{
            if(btb.hasEntry(pcVal)){
                pcVal = btb.getPrediction(pcVal); //should encode the offset for us
                flag = true;
            }else{
                pcVal++;
                flag = false;
            }
        }
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Br op) {
        op.setResult(pcVal + 1);
        pcVal = op.getImVal();
        pc.set(pcVal);
    }

    @Override
    public void accept(Op.Jp op) {
        op.setResult(pcVal + 1);
        pcVal += op.getImVal();
        pc.set(pcVal);
    }

    protected String showUnit(){
        return "FE";
    }
}
