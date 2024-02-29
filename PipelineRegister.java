public class PipelineRegister {

    //cannot be final because it MUST change
    private Instruction inFlight = null;
    private Integer pc = null;
    private boolean flag = false; //used for a 'branch taken' bit

    PipelineRegister(){}

    public void setPc(int count){
        pc = count;
    }

    public Integer getPc(){
        if(pc == null) throw new RuntimeException("getPc: pc is null and was not set");
        return pc;
    }

    public void setFlag(boolean val){
        flag = val;
    }

    public boolean isFlag(){
        return flag;
    }

    //push can stall (just if inFlight is not null)
    public boolean push(Instruction op){
        if(inFlight != null) return false;
        inFlight = op.copy(); //copy it in
        return true;
    }

    //can return null, indicating a stall
    public Instruction pull(){
        if(inFlight == null) return null; //this must be handled by the puller
        return inFlight.copy(); //copy it out, meaning we cant mutate the instruction inside even if we pull it out
    }

    public boolean canPush(){
        return inFlight == null;
    }

    public boolean canPull(){
        return pull() == null;
    }
}