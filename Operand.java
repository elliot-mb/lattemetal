public class Operand {

    private int value = 0;
    private boolean isNull = true;

    Operand(int value, boolean isNull){
        this.isNull = isNull;
        this.value = value;
    }

    public int getValue() throws RuntimeException{
        if(isNull) throw new RuntimeException("getValue: operand is null");
        return value;
    }

}
