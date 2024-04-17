import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAliasTable {
    private final Map<Integer, CircluarQueue<Integer>> resultStatus;
    //we pop when we commit a destination register
    //we refere to where we add to items in the queue when we create a new dependency

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
//    public boolean isMemValAtRobAndReady(int mem){
//        return resultStatus.containsKey(mem + MEM_OFFSET) && rob.getEntry(resultStatus.get(mem + MEM_OFFSET)).isReady(); //if the register does not point anywhere it is ready, or if it
//    }
    public boolean isRegValUnmapped(RegisterName reg){
        return !resultStatus.containsKey(reg.ordinal());
    }
//    public boolean isMemValUnmapped(int mem){
//        return !resultStatus.containsKey(mem + MEM_OFFSET);
//    }
    public boolean isRegValReady(RegisterName reg){
        return isRegValUnmapped(reg) || isRegValAtRobAndReady(reg);
    }
//    public boolean isMemValReady(int mem){
//        return  isMemValUnmapped(mem) || isMemValAtRobAndReady(mem);
//    }
    public void regValIsReady(RegisterName r) { resultStatus.remove(r.ordinal()); } // there is no res station it corresponds to

//    public void memValIsReady(int mem) { resultStatus.remove(mem + MEM_OFFSET); }
    public int whereRegInRob(RegisterName reg){
        return getLatestAlias(reg);
    }
//    public int whereMemInRob(int memAddr){
//        return resultStatus.get(memAddr + MEM_OFFSET);
//    }
    public void pointRegAtRobEntry(RegisterName reg, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        resultStatus.get(reg.ordinal()).push(robEntry);
    }
//    public void pointMemAtRobEntry(int memAddr, int robEntry) {
//        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
//        resultStatus.put(memAddr + MEM_OFFSET, robEntry);
//    }
}
