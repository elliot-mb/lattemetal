import java.util.*;

public class ArithmeticLogicUnit extends Unit {

    private final Map<Integer, List<Integer>> cdb;
    private final Forwarder fwd; //Probably obsolete; remove once resi stations are done !
    private final List<ReservationStation> rss;

    private Instruction opFromQueue;
    private int currentRs;
    private final RegisterFile rf;

    ArithmeticLogicUnit(Map<Integer, List<Integer>> cdb, Scoreboard sb, List<ReservationStation> rs, RegisterFile rf, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.currentOp = null;
        this.opFromQueue = null;
        this.currentRs = 0;
        this.fwd = new Forwarder();
        this.rss = rs;
        this.rf = rf;
        this.cdb = cdb;
    }

    @Override
    protected void readOffPipeline(){
        for(ReservationStation rs : rss){
            if(canPullOffActiveIn() && !rs.isBusy() && ins[selectionPriority()].canPull()){ //can read multiple times off the queue
                PipelineRegister in = ins[selectionPriority()];
                PipeRegEntry e = in.pull();
                pcVal = e.getPcVal();
                flag = e.getFlag();
                opFromQueue = e.getOp();
                rs.op = opFromQueue;
                RegisterName regD = opFromQueue.getRd();
                RegisterName regS = opFromQueue.getRs();
                RegisterName regT = opFromQueue.getRt();
//              store neesd to knwo waht Rd IS SO IM NOT SURE WHAT TO DO
                RegisterName regJ = regS;
                RegisterName regK = regT;

                if(regJ != null && rf.isRegValReady(regJ)){
                    rs.vJ = rf.getReg(regJ);
                    rs.rJ = true; //ready up
                }else if(regJ != null){
                    rs.qJ = rf.whereRegVal(regJ);
                }
                if(regK != null && rf.isRegValReady(regK)){
                    rs.vK = rf.getReg(regK);
                    rs.rK = true; //ready up
                }else if(regK != null){
                    rs.qK = rf.whereRegVal(regK);
                }
                rs.busy = true;                   // stores do not write to registers
                if(opFromQueue.getRd() != null && opFromQueue.visit(new Id()) != Opcode.st) rf.pointAtResStation(opFromQueue.getRd(), rs);
            }
        }
    }

    @Override
    protected void procInstruction() {
        for(ReservationStation rs : rss){
            if(rs.isBusy()) rs.update(); //dont update those with no instruction inside
            //System.out.println("UPDATED AND NOW " + rs.isReady());
            if(currentOp == null && rs.isReady()){
                currentOp = rs.op;
                currentRs = rs.getId(); //should only be reset after we finish processing stuff
            }
        }
        if(currentOp != null) currentOp.decr();

    }

    @Override
    protected boolean isUnfinished() {
        return currentOp == null; //if we arent done with the inner up, and its not blank
    }

    @Override
    public void flush(){
        super.flush();
        for(ReservationStation r : rss){
            r.flush();
        }
        fwd.flush();
    }

    @Override
    protected void writeOnPipeline(){
        super.writeOnPipeline();
        cdb.put(currentRs, Collections.singletonList(currentOp.getResult()));
        rss.get(currentRs).busy = false;
    }

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        op.setResult(op.getRsVal() + op.getRtVal());
        op.setRdVal(op.getResult());
        rf.regValIsReady(currentOp.getRd());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        op.setResult(op.getRsVal() + op.getImVal());
        op.setRdVal(op.getResult());
        rf.regValIsReady(currentOp.getRd());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        op.setResult(op.getRsVal() * op.getRtVal());
        op.setRdVal(op.getResult());
        rf.regValIsReady(currentOp.getRd());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        op.setResult(op.getRsVal() * op.getImVal());
        op.setRdVal(op.getResult());
        rf.regValIsReady(currentOp.getRd());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.Cmp op) {
        // modify rd value
        final int a = op.getRsVal();
        final int b = op.getRtVal();
        int cmpResult;
        if(a < b) cmpResult = -1;
        else if(a == b) cmpResult = 0;
        else cmpResult = 1;
        op.setResult(cmpResult);
        op.setRdVal(op.getResult());
        rf.regValIsReady(currentOp.getRd());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.Ld op) {
//        op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
//        fwd.setSlotReg(null); //doesnt correspond to a register because the result comes from the LSU
//        fwd.setSlot(currentOp.getResult());
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.LdC op) {
//        op.setResult(op.getImVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
        throw new RuntimeException("ArithmeticLogicUnit.accept: load instruction not valid");
    }

    @Override
    public void accept(Op.St op) {
        //throw new RuntimeException("ArithmeticLogicUnit.accept: cannot accept a store instruction!");
        //  store register value at offset address
        //op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
        //fwd.setSlotReg(null); //doesnt correspond to a register
        throw new RuntimeException("ArithmeticLogicUnit.accept: store instruction not valid");

    }

    @Override
    public void accept(Op.BrLZ op) {
        //pc.set(op.getImVal());
        //pc.incr();
        flag = op.getRdVal() <= 0;
        op.setResult(op.getImVal()); //result is just set to imval

//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.JpLZ op) {
        flag = op.getRdVal() <= 0;
        op.setResult(pcVal + op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.Br op) {
        op.setResult(op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    @Override
    public void accept(Op.Jp op) {
        op.setResult(pcVal + op.getImVal());
//        fwd.setSlotReg(null); //doesnt correspond to a register
    }

    protected String showUnit(){
        return "EX";
    }
}
