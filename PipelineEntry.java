import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipelineEntry {
    private final Instruction op;
    private final Integer pcVal;
    private final boolean flag; //used for a 'branch taken' bit

    private Integer entry = null;

    PipelineEntry(Instruction op, Integer pcVal, boolean flag, int entry){
        this.op = op;
        this.pcVal = pcVal;
        this.flag = flag;
        this.entry = entry;
    }

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

    public Integer getEntry() {
        if(entry == null) throw new RuntimeException("getEntry: entry is null");
        return entry;
    }

    public PipelineEntry copy(){
        if(entry == null) return new PipelineEntry(op.copy(), pcVal.intValue(), flag);
        return new PipelineEntry(op.copy(), pcVal.intValue(), flag, entry.intValue());
    }

    private String instrToId(Instruction op){
        if(op == null) return "__";
        String pad = op.getId() % 100 < 10 ? "0" : "";
        return pad + (op.getId() % 100);
    }


    @Override
    public String toString(){
        return "" + instrToId(op);
    }
}
