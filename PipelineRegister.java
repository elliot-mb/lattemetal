public class PipelineRegister {

    //cannot be final because it MUST change
    private Instruction inFlight;
    private Integer pcVal;
    private boolean flag; //used for a 'branch taken' bit

    PipelineRegister(){
        inFlight = null;
        pcVal = null;
        flag = false;
    }

    public void setPcVal(int count){
        pcVal = count;
    }

    public Integer getPcVal(){
        if(pcVal == null) throw new RuntimeException("getPc: pc is null and was not set");
        return pcVal;
    }

    public void setFlag(boolean val){
        flag = val;
    }

    public boolean isFlag(){
        return flag;
    }

    //push can stall (just if inFlight is not null)
    public void push(Instruction op){
        if(inFlight != null) throw new RuntimeException("push: pushing to a full pipereg");
        inFlight = op.copy(); //copy it in
    }

    //can return null, indicating a stall
    public Instruction pull(){
        if(inFlight == null) throw new RuntimeException("pull: pulling from an empty pipereg");
        Instruction result = inFlight.copy(); //copy it out, meaning we cant mutate the instruction inside even if we pull it out
        inFlight = null; //reset
        pcVal = null;
        flag = false;
        return result;
    }

    public boolean canPush(){
        return inFlight == null;
    }

    public boolean canPull(){
        return inFlight != null;
    }

    public String toString(){
        return "" + (canPull() ? Integer.toHexString(inFlight.getId() % 16) : "_");
    }
}