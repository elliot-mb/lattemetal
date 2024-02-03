public abstract class Instruction extends HasDuration {

    protected final RegisterName rd;
    protected final RegisterName rs;
    protected final RegisterName rt;
    protected final int im;

    protected int rdVal;
    protected int rsVal;
    protected int rtVal;
    protected int imVal;

    Instruction(int duration, int immediate,  RegisterName... regs){
        super(duration);
        this.im = immediate;
        this.rd = regs.length > 0 ? regs[0] : null;
        this.rs = regs.length > 1 ? regs[1] : null;
        this.rt = regs.length > 2 ? regs[2] : null;
    }

    public RegisterName getRd() throws RuntimeException{
        if(this.rd == null) throw new RuntimeException("getRd: there is no first register defined for this instruction");
        return this.rd;
    }
    public RegisterName getRs() throws RuntimeException{
        if(this.rs == null) throw new RuntimeException("getRs: there is no second register defined for this instruction");
        return this.rs;
    }
    public RegisterName getRt() throws RuntimeException{
        if(this.rt == null) throw new RuntimeException("getRt: there is no third register defined for this instruction");
        return this.rt;
    }
    public int getImmediate() throws RuntimeException{
        if(this.im == Assembler.IMM_UNSET) throw new RuntimeException("getImmediate: there is no immediate defined for this instruction");
        return this.im;
    }

    abstract public Opcode visit(InstructionCodeVisitor v);
    abstract public void visit(InstructionVoidVisitor v);

    protected String regToString(RegisterName r){
        return r == null ? "_" : r.name();
    }

    protected String immToString(int immediate){
        return immediate == Assembler.IMM_UNSET ? "_" : "" + immediate;
    }

    @Override
    public String toString(){
        Opcode underlying = visit(new Id());
        return underlying.name() + " " + regToString(rd) + " " + regToString(rs) + " " + regToString(rt) + " #" + immToString(im);
    }

    // term shapes:
    //      three registers
    //      two registers, one immediate
    //      one register, one immediate
    //      one immediate

    // operator argument validation on the four instruction shapes
    protected void checkShape(RegisterName rd, RegisterName rs, RegisterName rt){
        if(rd == null || rs == null || rt == null)
            throw new RuntimeException(visit(new Id()) + ": missing at least one register reference");
    }

    protected void checkShape(RegisterName rd, RegisterName rs, int im){
        if(rd == null || rs == null)
            throw new RuntimeException(visit(new Id()) +": missing at least one register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

    protected void checkShape(RegisterName rd, int im){
        if(rd == null)
            throw new RuntimeException(visit(new Id()) +": missing the register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

    protected void checkShape(int im){
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

}