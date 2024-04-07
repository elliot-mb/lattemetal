import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterFile extends Block{

    public static final int NUM_REGS = RegisterName.values().length;
    private final List<Integer> resultStatus;

    RegisterFile(Map<Integer, List<Integer>> cdb){
        super(NUM_REGS);
        this.resultStatus = new ArrayList<Integer>(NUM_REGS);
        for(int i = 0; i < NUM_REGS; i++){
            this.resultStatus.add(ReservationStation.NO_DEPENDENCY);
        }
    }

    public void setReg(RegisterName name, int val){
        if(name == RegisterName.zero) throw new RuntimeException("setReg: mutation not possible: 'zero' is a constant register");
        setDelegate(name.ordinal(), val);
    }

    public int getReg(RegisterName name, int... nameNum){
        if(nameNum.length > 1) throw new RuntimeException("getReg: optional argument nameNum cannot be more than one long");
        if(name == null) return read(nameNum[0]);
        return read(name.ordinal());
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
