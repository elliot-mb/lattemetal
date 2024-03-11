import java.util.AbstractMap;
import java.util.HashMap;

public class Scoreboard implements InstructionVoidVisitor {

    private final AbstractMap<RegisterName, Boolean> scoreboard = new HashMap<RegisterName, Boolean>();

    private boolean hasDependencies = false;

    Scoreboard(){
        for(RegisterName r : RegisterName.values()){
            scoreboard.put(r, true); // register => valid
            // we can execute an instruction just if its dependencies aren't set true in here
        }
    }

    //return true if we can execute this, and set its result as changing
    //otherwise return false as it is currently being used
    public boolean useOrHasDeps(Instruction op){
        hasDependencies = false;
        op.visit(this);
        return hasDependencies; //set in visitation
    }

    private void useRegs(RegisterName ...rs){
        for(RegisterName r : rs){
            scoreboard.put(r, false);
        }
    }

    private boolean regsInUse(RegisterName... rs){
        boolean inUse = true;
        for(RegisterName r : rs){
            inUse = inUse && scoreboard.get(r); //false or if any are being used we return true
        }
        return inUse;
    }

    @Override
    public void accept(Op.Add op) {
        //hazards:                  WAW         RAW         RAW
        hasDependencies = regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.AddI op) {
        hasDependencies = regsInUse(op.getRd(), op.getRs());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Mul op) {
        hasDependencies = regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.MulI op) {
        hasDependencies = regsInUse(op.getRd(), op.getRs());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Cmp op) {
        hasDependencies = regsInUse(op.getRd(), op.getRs(), op.getRt());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.Ld op) {
        hasDependencies = regsInUse(op.getRd(), op.getRs());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.LdC op) {
        hasDependencies = regsInUse(op.getRd());
        if(hasDependencies) return;
        useRegs(op.getRd());
    }

    @Override
    public void accept(Op.St op) {
        //                          WAW         RAW
        hasDependencies = regsInUse(op.getRd(), op.getRs());
        //changes nothing in register file
    }

    @Override
    public void accept(Op.BrLZ op) {
        hasDependencies = regsInUse(op.getRd());
        //changes nothing in register file
    }

    @Override
    public void accept(Op.JpLZ op) {
        hasDependencies = regsInUse(op.getRd());
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
