public class ReorderEntry {

    public static int uId = 0;
    public final int id;
    private final Instruction op;
    private final int destination; // register number OR memory address
    private int value; //holds the instruciton result until commital
    private boolean ready; //false if still executing, true otherwise

    ReorderEntry(Instruction op, int destination){
        this.op = op;
        this.destination = destination;
        this.id = uId;
        uId++;
    }
}
