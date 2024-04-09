import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicalRegFile {
    private final Map<Integer, Integer> resultStatus;

    private final ReorderBuffer rob;

    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    PhysicalRegFile(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.resultStatus = new HashMap<Integer, Integer>();
        this.rob = rob;
    }

    public boolean isDestValAtRobAndReady(int r){
        return resultStatus.containsKey(r) && rob.getEntry(resultStatus.get(r)).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isDestValUnmapped(int r){
        return !resultStatus.containsKey(r);
    }
    public boolean isRegValReady(int r){
        return isDestValAtRobAndReady(r) || isDestValUnmapped(r);
    }
    public void destValIsReady(int r) { resultStatus.remove(r); } // there is no res station it corresponds to
    public int whereInRob(int r){
        return resultStatus.get(r);
    }
    public void pointAtRobEntry(int r, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        resultStatus.put(r, robEntry);
    }
}
