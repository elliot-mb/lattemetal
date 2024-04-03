

public class ProgramCounter {

    private int counter; //address
    private int icSize;

    ProgramCounter(int icSize){
        this.counter = 0;
        this.icSize = icSize;
    }

    public void incr(){
        this.counter++;
    }

    public boolean isDone(){
        return this.counter >= this.icSize;
    }

    public void set(int val){
        System.out.println(val);
        this.counter = val;
    }

    public int getCount(){
        return counter;
    }

    public boolean isCountDone(int count) { return count >= icSize; }
}
