import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationGroup implements TubeLike{ //to set up grouped reservation stations!

    private final List<ReservationStation> rss;

    private int currentRobEntry;
    private int currentRs;

    private static final int NO_INDEX = -1;

    private final RegisterFile rf;

    private final PhysicalRegFile prf;

    private final Map<Integer, PipelineEntry> rsToEntry; //so we can load all the other junk that comes with a pipeiregentry

    ReservationGroup(int size, Map<Integer, List<Integer>> cdb, ReorderBuffer rob, RegisterFile rf, PhysicalRegFile prf,){
        this.rss = new ArrayList<ReservationStation>(size);
        for(int i = 0; i < size; i++){
            rss.add(new ReservationStation(cdb, rob));
        }
        this.rf = rf;
        this.prf = prf;
        this.rsToEntry = new HashMap<Integer, PipelineEntry>();
    }

    public void update(){
        for(ReservationStation rs : rss){
            if(rs.isBusy()) rs.update(); //dont update those with no instruction inside
        }
    }

    private int rsFirstFree(){
        int i = 0;
        for(ReservationStation rs : rss){
            if(!rs.isBusy()) return i;
            i++;
        }
        return NO_INDEX;
    }

    /**
     * see whos available
     * @return index of oldest ready reservation station
     */
    private int rsWithOldestOp(){
        int oldestInstructionId = Integer.MAX_VALUE;
        int i = 0;
        int index = NO_INDEX;
        for(ReservationStation rs : rss){
            if(rs.isBusy() && rs.isReady() && oldestInstructionId > rs.getOp().getId()) {
                index = i;
                oldestInstructionId = rs.getOp().getId();
            }
            i++;
        }
        return index;
    }

    /**
     *
     * @return null if there are no reservation stations ready to go
     */
    public Instruction requestOpOrNull(ReorderBuffer rob){
        Instruction ret = null;
        int index = rsWithOldestOp();
        if(index != -1) {
            ReservationStation rs = rss.get(index);
            currentRobEntry = rs.robEntry;
            ret = rob.getEntry(currentRobEntry).getOp(); //get it right from the rob so its the same reference! (we need to modify op fields...)
            ret.rst();
            currentRs = index; //should only be reset after we finish processing stuff
        }
        return ret;
    }

    public int getCurrentRobEntry(){
        return currentRobEntry;
    }

    public int getCurrentRs(){
        return currentRs;
    }

    public List<ReservationStation> getFreeRss(){
        List<ReservationStation> freeRss = new ArrayList<ReservationStation>();
        for(ReservationStation rs : rss){
            if(!rs.isBusy()) freeRss.add(rs);
        }
        return freeRss;
    }

    public List<ReservationStation> getReadyRss(){
        List<ReservationStation> readyRss = new ArrayList<ReservationStation>();
        for(ReservationStation rs : rss){
            if(rs.isReady()) readyRss.add(rs);
        }
        return readyRss;
    }

    @Override
    public boolean canPull() {
        return getReadyRss().size() > 0;
    }

    @Override
    public boolean canPush() {
        return getFreeRss().size() > 0;
    }

    @Override
    public void flush() {
        for(ReservationStation rs: rss){
            rs.flush();
        }
    }

    @Override
    public void push(PipelineEntry e) {
        if(!canPush()) throw new RuntimeException("push: no free reservation stations");
        int index = rsFirstFree(); //cannot be -1 as we have just confirmed we can in fact push
        ReservationStation firstFree = rss.get(index);
        PipelineEntry fresh = e.copy();
        firstFree.set(fresh, prf, rf); //first free reservation station
        rsToEntry.put(index, fresh);
    }

    @Override
    public PipelineEntry pull() {
        if(!canPull()) throw new RuntimeException("pull: no ready reservation stations");
        int index = rsWithOldestOp();
        ReservationStation rs = rss.get(index);
        PipelineEntry fresh = rsToEntry.get(index).copy();
        return new PipelineEntry(rs.getOp(), fresh.getPcVal(), fresh.getFlag(), fresh.getEntry());
    }

    @Override
    public PipelineEntry peek() {
        return null;
    }
}
