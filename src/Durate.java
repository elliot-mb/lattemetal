package src;

public class Durate {
    // provides an interface/update method for classes that have update methods and tick down with a cycle of the clock
    // this includes micro-ops like stores, decodes, issues etc.

    protected final int duration;
    private int count;

    Durate(int duration){
        this.duration = duration;
        this.count = duration;
    }

    //recieve clock cycle, dont call after action is done
    public void decr() {
        count--;
        if(count < 0) System.out.println("update: warning: count has gone below zero for this HasDuration subclass");
    }

    public boolean isDone(){
        return count == 0;
    }

    public void rst(){
        count = duration;
    }

}
