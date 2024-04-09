public class ReorderEntry {

    public static int uId = RegisterFile.NUM_REGS + 1; //start above register values so locations dont collide
    public final int id;
    private final Instruction op;
    private final int destination; // register number OR memory address for where instruction result is written
    private int value; //holds the instruciton result until commital
    private boolean ready; //false if still executing, true otherwise

    ReorderEntry(Instruction op, int destination){
        this.op = op;
        this.destination = destination;
        this.id = uId;
        this.ready = false;
        uId++;
    }

    public int getId(){
        return this.id;
    }

    public void setValue(int value){
        this.value = value;
        this.ready = true;
    }

    public String toString(){
        int id =  op.getId() % 100;
        String pad = Utils.digitsInInt(op.getId()) < 2 ? "0" : "";
        return pad + id;
    }

    public boolean isReady(){
        return ready;
    }

    public int getValue() {
        if(!ready) throw new RuntimeException("getValue: ReorderEntry has not been evaluated");
        return value;
    }

    public Instruction getOp(){
        return op;
    }
}
