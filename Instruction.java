
public abstract class Instruction extends Durate {

    private static int gId = 0;
    private int id;
    protected final RegisterName rd;
    protected final RegisterName rs;
    protected final RegisterName rt;
    protected final Integer im;

    protected int rdVal;
    protected int rsVal;
    protected int rtVal;

    protected Integer result = null;

    Instruction(int duration, Integer immediate,  RegisterName... regs){
        super(duration);
        this.im = immediate;
        this.rd = regs.length > 0 ? regs[0] : null;
        this.rs = regs.length > 1 ? regs[1] : null;
        this.rt = regs.length > 2 ? regs[2] : null;
        this.id = gId;
        gId++;
    }

    public RegisterName getRd() throws RuntimeException{
        return this.rd;
    }
    public RegisterName getRs() throws RuntimeException{
        return this.rs;
    }
    public RegisterName getRt() throws RuntimeException{
        return this.rt;
    }
    public int getIm() throws RuntimeException{
        if(this.im == null) throw new RuntimeException("getImmediate: there is no immediate defined for this instruction");
        return this.im;
    }

    //this is abstract since even though most instructions will write their results to
    //rd, when they have no rd they can write them to whatever auxiliary storage variables
    //they want
    public void setResult(Integer result){
        this.result = result;
    }
    public int getResult(){
        if(result == null) throw new RuntimeException("getResult: result is null and has not been set");
        return result;
    }

    abstract public Opcode visit(InstructionCodeVisitor v);
    abstract public void visit(InstructionVoidVisitor v);

    protected String regToString(RegisterName r){
        return r == null ? "" : " " + r.name();
    }

    protected String immToString(Integer immediate){
        return immediate == null ? "" : " #" + immediate;
    }

    @Override
    public String toString(){
        Opcode underlying = visit(new Id());
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
            throw new RuntimeException(visit(new Id()) + ": missing at least one register reference");
    }

    protected void checkShape(RegisterName rd, RegisterName rs, Integer im){
        if(rd == null || rs == null)
            throw new RuntimeException(visit(new Id()) +": missing at least one register reference");
        if(this.im == null)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

    protected void checkShape(RegisterName rd, Integer im){
        if(rd == null)
            throw new RuntimeException(visit(new Id()) +": missing the register reference");
        if(this.im == null)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

    protected void checkShape(Integer im){
        if(this.im == null)
            throw new RuntimeException(visit(new Id()) +": missing immediate");
    }

    public abstract Instruction copy();

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
