public class RegisterFile extends Block{

    RegisterFile(){
        super(RegisterName.values().length, Block.NODATA);
    }

    public void setReg(RegisterName name, int val){
        if(name == RegisterName.zero) throw new RuntimeException("setReg: mutation not possible: 'zero' is a constant register");
        data[name.ordinal()] = val;
    }

    public int getReg(RegisterName name){
        return read(name.ordinal());
    }
}
