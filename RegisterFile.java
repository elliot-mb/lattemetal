public class RegisterFile extends Block{

    private static final int NUM_REGS = RegisterName.values().length;

    RegisterFile(){
        super(NUM_REGS, Block.NODATA);
    }

    public void setReg(RegisterName name, int val){
        if(name == RegisterName.zero) throw new RuntimeException("setReg: mutation not possible: 'zero' is a constant register");
        data[name.ordinal()] = val;
    }

    public int getReg(RegisterName name){
        return read(name.ordinal());
    }

    @Override
    public String toString() {
        String builder = "";
        for(RegisterName reg : RegisterName.values()){
            builder += "\r\n " + reg.name() + "\t" + getReg(reg);
        }
        return builder;
    }
}
