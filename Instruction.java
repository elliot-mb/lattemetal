public interface Instruction {

//    protected int duration;
//    private final Operand rd; //always not null
//    private final Operand rs;
//    private final Operand rt;
//    protected boolean[] defined;
//
//
//    // all instructions can be seen as
//    Instruction(Operand rd, Operand rs, Operand rt){
//        this.rd = rd;
//        this.rs = rs;
//        this.rt = rt;
//    }
//
//    protected boolean definedOperands(){
//        return this.rd.isUsed() == defined[0] &&
//                this.rs.isUsed() == defined[1] &&
//                this.rt.isUsed() == defined[2];
//    }

    int duration = 0;
    int ticker = 0;
    RegisterName rd = null;
    RegisterName rs = null;
    RegisterName rt = null;
    int immediate = 0;

}

