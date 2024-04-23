import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAliasTable {
    private final Map<Integer, CircluarQueue<Integer>> resultStatus;

    private static int HISTORY_LENGTH = 64;

    private static int MEM_OFFSET = RegisterName.values().length;
    private final ReorderBuffer rob;
    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    RegisterAliasTable(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.resultStatus = new HashMap<Integer, CircluarQueue<Integer>>();
        for(int i = 0; i < RegisterName.values().length; i++){
            this.resultStatus.put(i, new CircluarQueue<Integer>(HISTORY_LENGTH));
        }
        this.rob = rob;
    }

//    public boolean canMakeNewAlias(RegisterName reg){
//
//    }

    public Integer getLatestAlias(RegisterName reg){
        return resultStatus.get(reg.ordinal()).peekHead(); // peek the item under where we're adding(latest)
    }
    public boolean isRegValAtRobAndReady(RegisterName reg){
        return resultStatus.containsKey(reg.ordinal()) && getLatestAlias(reg) != null && rob.getEntry(getLatestAlias(reg)).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isRegValUnmapped(RegisterName reg){
        return resultStatus.get(reg.ordinal()).isEmpty();
    }
    public boolean isRegValReady(RegisterName reg){
        return isRegValUnmapped(reg) || isRegValAtRobAndReady(reg);
    }
    public void regValIsReady(RegisterName reg) {
        if(!resultStatus.containsKey(reg.ordinal())) throw new RuntimeException("regValIsReady: there is no key '" + reg.ordinal() + "' for register '" + reg.name() + "'");
        if(resultStatus.get(reg.ordinal()).isEmpty()) throw new RuntimeException("regValIsReady: there is no alias for '" + reg.name() +"' in the RAT");
        resultStatus.get(reg.ordinal()).pop();
    } // there is no res station it corresponds to

    public int whereRegInRob(RegisterName reg){
        return getLatestAlias(reg);
    } // as we are in program order everything after an alias was added should reference this latest location in the rob
    // things before wont, but they will still be waiting for their prince charming to broadcast on the ROB and save them

    public void pointRegAtRobEntry(RegisterName reg, int robEntry) {
        if(resultStatus.size() >= NUM_PHYS_REGS) throw new RuntimeException("pointAtRobEntry: no more physical registers left!");
        if(resultStatus.get(reg.ordinal()).isFull()) throw new RuntimeException("pointAtRobEntry: no more aliases left for this register '" + reg.name() +"'");
        resultStatus.get(reg.ordinal()).push(robEntry);
    }

    //remove all references to rob entries after and including the passed in value!
    public void flushFrom(int fromRobEntry){
        for(Map.Entry<Integer, CircluarQueue<Integer>> e : resultStatus.entrySet()){
            int resetKey = e.getKey();
            CircluarQueue<Integer> oldQ = e.getValue();
            CircluarQueue<Integer> newQ = new CircluarQueue<Integer>(HISTORY_LENGTH);
            while(!oldQ.isEmpty() && oldQ.peek() < fromRobEntry){ //as soon as we meet or exceed fromRobEntry we stop transferring *(after the flush in program order)
                newQ.push(oldQ.pop()); //transfer those who're
            }
            resultStatus.put(resetKey, newQ); //queue with all values after the higher value in program order removed (perhaps this is wrong)??
        }
    }


}
