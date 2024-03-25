

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

public class Scoreboard implements InstructionVoidVisitor {

    private final AbstractMap<RegisterName, Boolean> scoreboard = new HashMap<RegisterName, Boolean>();

    private ArrayList<RegisterName> dependencies;

    Scoreboard(){
        flush();
    }

    public void flush(){
        for(RegisterName r : RegisterName.values()){
            scoreboard.put(r, true); // register => valid
            // we can execute an instruction just if its dependencies are set true in here
        }
    }

    //return true if we can execute this, and set its result as changing
    //otherwise return false as it is currently being used
    public ArrayList<RegisterName> useOrHasDeps(Instruction op){
        dependencies = new ArrayList<RegisterName>();
        op.visit(this);
        return dependencies; //set in visitation
    }

    private void useRegs(RegisterName ...rs){
        for(RegisterName r : rs){
            scoreboard.put(r, false);
        }
    }

    public void validateReg(RegisterName r){
        //System.out.println("freed '" + r.name() + "'");
        scoreboard.put(r, true);
    }

    private void regsInUse(RegisterName... rs){
        for(RegisterName r : rs) {
            if (!scoreboard.get(r)) {
                dependencies.add(r);
            }
        }
    }

    private boolean hasDeps(){
        return dependencies.size() > 0;
    }

    @Override
    public void accept(Op.Add op) {
        //hazards:                  WAW         RAW         RAW
        regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.AddI op) {
        regsInUse(op.getRd(), op.getRs());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Mul op) {
        regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.MulI op) {
        regsInUse(op.getRd(), op.getRs());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Cmp op) {
        regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Ld op) {
        regsInUse(op.getRd(), op.getRs());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.LdC op) {
        regsInUse(op.getRd());
        if(hasDeps()) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.St op) {
        //                          WAW         RAW
        regsInUse(op.getRd(), op.getRs());
        //changes nothing in register file
    }

    @Override
    public void accept(Op.BrLZ op) {
        regsInUse(op.getRd());
        //changes nothing in register file
    }

    @Override
    public void accept(Op.JpLZ op) {
        regsInUse(op.getRd());
        //changes nothing in register file
    }

    @Override
    public void accept(Op.Br op) {
        //changes nothing in register file
    }

    @Override
    public void accept(Op.Jp op) {
        //changes nothing in register file
    }

    @Override
    public void accept(Op.No op) {
        //changes nothing in register file
    }
}
