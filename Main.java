import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *      * ldi  rd (rs) #n --post-indexed!! loads from rs, saves rs + n to rs
     *      *
     *      * st   rd (rs) #n   --store whats in register rd into address rs offset by n
     *      * sti rd (rs) #n   --post-indexed!! whats in register rd into mem[rs], saves rs + n to rs
     *      *
     *      * brlz rd #n | label_name      --branch; if rd is less than or equal to zero move to absolute instruction # n
     *      * jplz rd #n | label_name     --jump; if rd is less than or equal to zero, relative branch by immediate operand
     *      *
     *      *
     *      * br   #n | label_name     --branch; sets the pc to absolute address (immediate/const)
     *      * jp   #n | label_name     --jump; jumps n instructions down (relative branch with immediate)*
     *      *                      --{pc} is a mandatory destination argument for branch instructions
     *      */

    static final String PROG = "prog";
    static final String TESTING = "testing";
    static final String WIDTH = "width";
    static final String QUIET = "quiet";
    static final String PREDICTOR = "predictor";
    static final String BTB_SIZE = "btb_size";
    static final String SS_WIDTH = "ss_width";
    static final String ALUS = "alus";
    static final String LSUS = "lsus";
    static final String BRUS = "brus";
    static final String ALU_RSS = "alu_rss";
    static final String LSU_RSS = "lsu_rss";
    static final String BRU_RSS = "bru_rss";
    static final String DP_ACC = "dp_acc";
    static final String ROB_SIZE = "rob_size";
    static final String ALIGN_FETCH = "align_fetch";


    /**
     * Decode gets data from registers etc.
     */


    private static int stoi(String str) throws NumberFormatException{
        return Integer.parseInt(str);
    }
    /**
     *
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        String programPath = "./benchmark_assembly/ttttn.latte"; //prog
        int printWidth = 1;             // width
        boolean testing = false;        // testing
        boolean quiet = false;          // quiet
        int btbSize = 32;               // btb_size
        int superscalarWidth = 8;       // ss_width
        int aluCount = 4;               // alus
        int lsuCount = 4;               // lsus
        int bruCount = 2;               // brus
        int aluRsCount = 4;             // alu_rss
        int lsuRsCount = 4;             // lsu_rss
        int bruRsCount = 2;             // bru_rss
        int dpAcc = 4;                  // dp_acc
        int robEntries = 64;            // rob_size
        boolean alignedFetch = false;   // aligned_fetch
        Processor.predictor pred = Processor.predictor.twoBit;

        List<String> recogArgs = Arrays.asList(
                PROG, TESTING, WIDTH, QUIET, PREDICTOR, BTB_SIZE, SS_WIDTH, ALUS, LSUS, BRUS, ALU_RSS, LSU_RSS, BRU_RSS, DP_ACC, ROB_SIZE, ALIGN_FETCH
        );

        Map<String, String> argMap = new HashMap<String, String>();

        for(String arg : args){
            //if(!arg.contains("=")) throw new RuntimeException("main: argument '" + arg + "' is not recognised and does not include an equals sign (must be of the form <property_name>=<value>)");
            String[] kv = arg.split("=");
            String key = kv[0];
            String val = kv.length < 2 ? null : kv[1];
            if(!recogArgs.contains(key)) throw new RuntimeException("main: argument name '" + key + "' does not correspond to an input for the program");
            if(argMap.containsKey(key)) throw new RuntimeException("main: argument '" + key +"' already mapped");
            argMap.put(key, val);
        }

        if(argMap.containsKey(PROG)) programPath = argMap.get(PROG);
        if(argMap.containsKey(TESTING)) testing = true;
        if(argMap.containsKey(WIDTH)) printWidth = stoi(argMap.get(WIDTH));
        if(argMap.containsKey(QUIET)) quiet = true;
        if(argMap.containsKey(PREDICTOR)) {
            String predArg = argMap.get(PREDICTOR);
            boolean set = false;
            if(predArg.equals(Processor.predictor.fixedTaken.name())){
                pred = Processor.predictor.fixedTaken; set = true;
            }
            if(predArg.equals(Processor.predictor.fixedNotTaken.name())){
                pred = Processor.predictor.fixedNotTaken; set = true;
            }
            if(predArg.equals(Processor.predictor.bckTknFwdNTkn.name())){
                pred = Processor.predictor.bckTknFwdNTkn; set = true;
            }
            if(predArg.equals(Processor.predictor.bckNTknFwdTkn.name())){
                pred = Processor.predictor.bckNTknFwdTkn; set = true;
            }
            if(predArg.equals(Processor.predictor.oneBit.name())){
                pred = Processor.predictor.oneBit; set = true;
            }
            if(predArg.equals(Processor.predictor.twoBit.name())){
                pred = Processor.predictor.twoBit; set = true;
            }
            if(!set) throw new RuntimeException("main: " + PREDICTOR + "='" + predArg + "' is not a valid predictor type");
        }
        if(argMap.containsKey(BTB_SIZE)) btbSize = stoi(argMap.get(BTB_SIZE));
        if(argMap.containsKey(SS_WIDTH)) superscalarWidth = stoi(argMap.get(SS_WIDTH));
        if(argMap.containsKey(ALUS)) aluCount = stoi(argMap.get(ALUS));
        if(argMap.containsKey(LSUS)) lsuCount = stoi(argMap.get(LSUS));
        if(argMap.containsKey(BRUS)) bruCount = stoi(argMap.get(BRUS));
        if(argMap.containsKey(ALU_RSS)) aluRsCount = stoi(argMap.get(ALU_RSS));
        if(argMap.containsKey(LSU_RSS)) lsuRsCount = stoi(argMap.get(LSU_RSS));
        if(argMap.containsKey(BRU_RSS)) bruRsCount = stoi(argMap.get(BRU_RSS));
        if(argMap.containsKey(DP_ACC)) dpAcc = stoi(argMap.get(DP_ACC));
        if(argMap.containsKey(ROB_SIZE)) robEntries = stoi(argMap.get(ROB_SIZE));
        if(argMap.containsKey(ALIGN_FETCH)) alignedFetch = true;

//
//        if(args.length >= 1){
//            programPath = args[0];
//        }
//        if(args.length >= 2){
//            printWidth = Integer.parseInt(args[1]);
//        }
//        if(args.length >= 3){
//            testing = args[2].equals("testing");
//            System.out.println("is testing? " + testing);
//            quiet = args[2].equals("quiet");
//            System.out.println("is quiet? " + quiet);
//        }

        // do testing

        if(testing){
            Test.test();
            //System.out.println("tests passed");
            return;
        }

        // run a specific program in debug mode

        System.out.println(programPath);
        Memory exampleMemory = new Memory(
                printWidth,
                new int[]{
                        40, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                        92, 84, 76, 68, 50, 42, 34, 26, 18, 0,
                        -2, -4, -6, -8, -10, -12, -14, -16, -18, -20,
                        12, 24, 36, 48, 51, 62, 74, 86, 98, 120,
                        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                }
        );
        Utils.runKern(programPath, exampleMemory, quiet, null, false,
            pred,
            btbSize,
            superscalarWidth,
            aluCount,
            lsuCount,
            bruCount,
            aluRsCount,
            lsuRsCount,
            bruRsCount,
            dpAcc,
            robEntries,
            alignedFetch
        );

    }

}
