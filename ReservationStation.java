import java.util.List;
import java.util.Map;

public class ReservationStation implements InstructionVoidVisitor {
    public static int uId = 0;
    public final int id;
    public Instruction op;
    public ReservationStation qJ, qK;
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
        this.qJ = null;
        this.qK = null;
        this.vJ = 0;
        this.vK = 0;
        this.rJ = false;
        this.rK = false;
        this.robEntry = 0;
    }

    public void set(PipelineEntry e, RegisterFile rf, int robEntry){
        op = e.getOp();
        List<RegisterName> sources = op.visit(new SourceRegVisitor());
        List<RegisterName> dest = op.visit(new DestRegVisitor());

        RegisterName regJ = sources.isEmpty() ? null : sources.get(0);
        RegisterName regK = sources.size() <= 1 ? null : sources.get(1);

        if(regJ != null && rf.isRegValReady(regJ)){
            vJ = rf.getReg(regJ);
            rJ = true; //ready up
        }else if(regJ != null){
            qJ = rf.whereRegVal(regJ);
            rJ = false;
        }
        if(regJ == null){
            rJ = true;
        }
        if(regK != null && rf.isRegValReady(regK)){
            vK = rf.getReg(regK);
            rK = true; //ready up
        }else if(regK != null){
            qK = rf.whereRegVal(regK);
            rJ = false;
        }
        if(regK == null){
            rJ = true;
        }

        busy = true;
        if(!dest.isEmpty()) rf.pointAtResStation(dest.get(0), this);
        this.robEntry = robEntry;
    }

    //checks if either dependant RSs have finished!
    public void update(){
        if(qJ == null || !qJ.isBusy()) rJ = true;
        if(qK == null || !qK.isBusy()) rK = true;
        if(qJ != null && cdb.containsKey(qJ.getId())){
            System.out.println(this.id + " READ OFF COMMON DATA BUS! reservation station " + qJ.getId() + " sent this result");
            vJ = cdb.get(qJ.getId()).get(0); //broadcast this data on the first element of the list

        }
        if(qK != null && cdb.containsKey(qK.getId())){
            System.out.println(this.id + " READ OFF COMMON DATA BUS! reservation station " + qK.getId() + " sent this result");
            vK = cdb.get(qK.getId()).get(0);
        }
        op.visit(this); //put the updated values inside op
    }

    public boolean isReady(){
        return (qJ == null || rJ) && (qK == null || rK); //if qi is null that means there is no resevation station assigned to ti
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
