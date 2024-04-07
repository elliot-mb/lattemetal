import java.util.List;
import java.util.Map;

public class ReservationStation implements InstructionVoidVisitor {
    public static final int NO_DEPENDENCY = -1;
    public static int uId = 0;
    public final int id;
    public Instruction op;
    public int qJ, qK;
    public int vJ, vK;
    public boolean rJ, rK, busy;

    public int robEntry;

    private final Map<Integer, List<Integer>> cdb;

    ReservationStation(Map<Integer, List<Integer>> cdb){
        this.cdb = cdb;
        flush();
        this.id = uId;
        uId++;
    }

    public int getId(){
        return id;
    }

    public boolean isBusy(){
        return busy;
    }

    public void flush(){
        this.busy = false;
        this.op = null;
        this.qJ = NO_DEPENDENCY; //rob entry id
        this.qK = NO_DEPENDENCY; //rob entry id
        this.vJ = 0;
        this.vK = 0;
        this.rJ = false;
        this.rK = false;
        this.robEntry = 0;
    }

    public void set(PipelineEntry e, PhysicalRegFile prf, RegisterFile rf, int robEntry){
        op = e.getOp();
        List<Integer> sources = op.visit(new SourceLocVisitor());
        List<Integer> dest = op.visit(new DestLocVisitor());

        Integer regJ = sources.isEmpty() ? null : sources.get(0);
        Integer regK = sources.size() <= 1 ? null : sources.get(1);

        if(regJ != null && prf.isRegValReady(regJ)){
            vJ = rf.getReg(null, regJ);
            rJ = true; //ready up
            qJ = NO_DEPENDENCY;
        }else if(regJ != null){
            qJ = prf.whereDestVal(regJ);
            rJ = false;
        }
        if(regJ == null){
            rJ = true;
        }
        if(regK != null && prf.isRegValReady(regK)){
            vK = rf.getReg(null, regK);
            rK = true; //ready up
            qK = NO_DEPENDENCY;
        }else if(regK != null){
            qK = prf.whereDestVal(regK);
            rJ = false;
        }
        if(regK == null){
            rJ = true;
        }

        busy = true;
        if(!dest.isEmpty()) prf.pointAtRobEntry(dest.get(0), robEntry); //tell the register file to point at the rob entry of the instruction in this rs, IF there is a result
        this.robEntry = robEntry;
    }

    //checks if either dependant RSs have finished!
    public void update(){
        if(qJ == NO_DEPENDENCY) rJ = true;
        if(qK == NO_DEPENDENCY) rK = true;
        if(qJ != NO_DEPENDENCY && cdb.containsKey(qJ)){
            System.out.println(this.id + " READ OFF COMMON DATA BUS! ROB entry " + qJ + " has this result");
            vJ = cdb.get(qJ).get(0); //broadcast this data on the first element of the list
            qJ = NO_DEPENDENCY;
            rJ = true;
        }
        if(qK != NO_DEPENDENCY && cdb.containsKey(qK)){
            System.out.println(this.id + " READ OFF COMMON DATA BUS! reservation station " + qK + " sent this result");
            vK = cdb.get(qK).get(0);
            qK = NO_DEPENDENCY;
            rK = true;
        }
        op.visit(this); //put the updated values inside op
    }

    public boolean isReady(){
        return (qJ  == NO_DEPENDENCY || rJ) && (qK == NO_DEPENDENCY || rK); //if qi is null that means there is no resevation station assigned to ti
    }

    @Override
    public void accept(Op.Add op) {
        op.setRsVal(vJ);
        op.setRtVal(vK);
    }

    @Override
    public void accept(Op.AddI op) {
        op.setRsVal(vJ);
    }

    @Override
    public void accept(Op.Mul op) {
        op.setRsVal(vJ);
        op.setRtVal(vK);
    }

    @Override
    public void accept(Op.MulI op) {
        op.setRsVal(vJ);
    }

    @Override
    public void accept(Op.Cmp op) {
        op.setRsVal(vJ);
        op.setRtVal(vK);
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRsVal(vJ);
    }

    @Override
    public void accept(Op.LdC op) {

    }

    @Override
    public void accept(Op.St op) {
        op.setRdVal(vJ);
    }

    @Override
    public void accept(Op.BrLZ op) {
        op.setRdVal(vJ);
    }

    @Override
    public void accept(Op.JpLZ op) {

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
