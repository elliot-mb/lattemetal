

import java.util.List;

public class IssueUnit extends Unit{

    private enum unitType{ exe, lsu, bru };

    private final RegisterFile rf;

    private final ReorderBuffer rob;

    private final RegisterAliasTable prf;

    private final DecodeUnit dec;

    //                          vv update rob      vv put mapping of rob to dest into prf
    IssueUnit(RegisterFile rf, ReorderBuffer rob, DecodeUnit dec, RegisterAliasTable prf, PipeLike[] ins, PipeLike[] outs){
        super(ins, outs);
        this.rf = rf;
        this.rob = rob;
        this.prf = prf;
        this.dec = dec;
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
    protected boolean isUnfinished() { //is when we stall basically
        return rob.isFull() || !outs[chooseUnit().ordinal()].canPush() || dec.noPrfsFree(); //if we cant push to the right unit! block otherwise...
    }

    @Override
    protected String writeUnfinished(){
        return dec.noPrfsFree() ? "PRF" : !outs[chooseUnit().ordinal()].canPush() ? "NRS" : "ROB";
    }

    private unitType chooseUnit(){
        if(Utils.isBranch(currentOp)) return unitType.bru;
        if(Utils.isLoadStore(currentOp)) return unitType.lsu;
        return unitType.exe;
    }

    @Override
    protected void chooseOuts(){
        //choose latter pipereg (index 1) if its a load/store, otherwise stick it to the alu
        if(Utils.isBranch(currentOp)) {
            outsChoice[2] = true;
            return;
        }
        if(Utils.isLoadStore(currentOp)) {
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
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.AddI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.Mul op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.MulI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.Cmp op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.Ld op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.LdC op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.LdI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRd().ordinal(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.St op) {
        ReorderEntry e = new ReorderEntry(op, op.getRsVal() + op.getIm(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.StI op) {
        ReorderEntry e = new ReorderEntry(op, op.getRsVal() + op.getIm(), pcVal);
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.BrLZ op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST, op.getResult());
        e.setValue(flag ? BranchUnit.TAKEN : BranchUnit.NOT_TAKEN, ReorderEntry.SND); //use the unused reorder entry to store whether the branch was taken
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.JpLZ op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST, op.getResult());
        e.setValue(flag ? BranchUnit.TAKEN : BranchUnit.NOT_TAKEN, ReorderEntry.SND); //use the unused reorder entry to store whether the branch was taken
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.Br op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST, op.getResult());
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.Jp op) {
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST, op.getResult());
        currentRobEntry = e.id;
        dec.usePrfs(op, e.id);
        rob.add(e);
    }

    @Override
    public void accept(Op.No op){
        ReorderEntry e = new ReorderEntry(op, ReorderBuffer.NO_DEST, pcVal);
        currentRobEntry = e.id;
        rob.add(e);
        rob.setEntryReady(currentRobEntry); //never let a no-op hold anything up, instantly ready
    }

    protected String showUnit(){
        return "IS";
    }
//    @Override
//    public String toString(){
//        return currentOp == null ? " " : "|";//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
//    }
}
