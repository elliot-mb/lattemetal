class Forwarder implements InstructionVoidVisitor {

    private int slot = 0;
    private RegisterName slotReg = null;

    Forwarder(){
        slot = 0;
        slotReg = null;
    }

    public void setSlot(int slot){
        this.slot = slot;
    }

    public void setSlotReg(RegisterName slotReg){
        this.slotReg = slotReg;
    }

    public Instruction forward(Instruction op){
        if(slotReg != null) {
            System.out.println("forwards for " + op);
            op.visit(this);
        }
        return op;
    }

    private int fwdIfEqual(RegisterName dest, int val){
        if(slotReg.equals(dest)) {
            return this.slot;
        }else{
            return val;
        }
    }

    @Override
    public void accept(Op.Add op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
        op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.AddI op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
        //op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.Mul op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
        op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.MulI op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
//        op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.Cmp op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
        op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.Ld op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
//        op.setRtVal(fwdIfEqual(op.getRt(), op.getRtVal()));
    }

    @Override
    public void accept(Op.LdC op) {
        //none
    }

    @Override
    public void accept(Op.St op) {
        op.setRsVal(fwdIfEqual(op.getRs(), op.getRsVal()));
    }

    @Override
    public void accept(Op.BrLZ op) {
        //none
    }

    @Override
    public void accept(Op.JpLZ op) {
        //none
    }

    @Override
    public void accept(Op.Br op) {
        //none
    }

    @Override
    public void accept(Op.Jp op) {
        //none
    }

    @Override
    public void accept(Op.No op) {
        //none
    }
}
