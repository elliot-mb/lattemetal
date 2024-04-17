import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationGroup implements PipeLike, InstructionVoidVisitor{ //to set up grouped reservation stations!

    private final List<ReservationStation> rss;

    private int currentRobEntry;
    private ReservationStation currentRes;

    private static final int NO_INDEX = -1;

    private final RegisterFile rf;

    private final RegisterAliasTable rat;

    private final Map<Integer, PipelineEntry> rsToEntry; //so we can load all the other junk that comes with a pipeiregentry

    ReservationGroup(int size, Map<Integer, List<Integer>> cdb, ReorderBuffer rob, RegisterFile rf, RegisterAliasTable rat){
        this.rss = new ArrayList<ReservationStation>(size);
        for(int i = 0; i < size; i++){
            rss.add(new ReservationStation(cdb, rob));
        }
        this.rf = rf;
        this.rat = rat;
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
    private int rsWithOldestOpReady(){
        int oldestInstructionId = Integer.MAX_VALUE;
        int i = 0;
        int index = NO_INDEX;
        for(ReservationStation rs : rss){
            if(rsToEntry.containsKey(i) && rs.isReady() && oldestInstructionId > rs.getOp().getId()) {
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
//    public Instruction requestOpOrNull(ReorderBuffer rob){
//        Instruction ret = null;
//        int index = rsWithOldestOp();
//        if(index != -1) {
//            ReservationStation rs = rss.get(index);
//            currentRobEntry = rs.robEntry;
//            ret = rob.getEntry(currentRobEntry).getOp(); //get it right from the rob so its the same reference! (we need to modify op fields...)
//            ret.rst();
//            currentRs = index; //should only be reset after we finish processing stuff
//        }
//        return ret;
//    }

    public int getCurrentRobEntry(){
        return currentRobEntry;
    }

    public ReservationStation getCurrentRes(){
        return currentRes;
    }

    public List<ReservationStation> getFreeRss(){
        List<ReservationStation> freeRss = new ArrayList<ReservationStation>();
        for(ReservationStation rs : rss){
            if(!rs.isBusy()) freeRss.add(rs);
        }
        return freeRss;
    }

    public List<ReservationStation> getReadyAndFullRss(){
        List<ReservationStation> readyRss = new ArrayList<ReservationStation>();
        for(ReservationStation rs : rss){
            if(rs.isReady() && rs.getOp() != null) readyRss.add(rs);
        }
        return readyRss;
    }

    @Override
    public boolean canPull() {
        return rsToEntry.size() > 0 && getReadyAndFullRss().size() > 0;
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
//        PipelineEntry fresh = e.copy();
        firstFree.set(e, rat, rf); //first free reservation station
        rsToEntry.put(index, e);
    }

    @Override
    public PipelineEntry pull() {
        if(!canPull()) throw new RuntimeException("pull: no mappings to pipeentries");
        int index = rsWithOldestOpReady();
        currentRes = rss.get(index);
        PipelineEntry e = rsToEntry.get(index);

        //visitation that fills in fields from reservation station
        currentRes.getOp().visit(this);
        PipelineEntry ret = new PipelineEntry(currentRes.getOp(), e.getPcVal(), e.getFlag(), e.getEntry());

        //cleanup
        rsToEntry.remove(index); //dont map this reservation station to an entry
        currentRes.setIsBusy(false);
        currentRes.flush();
        currentRes = null;

        return ret;
    }

    @Override
    public PipelineEntry peek() {
        return rsToEntry.get(rsWithOldestOpReady());
    }

    // visitation
    // visitation to update values in instructions!
    // visitation

    @Override
    public void accept(Op.Add op) {
        op.setRsVal(currentRes.getvJ());
        op.setRtVal(currentRes.getvK()); //assign what we know from the reservation stations!
    }

    @Override
    public void accept(Op.AddI op) {
        op.setRsVal(currentRes.getvJ());
    }

    @Override
    public void accept(Op.Mul op) {
        op.setRsVal(currentRes.getvJ());
        op.setRtVal(currentRes.getvK());
    }

    @Override
    public void accept(Op.MulI op) {
        op.setRsVal(currentRes.getvJ());
    }

    @Override
    public void accept(Op.Cmp op) {
        op.setRsVal(currentRes.getvJ());
        op.setRtVal(currentRes.getvK());
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRsVal(currentRes.getvJ());
    }

    @Override
    public void accept(Op.LdC op) {
        //no register read dependencies
    }

    @Override
    public void accept(Op.St op) {
        op.setRsVal(currentRes.getvK()); //second dependant is needed for address calc
    }

    @Override
    public void accept(Op.BrLZ op) {
        op.setRdVal(currentRes.getvJ());
    }

    @Override
    public void accept(Op.JpLZ op) {
        op.setRdVal(currentRes.getvJ());
    }

    @Override
    public void accept(Op.Br op) {

    }

    @Override
    public void accept(Op.Jp op) {

    }

    @Override
    public void accept(Op.No op) {

    }

    @Override
    public String toString(){
        return rsToEntry.toString();
    }
}
