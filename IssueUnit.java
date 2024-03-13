

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IssueUnit extends Unit{

    private final Scoreboard sb;
    private ArrayList<RegisterName> dependencies = new ArrayList<RegisterName>();

    IssueUnit(Scoreboard sb, PipelineRegister last, PipelineRegister next){
        super(last, next);
        this.sb = sb;

    }

    @Override
    protected void readOffPipeline(){
        super.readOffPipeline();
        dependencies = new ArrayList<RegisterName>();
        dependencies.add(Lookup.reg.get("zero")); // initial fake dependency just to get it to check
    }

    private boolean hasDeps(){
        return dependencies.size() > 0;
    }

    @Override
    protected void procInstruction() {
        //must run just once to avoid false positive of trying to register the instruction twice
        //...once we realise it doesnt have deps, we dont try to register it again
        if(hasDeps()) dependencies = sb.useOrHasDeps(currentOp);
    }

    @Override
    public void flush(){
        super.flush();
        dependencies = new ArrayList<RegisterName>();
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

}
