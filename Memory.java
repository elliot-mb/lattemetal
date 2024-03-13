public class Memory extends Block{

    private static final int SIZE = 64;

    Memory(){
        super(SIZE, 1);
    }

    Memory(int printWidth, int[] contents){
        super(SIZE, Math.max(printWidth, 1));
        load(contents);
    }

    public int get(int addr){
        return read(addr);
    }

    public void set(int val, int addr){
        read(addr); //throw if addr is out of bounds
        setDelegate(addr, val);
    }

    @Override
    public String toString(){
        return toListString();
    }
}
