public abstract class Block {

    //a generic block of memory
    //inhereted by register file, instruction cache, memory etc

    private final int[] data;
    private final boolean[] dirty; //whether data has been mutated
    protected final int size;
    static protected final int[] NODATA = {};

    //numbers are passed in variadically for now for easy parameter setting
    Block(int size){
        this.size = size;
        this.data = new int[size];
        this.dirty = new boolean[size];
    }

    protected void load(int[] in) throws RuntimeException{
        if(in.length > 0){ //just takes the first data argument
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
        if(addr < 0 || addr > this.size) throw new RuntimeException("dirty: addr was out of range");
        return dirty[addr];
    }

    protected int read(int addr) throws RuntimeException{
        if(addr < 0 || addr > this.size) throw new RuntimeException("read: address is larger than the size of the block, or smaller than zero");
        return data[addr];
    }

    protected void setDelegate(int addr, int val) throws RuntimeException{
        if(addr < 0 || addr > this.size) throw new RuntimeException("setDelegate: addr " + addr + " was out of range");
        data[addr] = val;
        dirty[addr] = true;
    }

}
