import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicalRegFile {
    private final Map<Integer, Integer> resultStatus;
    private static int MEM_OFFSET = RegisterName.values().length;
    private final ReorderBuffer rob;
    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    PhysicalRegFile(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.resultStatus = new HashMap<Integer, Integer>();
        this.rob = rob;
    }

    public boolean isRegValAtRobAndReady(RegisterName reg){
        return resultStatus.containsKey(reg.ordinal()) && rob.getEntry(resultStatus.get(reg.ordinal())).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isMemValAtRobAndReady(int mem){
        return resultStatus.containsKey(mem + MEM_OFFSET) && rob.getEntry(resultStatus.get(mem + MEM_OFFSET)).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isRegValUnmapped(RegisterName reg){
        return !resultStatus.containsKey(reg.ordinal());
    }
    public boolean isMemValUnmapped(int mem){
        return !resultStatus.containsKey(mem + MEM_OFFSET);
    }
    public boolean isRegValReady(RegisterName reg){
        return isRegValAtRobAndReady(reg) || isRegValUnmapped(reg);
    }
    public boolean isMemValReady(int mem){
        return isMemValAtRobAndReady(mem) || isMemValUnmapped(mem);
    }
    public void regValIsReady(RegisterName r) { resultStatus.remove(r.ordinal()); } // there is no res station it corresponds to

    public void memValIsReady(int mem) { resultStatus.remove(mem + MEM_OFFSET); }
    public int whereRegInRob(RegisterName reg){
        return resultStatus.get(reg.ordinal());
    }
    public int whereMemInRob(int memAddr){
        return resultStatus.get(memAddr + MEM_OFFSET);
    }
    public void pointRegAtRobEntry(RegisterName reg, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        resultStatus.put(reg.ordinal(), robEntry);
    }
    public void pointMemAtRobEntry(int memAddr, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        resultStatus.put(memAddr + MEM_OFFSET, robEntry);
    }
}
