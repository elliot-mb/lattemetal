public class Memory extends Block{

    private static final int SIZE = 16;

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

    public String toString(){
        String result = "";
        int addr = 0;
        for(int val : data){
            result += "\r\n 0x" + Integer.toHexString(addr) + "\t" + val;
            addr++;
        }
        return result;
    }
}
