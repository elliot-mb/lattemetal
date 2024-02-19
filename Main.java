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
     *      * ldc  rd #n        --load from a constant address
     *      *
     *      * st   rd (rs) #n   --store whats in register rd into address rs offset by n
     *      *
     *      * brlz rd #n | label_name      --branch; if rd is less than or equal to zero move to absolute instruction # n
     *      * jplz rd #n | label_name     --jump; if rd is less than or equal to zero, relative branch by immediate operand
     *      *
     *      *
     *      * br   #n | label_name     --branch; sets the pc to absolute address (immediate/const)
     *      * jp   #n | label_name     --jump; jumps n instructions down (relative branch with immediate)*
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
        String programPath = "assembly/mat2mul.latte";
        int printWidth = 1;
        if(args.length >= 1){
            System.out.println(args[0]);
            programPath = args[0];
        }
        if(args.length >= 2){
            printWidth = Integer.parseInt(args[1]);
        }
        Assembler assembler = new Assembler(programPath);
        boolean didRead = assembler.readFile();
        if(!didRead){
            throw new RuntimeException("main: program assembler failed to read program");
        }
        InstructionCache ic = new InstructionCache(assembler.assemble());
        Processor p = new Processor(ic, new Memory(
                printWidth,
                new int[]{
                        40, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                        8, 9, -1, 2, -4, 5, 1, 0, 1, 0,
                        8, 11, -1, 6, -4, 5, 1, 4, 1, 0,
                        8, 12, -8, 2, 20, 5, 1, 10, 1, 0,
                        8, 15, -1, 3, -4, 5, 1, 3, 1, 0,
                }
            )); //memory can be set if you like
        p.run();

    }

}
