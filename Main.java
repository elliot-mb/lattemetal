import java.io.FileNotFoundException;

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
    public static void main(String[] args) throws FileNotFoundException {
        String programPath = "./assembly/basic_block.latte";
        int printWidth = 1;
        boolean testing = false;
        if(args.length >= 1){
            programPath = args[0];
        }
        if(args.length >= 2){
            printWidth = Integer.parseInt(args[1]);
        }
        if(args.length >= 3){
            testing = args[2].equals("testing");
            System.out.println("is testing? " + testing);
        }

        // do testing

        if(testing){
            Test.test();
            System.out.println("tests passed");
            return;
        }

        // run a specific program in debug mode

        System.out.println(programPath);
        Memory exampleMemory = new Memory(
                printWidth,
                new int[]{
                        40, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                        78, 9, -15, 22, -4, 65, 11, 2, 10, 18,
                        58, 11, -11, 6, -4, 51, 51, 4, 51, 17,
                        48, 12, -8, 24, 20, 25, 31, 10, 19, 25,
                        148, 15, -81, 31, -4, 54, 14, 23, 41, 4,
                }
        );
        Utils.runKern(programPath, exampleMemory, false);

    }

}
