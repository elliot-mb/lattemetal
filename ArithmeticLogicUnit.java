import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ArithmeticLogicUnit extends Unit {

    private final Forwarder fwd; //Probably obsolete; remove once resi stations are done !
    private final List<ReservationStation> rs;

    private final RegisterFile rf;

    ArithmeticLogicUnit(Scoreboard sb, List<ReservationStation> rs, RegisterFile rf, PipelineRegister[] ins, PipelineRegister[] outs){
        super(ins, outs);
        this.currentOp = null;
        this.fwd = new Forwarder();
        this.rs = rs;
        this.rf = rf;
    }

    @Override
    protected void readOffPipeline(){
        for(ReservationStation r : rs){
            if(super.canPullOffActiveIn() && !r.isBusy() && ins[getActiveIn()].canPull()){
                super.readOffPipeline();
                RegisterName regS = currentOp.getRs();
                RegisterName regT = currentOp.getRt();
                if(regS != null && rf.isRegValReady(regS)){
                    r.vJ = rf.getReg(regS);
                }else if(regS != null){
                    r.qJ = rf.whereRegVal(regS);
                }
                if(regT != null && rf.isRegValReady(regT)){
                    r.vK = rf.getReg(regT);
                }else if(regT != null){
                    r.qK = rf.whereRegVal(regT);
                }
                r.busy = true;
                r.op = currentOp;
            }
        }
    }

    @Override
    protected void procInstruction() {
        currentOp.decr();
//        if(!isUnfinished()){ //this is the final procInstruction call
//            currentOp = fwd.forward(currentOp);
//        }
    }

    @Override
    protected boolean isUnfinished() {
        return !isDone() && !currentOp.isDone(); //if we arent done with the inner up, and its not blank
    }

    @Override
    public void flush(){
        super.flush();
        for(ReservationStation r : rs){
            r.flush();
        }
        fwd.flush();
    }

    @Override
    public void accept(Op.Add op) {
        //modify register value = op.getRsVal() + op.getRtVal(); // i guess we can just write into the instruction
        //and then create a writeback stage
        op.setResult(op.getRsVal() + op.getRtVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.AddI op) {
        // modify register value = op.getRsVal() + op.getImVal();
        op.setResult(op.getRsVal() + op.getImVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.Mul op) {
        // modify register value = op.getRsVal() * op.getRtVal();
        op.setResult(op.getRsVal() * op.getRtVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.MulI op) {
        // modify register value = op.getRsVal() * op.getImVal();
        op.setResult(op.getRsVal() * op.getImVal());
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
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.Ld op) {
//        op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
//        fwd.setSlotReg(null); //doesnt correspond to a register because the result comes from the LSU
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.LdC op) {
//        op.setResult(op.getImVal());
//        fwd.setSlotReg(op.getRd());
//        fwd.setSlot(currentOp.getResult());
    }

    @Override
    public void accept(Op.St op) {
        //throw new RuntimeException("ArithmeticLogicUnit.accept: cannot accept a store instruction!");
        //  store register value at offset address
        //op.setResult(op.getRsVal() + op.getImVal()); //calculate offset
        //fwd.setSlotReg(null); //doesnt correspond to a register
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
