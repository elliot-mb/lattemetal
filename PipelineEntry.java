public class PipelineEntry {
    private final Instruction op;
    private final Integer pcVal;
    private final boolean flag; //used for a 'branch taken' bit

    PipelineEntry(Instruction op, Integer pcVal, boolean flag){
        this.op = op;
        this.pcVal = pcVal;
        this.flag = flag;
    }

    PipelineEntry(){
        this.op = Utils.opFactory.new No();
        this.pcVal = 0;
        this.flag = false;
    }

    public Instruction getOp(){
        if(op == null) return new PipelineEntry().getOp();
        return op;
    }

    public Integer getPcVal(){
        return pcVal;
    }

    public boolean getFlag(){
        return flag;
    }

    public PipelineEntry copy(){
        return new PipelineEntry(op.copy(), pcVal.intValue(), flag);
    }
}
