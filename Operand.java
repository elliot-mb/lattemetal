import java.util.Optional;

public class Operand {

    private final int value;
    private final boolean unused;

    Operand(Integer... value){
        this.unused = value.length == 0;
        this.value = value.length > 0 ? value[0] : 0;
    }

    public int getValue(){
        return value;
    }

    public boolean isUsed(){
        return !this.unused;
    }

}
