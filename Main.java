import java.util.ArrayList;
import java.util.function.BiFunction;

public class Main {


    /**
     * *      * //EXCLUDED                                    ld rd n(rs)[rt] --scaled addressing (? not totally sure how this works because how is d decided)
     *      *      *                      --since we have regs.rd <- mem[n + regs.rs + (regs.rt * d)]
     */


    /**
     * ISA (register-memory)
     *
     *      * add  rd rs rt         --addition of two registers
     *      * addi rd rs #n        --addition of a register and an immediate
     *      * mul  rd rs rt
     *      * muli rd rs #n
     *      * cmp  rd rs rt         --compare rs and rt, returning -1 less, 0 eq, 1 greater
     *      * ld   rd (rs) #n    --if we have a constant zero register (or load a constant like below)
     *      *                  --we can use this for direct and indirect addressing
     *      * ldc  rd #n        --load a constant
     *      *
     *      * st   rd (rs) #n   --store whats in register rd into address rs offset by n
     *      *
     *      * brlz rd #n      --branch; if rd is less than or equal to zero move to absolute instruction # n
     *      * jplz rd #n      --branch; if rd is less than or equal to zero, relative branch by immediate operand
     *      *
     *      *
     *      * br   #n      --branch; sets the pc to absolute address (immediate/const)
     *      * jp   #n      --branch; jumps n instructions down (relative branch with immediate)*
     *      *                      --{pc} is a mandatory destination argument for branch instructions
     *      */



    /**
     * Decode gets data from registers etc.
     */

    /**
     *
     * @param args
     */
    public static void main(String[] args){
        String programPath = "./assembly/matmul.latte";
        if(args.length >= 1){
            System.out.println(args[0]);
            programPath = args[0];
        }
        Assembler assembler = new Assembler(programPath);
        boolean didRead = assembler.readFile();
        if(!didRead){
            throw new RuntimeException("main: program assembler failed to read program");
        }
        InstructionCache ic = new InstructionCache(assembler.assemble());
        Processor p = new Processor(ic);
        p.run();

    }

}
