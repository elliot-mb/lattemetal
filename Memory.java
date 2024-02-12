public class Memory extends Block{

    private static final int SIZE = 64;
    private static final int MAX_ADDR_DIGIT = Integer.toHexString(SIZE).length();

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
        int PER_ROW = 8;

        for(int j = 0; j < SIZE; j++){
            int val = read(j);
            boolean negative = val < 0;
            int absVal = Math.abs(val);
            int digits = Utils.digitsInInt(val); //will only be as much as 10, so the cast is fine
            int padLen = MAX_DIGIT - digits;
            String valPadding = (negative ? "-" : "");
            String padChar = !isDirty(j) ? "\s" : "0";
            for(int i = 0; i < padLen; i++) valPadding += padChar;
            String addrName = Integer.toHexString(addr);
            int addrDigits = addrName.length();
            int addrPadLen = MAX_ADDR_DIGIT - addrDigits;
            String addrPadding = "\s";
            for(int i = 0; i < addrPadLen; i++) addrPadding += "\s";
            result += (addr % PER_ROW == 0 ? "\r\n" : "");
            result += "0x" + Integer.toHexString(addr) + addrPadding + (negative ? "" : "\s") + valPadding + (val == 0 ? "" : absVal) + "\s\s\s";
            addr++;
        }
        return result;
    }
}
