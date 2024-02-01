public abstract class Instruction {

    protected final RegisterName rd;
    protected final RegisterName rs;
    protected final RegisterName rt;
    protected final int immediate;
    protected final int duration;
    protected int ticker = 0;

    Instruction(int duration, int immediate, RegisterName... regs){
        this.duration = duration;
        this.immediate = immediate;
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
        if(this.immediate == Assembler.IMM_UNSET) throw new RuntimeException("getImmediate: there is no immediate defined for this instruction");
        return this.immediate;
    }

    abstract public Opcode visit(InstructionVisitor v);

    protected String regToString(RegisterName r){
        return r == null ? "_" : r.name();
    }

    protected String immToString(int immediate){
        return immediate == Assembler.IMM_UNSET ? "_" : "" + immediate;
    }

    @Override
    public String toString(){
        Opcode underlying = visit(new Id());
        return underlying.name() + " " + regToString(rd) + " " + regToString(rs) + " " + regToString(rt) + " #" + immToString(immediate);
    }

}
