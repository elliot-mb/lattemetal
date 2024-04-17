import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAliasTable {
    private final Map<Integer, CircluarQueue<Integer>> resultStatus;

    private static int MEM_OFFSET = RegisterName.values().length;
    private final ReorderBuffer rob;
    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    RegisterAliasTable(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.resultStatus = new HashMap<Integer, CircluarQueue<Integer>>();
        this.rob = rob;
    }

    public Integer getLatestAlias(RegisterName reg){
        return resultStatus.get(reg.ordinal()).peekHead(); // peek the item under where we're adding(latest)
    }
    public boolean isRegValAtRobAndReady(RegisterName reg){
        return resultStatus.containsKey(reg.ordinal()) && rob.getEntry(getLatestAlias(reg)).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isRegValUnmapped(RegisterName reg){
        return !resultStatus.containsKey(reg.ordinal());
    }
    public boolean isRegValReady(RegisterName reg){
        return isRegValUnmapped(reg) || isRegValAtRobAndReady(reg);
    }
    public void regValIsReady(RegisterName r) { resultStatus.remove(r.ordinal()); } // there is no res station it corresponds to

    public int whereRegInRob(RegisterName reg){
        return getLatestAlias(reg);
    }

    public void pointRegAtRobEntry(RegisterName reg, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        resultStatus.get(reg.ordinal()).push(robEntry);
    }
}
