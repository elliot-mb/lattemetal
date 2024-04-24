import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterAliasTable {
    private final Map<Integer, CircluarQueue<Integer>> regAlias;
    private final Map<Integer, CircluarQueue<Integer>> whichCDBEntry; //if we produce two results from an instruction, this tells us which cdb entry to look in


    private static int HISTORY_LENGTH = 64;

    private static int MEM_OFFSET = RegisterName.values().length;
    private final ReorderBuffer rob;
    private final int NUM_PHYS_REGS = 128; //four times that of the register file

    RegisterAliasTable(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.regAlias = new HashMap<Integer, CircluarQueue<Integer>>();
        for(int i = 0; i < RegisterName.values().length; i++){
            this.regAlias.put(i, new CircluarQueue<Integer>(HISTORY_LENGTH));
        }
        this.whichCDBEntry = new HashMap<Integer, CircluarQueue<Integer>>();
        for(int i = 0; i < RegisterName.values().length; i++){
            this.whichCDBEntry.put(i, new CircluarQueue<Integer>(HISTORY_LENGTH));
        }
        this.rob = rob;
    }

//    public boolean canMakeNewAlias(RegisterName reg){
//
//    }

    public Integer getLatestAlias(RegisterName reg){
        return regAlias.get(reg.ordinal()).peekHead(); // peek the item under where we're adding(latest)
    }
    public boolean isRegValAtRobAndReady(RegisterName reg){
        return regAlias.containsKey(reg.ordinal()) && getLatestAlias(reg) != null && rob.getEntry(getLatestAlias(reg)).isReady(); //if the register does not point anywhere it is ready, or if it
    }
    public boolean isRegValUnmapped(RegisterName reg){
        return regAlias.get(reg.ordinal()).isEmpty();
    }
    public boolean isRegValReady(RegisterName reg){
        return isRegValUnmapped(reg) || isRegValAtRobAndReady(reg);
    }
    public void regValIsReady(RegisterName reg) {
        if(!regAlias.containsKey(reg.ordinal())) throw new RuntimeException("regValIsReady: there is no key '" + reg.ordinal() + "' for register '" + reg.name() + "'");
        if(regAlias.get(reg.ordinal()).isEmpty()) throw new RuntimeException("regValIsReady: there is no alias for '" + reg.name() +"' in the RAT");
        regAlias.get(reg.ordinal()).pop();
        whichCDBEntry.get(reg.ordinal()).pop();
    } // there is no res station it corresponds to

    public int whereRegInRob(RegisterName reg){
        return getLatestAlias(reg);
    } // as we are in program order everything after an alias was added should reference this latest location in the rob
    // things before wont, but they will still be waiting for their prince charming to broadcast on the ROB and save them

    public void pointRegAtRobEntry(RegisterName reg, int robEntry, int entry) {
        if(regAlias.get(reg.ordinal()).isFull()) throw new RuntimeException("pointAtRobEntry: no more history left for register '" + reg.name() +"'");
        regAlias.get(reg.ordinal()).push(robEntry);
        whichCDBEntry.get(reg.ordinal()).push(entry);
    }

    //remove all references to rob entries after and including the passed in value!
    public void flushFrom(int fromRobEntry){
        for(Map.Entry<Integer, CircluarQueue<Integer>> e : regAlias.entrySet()){
            int resetKey = e.getKey();
            CircluarQueue<Integer> oldAliasQ = e.getValue();
            CircluarQueue<Integer> oldEntryQ = e.getValue();
            CircluarQueue<Integer> newAliasQ = new CircluarQueue<Integer>(HISTORY_LENGTH);
            CircluarQueue<Integer> newEntryQ = new CircluarQueue<Integer>(HISTORY_LENGTH);
            while(!oldAliasQ.isEmpty() && oldAliasQ.peek() < fromRobEntry){ //as soon as we meet or exceed fromRobEntry we stop transferring *(after the flush in program order)
                newAliasQ.push(oldAliasQ.pop()); //transfer those who're
                newEntryQ.push(oldEntryQ.pop());
            }
            regAlias.put(resetKey, newAliasQ); //queue with all values after the higher value in program order removed (perhaps this is wrong)??
            whichCDBEntry.put(resetKey, newEntryQ);
        }
    }

    public int cdbEntryOf(RegisterName reg){
        if(regAlias.get(reg.ordinal()).isEmpty()) throw new RuntimeException("cdbOfEntry: there is alias for this register");
        return whichCDBEntry.get(reg.ordinal()).peekHead();
    }

}
