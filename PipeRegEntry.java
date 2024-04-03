public class PipeRegEntry {
    private final Instruction op;
    private final Integer pcVal;
    private final boolean flag; //used for a 'branch taken' bit

    PipeRegEntry(Instruction op, Integer pcVal, boolean flag){
        this.op = op;
        this.pcVal = pcVal;
        this.flag = flag;
    }

    PipeRegEntry(){
        this.op = Utils.opFactory.new No();
        this.pcVal = 0;
        this.flag = false;
    }

    public Instruction getOp(){
        if(op == null) return new PipeRegEntry().getOp();
        return op;
    }

    public Integer getPcVal(){
        return pcVal;
    }

    public boolean getFlag(){
        return flag;
    }

    public PipeRegEntry copy(){
        return new PipeRegEntry(op.copy(), pcVal.intValue(), flag);
    }
}
