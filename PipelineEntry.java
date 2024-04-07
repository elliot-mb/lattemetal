import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineEntry {
    private final Instruction op;
    private final Integer pcVal;
    private final boolean flag; //used for a 'branch taken' bit

    private final List<Integer> entry = new ArrayList<Integer>();

    PipelineEntry(Instruction op, Integer pcVal, boolean flag, int... entry){
        this.op = op;
        this.pcVal = pcVal;
        this.flag = flag;
        for(int x : entry){
            this.entry.add(x);
        }
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

    public List<Integer> getEntry() { return entry; }

    public PipelineEntry copy(){
        return new PipelineEntry(op.copy(), pcVal.intValue(), flag);
    }
}
