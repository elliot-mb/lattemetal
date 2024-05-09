import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterFile extends Block{

    public static final int NUM_REGS = RegisterName.values().length;

    RegisterFile(Map<Integer, List<Integer>> cdb){
        super(NUM_REGS);
    }

    public void setReg(RegisterName name, int val){
        if(name == RegisterName.zero) throw new RuntimeException("setReg: mutation not possible: 'zero' is a constant register");
        setDelegate(name.ordinal(), val);
    }

    public int getReg(RegisterName name){
        return read(name.ordinal());
    }

    public int getReg(int nameNum){
        return read(nameNum);
    }

    @Override
    public String toString() {
        String builder = "";
        for(RegisterName reg : RegisterName.values()){
            if(isDirty(reg.ordinal())) builder += "\t" + reg.name() + ":" + getReg(reg);
        }
        return builder;
    }
}
