public abstract class Block {

    //a generic block of memory
    //inhereted by register file, instruction cache, memory etc

    protected final int[] data;
    protected final int size;
    static protected final int[] NODATA = {};

    //numbers are passed in variadically for now for easy parameter setting
    Block(int size, int[] data) throws RuntimeException{
        this.size = size;
        this.data = new int[size];

        if(data.length > 0){ //just takes the first data argument
            if(data.length > size) throw new RuntimeException("Block: data block passed in was too large");
            int addr = 0;
            for(int e: data){
                this.data[addr] = e;
            }
        }

    }

    protected int read(int addr) throws RuntimeException{
        if(addr > this.size || addr < 0) throw new RuntimeException("read: address is larger than the size of the block, or smaller than zero");
        return data[addr];
    }

}
