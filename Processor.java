public class Processor {

    private final ProgramCounter pc;
    private final InstructionCache ic;

    Processor(ProgramCounter pc, InstructionCache ic){
        this.pc = pc;
        this.ic = ic;
    }

}
