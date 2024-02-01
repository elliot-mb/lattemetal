public abstract class Block {

    //a generic block of memory
    //inhereted by register file, instruction cache, memory etc

    protected final int[] data;
    protected final int size;

    Block(int size, int[]... datas) throws RuntimeException{
        this.size = size;
        this.data = new int[size];

        if(datas.length > 0){ //just takes the first data argument
            int[] dataOption = Utils.last(datas);
            if(dataOption.length > size) throw new RuntimeException("Block: data block passed in was too large");
            int addr = 0;
            for(int e: dataOption){
                this.data[addr] = e;
            }
        }

    }

    //all methods must have a read method defined, but e.g. the instruction cache can have no write method
    public int read(int addr) throws RuntimeException {
        if(addr > this.size) throw new RuntimeException("read: address is larger than the size of the block");
        return readDelegate(addr);
    }

    protected abstract int readDelegate(int addr); // addresses passed here are always within size if called through read



}
