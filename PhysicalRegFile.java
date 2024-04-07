import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicalRegFile {
    private final Map<Integer, Integer> resultStatus;

    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    PhysicalRegFile(Map<Integer, List<Integer>> cdb){
        this.resultStatus = new HashMap<Integer, Integer>();
    }

    public boolean isRegValReady(int r){
        return !resultStatus.containsKey(r); //if the register does not point anywhere it is ready
    }
    public void destValIsReady(int r) { resultStatus.remove(r); } // there is no res station it corresponds to
    public int whereDestVal(int r){
        return resultStatus.get(r);
    }
    public void pointAtRobEntry(RegisterName r, int robEntry) { resultStatus.put(r.ordinal(), robEntry); }
}
