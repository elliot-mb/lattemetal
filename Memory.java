public class Memory extends Block{

    private static final int SIZE = 64;
    private static final int MAX_ADDR_DIGIT = Utils.digitsInInt(SIZE);

    Memory(){
        super(SIZE);
    }

    public int get(int addr){
        return read(addr);
    }

    public void set(int val, int addr){
        read(addr); //throw if addr is out of bounds
        setDelegate(addr, val);
    }

    public String toString(){
        String result = "";
        int addr = 0;
        int MAX_DIGIT = 10;
        int PER_ROW = 3;

        for(int j = 0; j < SIZE; j++){
            int val = read(j);
            boolean negative = val < 0;
            boolean zero = val == 0;
            int absVal = Math.abs(val);
            int digits = Utils.digitsInInt(val); //will only be as much as 10, so the cast is fine
            int padLen = MAX_DIGIT - digits;
            String valPadding = "\s\s";
            //String padChar = !isDirty(j) ? "\s" : "0";
            String padChar = "\s";
            for(int i = (zero ? 1 : 0); i < padLen; i++) valPadding += padChar;
            int addrDigits = Utils.digitsInInt(addr);
            int addrPadLen = MAX_ADDR_DIGIT - addrDigits;
            String addrPadding = "";
            for(int i = 0; i < addrPadLen; i++) addrPadding += "0";
            result += (addr % PER_ROW == 0 ? "\r\n" : "");
            result += "[" + addrPadding + (addr == 0 ? "" : addr) + "]\s";
            result += (negative ? "-" : "\s");
            result += (isDirty(j) ? absVal : "\s") + valPadding;
            addr++;
        }
        return result;
    }
}
