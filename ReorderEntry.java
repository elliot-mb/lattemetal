public class ReorderEntry {

    public static int uId = RegisterFile.NUM_REGS + 1; //start above register values so locations dont collide
    public final int id;
    private final Instruction op;
    private final int destination; // register number OR memory address for where instruction result is written
    private int value; //holds the instruciton result until commital
    private boolean ready; //false if still executing, true otherwise
    private final int pcVal;
//    private boolean flag; //used for branch instructions

    ReorderEntry(Instruction op, int destination, int pcVal){
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

    public void setValue(int value){
        this.value = value;
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

    public int getValue() {
        if(!ready) throw new RuntimeException("getValue: ReorderEntry has not been evaluated");
        return value;
    }

//    public void isBranchAndFlushesPipe(){
//        if(!Utils.isBranch(op)) throw new RuntimeException("isBranchAndFlushesPipe: this instruction '" +op + "' is not a branch");
//        flag = true;
//    }

    public Instruction getOp(){
        return op;
    }
}
