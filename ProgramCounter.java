public class ProgramCounter {

    private int counter; //address

    ProgramCounter(){
        this.counter = 0;
    }

    public void incr(){
        this.counter++;
    }

    public void set(int val){
        this.counter = val;
    }

    public int count(){
        return counter;
    }
}
