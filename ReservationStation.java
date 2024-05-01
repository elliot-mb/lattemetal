import java.util.List;
import java.util.Map;

public class ReservationStation implements InstructionVoidVisitor {
    public static final int NO_DEPENDENCY = -1;
    public static final int DEFAULT_ENTRY = 0;
    public static int uId = 0;
    public final int id;
    private Instruction op;
    private int qJ, qK;
    private int sJ, sK; //decides the entries we use for reading from the cdb
    private int vJ, vK;
    private boolean rJ, rK, busy;
    private final ReorderBuffer rob;
    public int robEntry;
    private final Map<Integer, List<Integer>> cdb;



    ReservationStation(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.cdb = cdb;
        flush(Processor.FLUSH_ALL);
        this.id = uId;
        this.rob = rob;
        uId++;
    }

    public int getId(){
        return id;
    }

    public boolean isBusy(){
        return busy;
    }

    public void setIsBusy(boolean busy){
        this.busy = busy;
    }

    public Instruction getOp(){
        return op;
    }

    public void flush(int fromRobEntry){
        if(this.robEntry >= fromRobEntry){
            this.busy = false;
            this.op = null;
            this.qJ = NO_DEPENDENCY; //rob entry id
            this.sJ = DEFAULT_ENTRY;
            this.qK = NO_DEPENDENCY; //rob entry id
            this.sK = DEFAULT_ENTRY;
            this.vJ = 0;
            this.vK = 0;
            this.rJ = false;
            this.rK = false;
            this.robEntry = 0;
        }

    }
//    //this must happen in program order and so needs to be moved!
    public void set(PipelineEntry e, RegisterAliasTable rat, RegisterFile rf){

//        System.out.println("should be in order as we renamed : " + e.getEntry());
        op = e.getOp();
        if(Utils.isNoOP(op)) {
            qJ = NO_DEPENDENCY;
            rJ = true;
            qK = NO_DEPENDENCY;
            rK = true;
            busy = true;
            this.robEntry = e.getEntry();
            return;
        }
        List<Integer> sources = op.visit(new SourceLocVisitor());
        List<Integer> dest = op.visit(new DestLocVisitor());

        RegisterName regJ = sources.isEmpty() ? null : RegisterName.values()[sources.get(0)];
        RegisterName regK = sources.size() <= 1 ? null : RegisterName.values()[sources.get(1)];

        if(regJ != null && rat.isRegValReady(regJ)){
            vJ = rat.isRegValUnmapped(regJ) ? rf.getReg(regJ) : rob.getValOfEntry(rat.whereRegInRob(regJ), rat.cdbEntryOf(regJ));
            rJ = true; //ready up
            qJ = NO_DEPENDENCY;
        }else if(regJ != null){
            qJ = rat.whereRegInRob(regJ);
            sJ = rat.cdbEntryOf(regJ);
            rJ = false;
        }
        if(regJ == null){
            rJ = true;
        }
        if(regK != null && rat.isRegValReady(regK)){
            vK =  rat.isRegValUnmapped(regK) ? rf.getReg(regK) : rob.getValOfEntry(rat.whereRegInRob(regK), rat.cdbEntryOf(regK));
            rK = true; //ready up
            qK = NO_DEPENDENCY;
        }else if(regK != null){
            qK = rat.whereRegInRob(regK);
            sK = rat.cdbEntryOf(regK);
            rK = false;
        }
        if(regK == null){
            rK = true;
        }

        busy = true;

        int entry = 0;
        for(Integer d : dest){
            if(d != NO_DEPENDENCY) rat.pointRegAtRobEntry(RegisterName.values()[d], e.getEntry(), entry); //tell the register file to point at the rob entry of the instruction in this rs, IF there is a result
            entry++;
        }

        this.robEntry = e.getEntry();

        if(isReady()) op.visit(this); //updates fields in instruction in case its ready right away
    }

    //checks if either dependant RSs have finished!
    public void update(){
        if(qJ == NO_DEPENDENCY) rJ = true;
        if(qK == NO_DEPENDENCY) rK = true;
        if(qJ != NO_DEPENDENCY && cdb.containsKey(qJ)){ //qJ is memory location or register
            //System.out.println("RS #" +id + " read rob entry " + qJ + " has this result off cdb");
            vJ = cdb.get(qJ).get(sJ); //broadcast this  data on the first element of the list
            qJ = NO_DEPENDENCY;
            rJ = true;
        }
        if(qK != NO_DEPENDENCY && cdb.containsKey(qK)){
            //System.out.println("RS #" +id + " read rob entry " + qK + " has this result off cdb");
            vK = cdb.get(qK).get(sK);
            qK = NO_DEPENDENCY;
            rK = true;
        }
        op.visit(this); //put the updated values inside op
    }

    public boolean isReady(){
        return (qJ == NO_DEPENDENCY || rJ) && (qK == NO_DEPENDENCY || rK); //if qi is null that means there is no resevation station assigned to ti
    }

    public int getvJ(){
        if(!rJ) throw new RuntimeException("getvJ: reservation station has an unready value which cannot be read");
        return vJ;
    }

    public int getvK(){
        if(!rK) throw new RuntimeException("getvK: reservation station has an unready value which cannot be read");
        return vK;
    }

    @Override
    public String toString(){
        return "" + Utils.twoDigitInstrId(op) + "@" + id;
    }

    @Override
    public void accept(Op.Add op) {
        if(rJ) op.setRsVal(vJ);
        if(rK) op.setRtVal(vK);
    }

    @Override
    public void accept(Op.AddI op) {
        if(rJ) op.setRsVal(vJ);
    }

    @Override
    public void accept(Op.Mul op) {
        if(rJ) op.setRsVal(vJ);
        if(rK) op.setRtVal(vK);
    }

    @Override
    public void accept(Op.MulI op) {
        if(rJ) op.setRsVal(vJ);
    }

    @Override
    public void accept(Op.Cmp op) {
        if(rJ) op.setRsVal(vJ);
        if(rK) op.setRtVal(vK);
    }

    @Override
    public void accept(Op.Ld op) {
        if(rJ) op.setRsVal(vJ);
        if(rJ) op.setResult(vJ + op.getImVal()); //set so we can tell if we have calculated the address, from the rob
    }

    @Override
    public void accept(Op.LdC op) {
        op.setResult(op.getImVal());
    }

    @Override
    public void accept(Op.LdI op) {
        if(rJ) op.setRsVal(vJ);
        if(rJ) op.setResult(vJ);
    }

    @Override
    public void accept(Op.St op) {
        if(rJ) op.setRdVal(vJ);
        if(rJ) op.setRsVal(vK);
    }

    @Override
    public void accept(Op.StI op) {
        if(rJ) op.setRdVal(vJ);
        if(rK) op.setRsVal(vK);
    }

    @Override
    public void accept(Op.BrLZ op) {
        if(rJ) op.setRdVal(vJ);
    }

    @Override
    public void accept(Op.JpLZ op) {
        if(rJ) op.setRdVal(vJ);
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
}
