

import java.util.ArrayList;

public class IssueUnit extends Unit{

    private final RegisterFile rf;

    private final ReorderBuffer rob;

    IssueUnit(RegisterFile rf, ReorderBuffer rob, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.rf = rf;
        this.rob = rob;
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
        return false;
    }

    @Override
    protected void chooseOuts(){
        //choose latter pipereg (index 1) if its a load/store, otherwise stick it to the alu
        if(Utils.isLoadStore(currentOp)) {
            outsChoice[1] = true;
            return;
        }
        outsChoice[0] = true;
    }

    // write the required dependencies into

    @Override
    public void accept(Op.Add op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.AddI op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.Mul op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.MulI op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.Cmp op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.Ld op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.LdC op) {
        rob.add(new ReorderEntry(op, op.getRd().ordinal()));
    }

    @Override
    public void accept(Op.St op) {
        rob.add(new ReorderEntry(op, op.getRsVal() + op.getIm()));
    }

    @Override
    public void accept(Op.BrLZ op) {
        rob.add(new ReorderEntry(op, ReorderBuffer.NO_DEST));
    }

    @Override
    public void accept(Op.JpLZ op) {
        rob.add(new ReorderEntry(op, ReorderBuffer.NO_DEST));
    }

    @Override
    public void accept(Op.Br op) {
        rob.add(new ReorderEntry(op, ReorderBuffer.NO_DEST));
    }

    @Override
    public void accept(Op.Jp op) {
        rob.add(new ReorderEntry(op, ReorderBuffer.NO_DEST));
    }

    protected String showUnit(){
        return "IS";
    }
//    @Override
//    public String toString(){
//        return currentOp == null ? " " : "|";//(currentOp != null ? Integer.toHexString(currentOp.getId() % 16) : "_");
//    }
}
