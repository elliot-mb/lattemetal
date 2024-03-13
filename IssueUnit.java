

import java.util.AbstractMap;
import java.util.HashMap;

public class IssueUnit extends Unit{

    private final Scoreboard sb;
    private boolean hasDeps = false;

    IssueUnit(Scoreboard sb, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.sb = sb;

    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        hasDeps = sb.useOrHasDeps(currentOp); //must run just once to avoid false positive of trying to register the instruction twice
    }

    @Override
    protected void procInstruction() {
        //nothing
    }

    @Override
    public void flush(){
        super.flush();
        hasDeps = false;
        sb.flush();
    }

    @Override
    protected boolean isUnfinished() {
        return hasDeps;
    }

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

}
