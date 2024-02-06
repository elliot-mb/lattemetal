public class Memory extends Block{

    private static final int SIZE = 4096;

    Memory(int... data){
        super(SIZE, data);
    }

    public int get(int addr){
        return read(addr);
    }

    public void set(int val, int addr){
        read(addr); //throw if addr is out of bounds
        data[addr] = val;
    }
}
