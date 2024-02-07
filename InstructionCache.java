import java.util.ArrayList;

public class InstructionCache{

//    // one integer stores
//    // iiiii    rrrrr   rrrrr   rrrrr   iiiiiiiiiiiii
//    // opcode   destin  source  source  immediate
//    // this is a possible extension for me to implement later
//
//    InstructionCache(int size, int[] data){ // instruction cache is not writable
//        super(size, data);
//    }
//
//    protected int readDelegate(int addr){
//        return data[addr];
//    }

    private final ArrayList<Instruction> program;

    InstructionCache(ArrayList<Instruction> program){
        this.program = program;
    }

    public Instruction getInstruction(int pcVal){
        return program.get(pcVal);
    }

    //gets number of instructions
    public int numInstrs(){
        return program.size();
    }

    @Override
    public String toString(){
        return Utils.writeList(program);
    }
}
