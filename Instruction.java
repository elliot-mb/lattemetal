public abstract class Instruction extends HasDuration {

    protected final RegisterName rd;
    protected final RegisterName rs;
    protected final RegisterName rt;
    protected final int im;

    protected int rdVal;
    protected int rsVal;
    protected int rtVal;

    Instruction(int duration, int immediate,  RegisterName... regs){
        super(duration);
        this.im = immediate;
        this.rd = regs.length > 0 ? regs[0] : null;
        this.rs = regs.length > 1 ? regs[1] : null;
        this.rt = regs.length > 2 ? regs[2] : null;
    }

    abstract public OpCode visit(InstructionCodeVisitor v);
    abstract public void visit(InstructionVoidVisitor v);

    protected String regToString(RegisterName r){
        return r == null ? "" : " " + r.name();
    }

    protected String immToString(int immediate){
        return immediate == Assembler.IMM_UNSET ? "" : " #" + immediate;
    }

    @Override
    public String toString(){
        OpCode underlying = visit(new OpCoder());
        return underlying.name() + "\t" + regToString(rd) + regToString(rs) + regToString(rt) + immToString(im);
    }

    // term shapes:
    //      three registers
    //      two registers, one immediate
    //      one register, one immediate
    //      one immediate

    // operator argument validation on the four instruction shapes
    protected void checkShape(RegisterName rd, RegisterName rs, RegisterName rt){
        if(rd == null || rs == null || rt == null)
            throw new RuntimeException(visit(new OpCoder()) + ": missing at least one register reference");
    }

    protected void checkShape(RegisterName rd, RegisterName rs, int im){
        if(rd == null || rs == null)
            throw new RuntimeException(visit(new OpCoder()) +": missing at least one register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new OpCoder()) +": missing immediate");
    }

    protected void checkShape(RegisterName rd, int im){
        if(rd == null)
            throw new RuntimeException(visit(new OpCoder()) +": missing the register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new OpCoder()) +": missing immediate");
    }

    protected void checkShape(int im){
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new OpCoder()) +": missing immediate");
    }

    public abstract Instruction copy();

}
