

import java.util.ArrayList;

public class IssueUnit extends Unit{

    private final Scoreboard sb;
    private final RegisterFile rf;
    private ArrayList<RegisterName> deps;
    private ArrayList<RegisterName> depsInital;

    IssueUnit(Scoreboard sb, RegisterFile rf, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.sb = sb;
        this.rf = rf;
        this.deps = new ArrayList<>();
        this.depsInital = new ArrayList<>();
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        deps = sb.useOrHasDeps(currentOp);
        copyToDepsC(deps);
        //dependencies.add(Lookup.reg.get("zero")); // initial fake dependency just to get it to check
    }

    //we fetch the latest operands from the rf because they were updated since we decoded if we block here
    //for a few cycles!
    @Override
    protected void writeOnPipeline(){
//        Forwarder f = new Forwarder(); // mini forwarder just used for updating dependencies
//        for(RegisterName d : depsInital){
//            f.setSlot(rf.getReg(d));
//            f.setSlotReg(d);
//            currentOp = f.forward(currentOp);
//        }
        super.writeOnPipeline();
    }

    private boolean hasDeps(){
        return deps.size() > 0;
    }

    private void copyToDepsC(ArrayList<RegisterName> deps) {
        depsInital.clear();
        for(RegisterName d : deps){
            depsInital.add(d);
        }
    }

    @Override
    protected void procInstruction() {
        //must run just once to avoid false positive of trying to register the instruction twice
        //...once we realise it doesnt have deps, we dont try to register it again
        if(hasDeps()) deps = sb.useOrHasDeps(currentOp);
    }

    @Override
    public void flush(){
        super.flush();
        deps = new ArrayList<RegisterName>();
        sb.flush();
    }

    @Override
    protected boolean isUnfinished() {
        return hasDeps();
    }

    // write the required dependencies into

    @Override
    public void accept(Op.Add op) {

    }

    @Override
    public void accept(Op.AddI op) {

    }

    @Override
    public void accept(Op.Mul op) {

    }

    @Override
    public void accept(Op.MulI op) {

    }

    @Override
    public void accept(Op.Cmp op) {

    }

    @Override
    public void accept(Op.Ld op) {

    }

    @Override
    public void accept(Op.LdC op) {

    }

    @Override
    public void accept(Op.St op) {

    }

    @Override
    public void accept(Op.BrLZ op) {

    }

    @Override
    public void accept(Op.JpLZ op) {

    }

    @Override
    public void accept(Op.Br op) {

    }

    @Override
    public void accept(Op.Jp op) {

    }

    @Override
    public String toString(){
        return currentOp == null ? " " : "|";//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
    }
}
