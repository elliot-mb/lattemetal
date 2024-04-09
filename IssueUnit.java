

import java.util.ArrayList;
import java.util.List;

public class IssueUnit extends Unit{

    private final RegisterFile rf;

    private final ReorderBuffer rob;

    private final List<ReservationStation> aluRs;
    private final List<ReservationStation> lsuRs;
    private final PhysicalRegFile prf;

    private int currentRobEntry;
    //                          vv update rob      vv put mapping of rob to dest into prf
    IssueUnit(RegisterFile rf, ReorderBuffer rob, PhysicalRegFile prf, List<ReservationStation> aluRs, List<ReservationStation> lsuRs, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.rf = rf;
        this.rob = rob;
        this.aluRs = aluRs;
        this.lsuRs = lsuRs;
        this.prf = prf;
    }

    private boolean isAnRsFree(List<ReservationStation> rss){
        for(ReservationStation rs : rss){
            if(!rs.isBusy()) return true;
        }
        return false;
    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        //copyToDepsC(deps);
        //dependencies.add(Lookup.reg.get("zero")); // initial fake dependency just to get it to check
    }

    @Override
    protected void procInstruction() {

        //must run just once to avoid false positive of trying to register the instruction twice
        //...once we realise it doesnt have deps, we dont try to register it again
        //if(hasDeps()) deps = sb.useOrHasDeps(currentOp);
    }

    @Override
    public void flush(){
        super.flush();
    }

    @Override
    protected boolean isUnfinished() {
        return rob.isFull() || (!sendToAlu(currentOp) && !isAnRsFree(lsuRs)) || (sendToAlu(currentOp) && !isAnRsFree(aluRs));
    }

    private boolean sendToAlu(Instruction op){
        return !Utils.isLoadStore(op);
    }

    @Override
    protected void chooseOuts(){
        //choose latter pipereg (index 1) if its a load/store, otherwise stick it to the alu
        if(!sendToAlu(currentOp)) {
            outsChoice[1] = true;
            return;
        }
        outsChoice[0] = true;
    }

    @Override
    protected PipelineEntry makeEntryToWrite(){


        return new PipelineEntry(currentOp, pcVal, flag, currentRobEntry); //send currentRobEntry to resi station!
    }

    // write the required dependencies into

    @Override
    public void accept(Op.Add op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.AddI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.Mul op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.MulI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.Cmp op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.Ld op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.LdC op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.St op) {
        ReorderEntry e = new ReorderEntry(op, op.getRsVal() + op.getIm());
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.BrLZ op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST);
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.JpLZ op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST);
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.Br op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST);
        currentRobEntry = e.id;
        rob.add(e);
    }

    @Override
    public void accept(Op.Jp op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST);
        currentRobEntry = e.id;
        rob.add(e);
    }

    protected String showUnit(){
        return "IS";
    }
//    @Override
//    public String toString(){
//        return currentOp == null ? " " : "|";//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
//    }
}
