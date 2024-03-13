import java.util.Arrays;

public abstract class Block {

    //a generic block of memory
    //inhereted by register file, instruction cache, memory etc

    private final int[] data;
    private final boolean[] dirty; //whether data has been mutated
    protected final int size;
    static protected final int[] NODATA = {};

    private final int maxDigits;
    private final int printWidth;

    Block(int size){
        this.size = size;
        this.data = new int[size];
        this.dirty = new boolean[size];
        this.maxDigits = Utils.digitsInInt(size);
        this.printWidth = 1;
    }


    //numbers are passed in variadically for now for easy parameter setting
    Block(int size, int printWidth){
        this.size = size;
        this.data = new int[size];
        this.dirty = new boolean[size];
        this.maxDigits = Utils.digitsInInt(size);
        this.printWidth = printWidth;
    }

    protected void load(int[] in) throws RuntimeException{
        if(in.length > 0){
            if(in.length > size) throw new RuntimeException("load: data block passed in was too large");
            int addr = 0;
            for(int e: in){
                this.data[addr] = e;
                this.dirty[addr] = true;
                addr++;
            }
        }
    }

    protected boolean isDirty(int addr){
        if(addr < 0 || addr >= this.size) throw new RuntimeException("dirty: addr was out of range");
        return dirty[addr];
    }

    protected int read(int addr) throws RuntimeException{
        if(addr < 0 || addr >= this.size) {
            System.out.println(this);
            throw new RuntimeException("read: address [" + addr + "] is larger than the size of the block, or smaller than zero");
        }
        return data[addr];
    }

    protected void setDelegate(int addr, int val) throws RuntimeException{
        if(addr < 0 || addr >= this.size) throw new RuntimeException("setDelegate: addr " + addr + " was out of range");
        data[addr] = val;
        dirty[addr] = true;
    }

    public String toListString(){
        return Arrays.toString(data);
    }

    public String toString(){
        String result = "";
        int addr = 0;
        int MAX_DIGIT = 10;

        for(int j = 0; j < this.size; j++){
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
            int addrPadLen = maxDigits - addrDigits;
            String addrPadding = "";
            for(int i = 0; i < addrPadLen; i++) addrPadding += "0";
            result += (addr % printWidth == 0 ? "\r\n" : "");
            result += "[" + addrPadding + (addr == 0 ? "" : addr) + "]\s";
            result += (negative ? "-" : "\s");
            result += (isDirty(j) ? absVal : "\s") + valPadding;
            addr++;
        }
        return result;
    }

}
