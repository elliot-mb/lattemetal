import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Instruction extends Durate {

    private String annotation;

    public static final int INDEXED_ADDR_LOC = 0; //where we get the address location for the rs in any indexed load store cbd entry
    public static final int INDEXED_UPDT_LOC = 1; //where we get the updated value for rs in any indexed load store cbd entry
    private final int physicalRegistersNeeded;
    private static int gId = 0;
    private int id;
    protected final RegisterName rd;
    protected final RegisterName rs;
    protected final RegisterName rt;
    protected final Integer im;

    private final List<RegisterName> regsList;

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
        this.physicalRegistersNeeded = regs.length;
        this.regsList = new ArrayList<RegisterName>(physicalRegistersNeeded);
        regsList.addAll(Arrays.asList(regs));
        this.id = gId;
        gId++;
        this.annotation = "";
    }
    public void setAnnotation(){
        annotation = "£";
    }

    public int getRegsNeeded(){
        return physicalRegistersNeeded;
    }

    public RegisterName getIthReg(int i){
        return this.regsList.get(i);
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
    public boolean hasResult(){
        return result != null;
    }
    public int getResult(){
        if(result == null) throw new RuntimeException("getResult: result is null and has not been set");
        return result;
    }

    abstract public Opcode visit(InstructionCodeVisitor v);
    abstract public List<Integer> visit(InstructionLocVisitor v);
    abstract public void visit(InstructionVoidVisitor v);

    protected String regToString(RegisterName r){
        return r == null ? "" : " " + r.name();
    }

    protected String immToString(Integer immediate){
        return immediate == null ? "" : " #" + immediate;
    }

    @Override
    public String toString(){
        Opcode underlying = visit(new ConcreteCodeVisitor());
        return underlying.name() + "\t" + regToString(rd) + regToString(rs) + regToString(rt) + immToString(im) + "\t\t" + annotation;
    }

    // term shapes:
    //      three registers
    //      two registers, one immediate
    //      one register, one immediate
    //      one immediate

    // operator argument validation on the four instruction shapes
    protected void checkShape(RegisterName rd, RegisterName rs, RegisterName rt){
        if(rd == null || rs == null || rt == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) + ": missing at least one register reference");
    }

    protected void checkShape(RegisterName rd, RegisterName rs, Integer im){
        if(rd == null || rs == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) +": missing at least one register reference");
        if(this.im == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) +": missing immediate");
    }

    protected void checkShape(RegisterName rd, Integer im){
        if(rd == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) +": missing the register reference");
        if(this.im == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) +": missing immediate");
    }

    protected void checkShape(Integer im){
        if(this.im == null)
            throw new RuntimeException(visit(new ConcreteCodeVisitor()) +": missing immediate");
    }

    public abstract Instruction copy();

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
