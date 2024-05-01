import java.util.Arrays;
import java.util.List;

public class ReorderEntry {

    public static final int FST = 0;
    public static final int SND = 1;
    public static int uId = RegisterFile.NUM_REGS + 1; //start above register values so locations dont collide
    public final int id;
    private final Instruction op;
    private final int destination; // register number OR memory address for where instruction result is written
    private Integer value1; //holds the instruciton result until commital
    private Integer value2;
    private boolean ready; //false if still executing, true otherwise
    private final int pcVal;
//    private boolean flag; //used for branch instructions

    ReorderEntry(Instruction op, int destination, int pcVal){
        this.value1 = null;
        this.value2 = null;
        this.op = op;
        this.destination = destination;
        this.id = uId;
        this.ready = false;
        this.pcVal = pcVal; //used for flushing and resetting!
//        this.flag = false; //is set true when flush
        uId++;
    }

    public int getPcVal(){
        return pcVal;
    }

    public int getId(){
        return this.id;
    }
//
    public void setValue(int value, int which){
        if(which == 0) value1 = value;
        else value2 = value;
    }

    public List<Integer> getValues(){
        return Arrays.asList(value1, value2);
    }

    public void readyUp(){
        this.ready = true;
    }

    public String toString(){
        return Utils.twoDigitInstrId(op);
    }

    public boolean isReady(){
        return ready;
    }

    public int getValue(int which) {
        if(which != 0 && which != 1) throw new RuntimeException("getValue: which must select between two values (cannot interpret '" + which + "')" );
        if(!ready) throw new RuntimeException("getValue: ReorderEntry has not been evaluated");
        return which == 0 ? value1 : value2;
    }

//    public void isBranchAndFlushesPipe(){
//        if(!Utils.isBranch(op)) throw new RuntimeException("isBranchAndFlushesPipe: this instruction '" +op + "' is not a branch");
//        flag = true;
//    }

    public Instruction getOp(){
        return op;
    }
}
