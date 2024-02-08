public class PipelineRegister {

    //cannot be final because it MUST change
    private Instruction inFlight = null;

    PipelineRegister(){}

    //push can stall (just if inFlight is not null)
    public boolean push(Instruction op){
        if(inFlight != null) return false;
        inFlight = op.copy(); //copy it in
        return true;
    }

    //can return null, causing a stall
    public Instruction pull(){
        if(inFlight == null) return null; //this must be handled by the puller
        return inFlight.copy(); //copy it out, meaning we cant mutate the instruction inside even if we pull it out
    }
}
