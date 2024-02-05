import java.util.Arrays;

public class RegisterFile {

    private final int[] rf = new int[32]; //we should index this with the enum

    RegisterFile(){
        Arrays.fill(rf, 0);
    }

    public int getReg(RegisterName name){
        return rf[name.ordinal()];
    }

    public void setReg(RegisterName name, int val){
        if(name == RegisterName.zero) throw new RuntimeException("setReg: mutation not possible: 'zero' is a constant register");
        rf[name.ordinal()] = val;
    }

}
