public class InstructionCache extends Block{

    // one integer stores
    // iiiii    rrrrr   rrrrr   rrrrr   iiiiiiiiiiiii
    // opcode   destin  source  source  immediate

    InstructionCache(int size, int[] data){ // instruction cache is not writable
        super(size, data);
    }

    protected int readDelegate(int addr){
        return data[addr];
    }
}
