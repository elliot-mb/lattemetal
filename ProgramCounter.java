public class ProgramCounter {

    private int counter; //address

    ProgramCounter(){
        this.counter = 0;
    }

    public void incr(){
        this.counter++;
    }

    public int count(){
        return counter;
    }
}
