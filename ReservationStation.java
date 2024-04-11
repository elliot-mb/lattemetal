import java.util.List;
import java.util.Map;

public class ReservationStation implements InstructionVoidVisitor {
    public static final int NO_DEPENDENCY = -1;

    public static int uId = 0;
    public final int id;
    private Instruction op;
    private int qJ, qK;
    private int vJ, vK;
    private boolean rJ, rK, busy;
    private final ReorderBuffer rob;
    public int robEntry;
    private final Map<Integer, List<Integer>> cdb;

    ReservationStation(Map<Integer, List<Integer>> cdb, ReorderBuffer rob){
        this.cdb = cdb;
        flush();
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

        RegisterName regJ = sources.isEmpty() ? null : RegisterName.values()[sources.get(0)];
        RegisterName regK = sources.size() <= 1 ? null : RegisterName.values()[sources.get(1)];

        if(regJ != null && prf.isRegValReady(regJ)){
            vJ = prf.isRegValUnmapped(regJ) ? rf.getReg(regJ) : rob.getValOfEntry(prf.whereRegInRob(regJ));
            rJ = true; //ready up
            qJ = NO_DEPENDENCY;
        }else if(regJ != null){
            qJ = prf.whereRegInRob(regJ);
            rJ = false;
        }
        if(regJ == null){
            rJ = true;
        }
        if(regK != null && prf.isRegValReady(regK)){
            vK =  prf.isRegValUnmapped(regK) ? rf.getReg(regK) : rob.getValOfEntry(prf.whereRegInRob(regK));
            rK = true; //ready up
            qK = NO_DEPENDENCY;
        }else if(regK != null){
            qK = prf.whereRegInRob(regK);
            rJ = false;
        }
        if(regK == null){
            rJ = true;
        }

        busy = true;
        if(!dest.isEmpty()) prf.pointRegAtRobEntry(RegisterName.values()[dest.get(0)], robEntry); //tell the register file to point at the rob entry of the instruction in this rs, IF there is a result
        this.robEntry = robEntry;
    }

    //checks if either dependant RSs have finished!
    public void update(){
        if(qJ == NO_DEPENDENCY) rJ = true;
        if(qK == NO_DEPENDENCY) rK = true;
        if(qJ != NO_DEPENDENCY && cdb.containsKey(qJ)){ //qJ is memory location or register
            System.out.println(id + " READ OFF COMMON DATA BUS! ROB entry" + qJ + " has this result");
            vJ = cdb.get(qJ).get(0); //broadcast this data on the first element of the list
            qJ = NO_DEPENDENCY;
            rJ = true;
        }
        if(qK != NO_DEPENDENCY && cdb.containsKey(qK)){
            System.out.println(id + " READ OFF COMMON DATA BUS! ROB entry " + qK + " has this result");
            vK = cdb.get(qK).get(0);
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

    private String instrToId(Instruction op){
        if(op == null) return "__";
        String pad = op.getId() % 100 < 10 ? "0" : "";
        return pad + (op.getId() % 100);
    }

    @Override
    public String toString(){
        return "" + instrToId(op);
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
