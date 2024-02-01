import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Assembler {
    // this class contains the basic assembler that turns our raw instructions into machine readable objects

    private final String fileName;
    private final ArrayList<String> rawLines;
    private static final Boolean FAIL = false;
    private static final Boolean PASS = true;
    private static final int EOF = -1;
    private static final String DOT = ".";
    private static final char IMMEDIATE = '#';
    private static final int IMM_UNSET = Integer.MIN_VALUE;
    private static final String EXT = "latte";

    /**
     *
     * @param fileName must have the '.latte' file extension, please note set files according to where you run the
     *                 compile&run script
     */
    public Assembler(String fileName) throws RuntimeException{
        if(!extensionCheck(fileName)) throw new RuntimeException("Assembler: fileName has the wrong extension, requires '." + EXT + "' file");
        this.fileName = fileName;
        this.rawLines = new ArrayList<String>();
    }

    /**
     * @return whether it passed the check or not
     */
    private static boolean extensionCheck(String fileName){
        String[] pathSplit = fileName.split("/"); //always at least 1 thing in here
        String[] dotSplit = Utils.last(pathSplit).split("\\.");
        if(dotSplit.length <= 1 ||
           !Utils.last(dotSplit).equals(Assembler.EXT)){
            return FAIL;
        }
        return PASS;
    }

    /**
     * read the assembly program
     * @return pass (true) or fail (false) at finding our file
     */
    public boolean readFile(){
        try(FileReader reader = new FileReader(fileName, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader)){
            String ln = br.readLine();
            while(ln != null){
                String noComment = ln.split("--")[0];
                String trimmed   = noComment.trim();
                if(trimmed.length() != 0)
                    rawLines.add(trimmed); //should remove comments
                ln = br.readLine();
            }
        }catch(FileNotFoundException err){
            System.out.println(err.getMessage());
            return FAIL; //failure
        }catch(IOException err){
            System.out.println(err.getMessage());
            return FAIL;
        }

        return PASS;
    }

    public int getLineCount(){
        return rawLines.size();
    }

    /**
     * instruction format in IC
     * one integer stores
     // iiiii    rrrrr   rrrrr   rrrrr   iiiiiiiiiiiii
     // opcode   destin  source  source  immediate
     * @return an integer array that is the same length as the number of rawlines
     */
    public int[] assemble() throws RuntimeException{
        // line by line
            // split up line on spaces
            // select opcode, rd, rs, rt, immediate expecting them in that order
            // so the first register to appear is rd in the spec, the next is rs, finally rt, and any immediate fills the immediate slot
            // pack into bits

        for(String ln : this.rawLines){
            String[] tokens = ln.split("\s");
            String op = tokens[0];
            if(!Lookup.op.containsKey(op)) throw new RuntimeException("assemble: there is no such opcode '" + op + "'");
            Opcode code = Lookup.op.get(op);
            RegisterName[] regs = new RegisterName[3]; //between zero to three registers can be specified
            int regI = 0;
            int immediate = IMM_UNSET; //has it been set
            for(int i = 1; i < tokens.length; i++){
                String regOrImmediate = tokens[i];
                if(Lookup.reg.containsKey(regOrImmediate)){ //it must be a register
                    regs[regI] = Lookup.reg.get(regOrImmediate);
                    regI++;
                }
                if(regOrImmediate.charAt(0) == IMMEDIATE){
                    if(immediate != IMM_UNSET) throw new RuntimeException("assemble: more than one immediate isn't allowed");
                    String imm = regOrImmediate.substring(1); //throw away the hash
                    int immNum;
                    try{ immNum = Integer.parseInt(imm); }
                    catch(NumberFormatException err){
                        throw new RuntimeException("assembl: immediate was not recognised as a number");
                    }
                    immediate = immNum;
                }
            }

            System.out.println("op:" + code.toString() + " regs:" + Arrays.toString(regs) + " #" + immediate);
        }

        return new int[]{};
    }

    public ArrayList<String> getRawLines(){
        return rawLines; //this cant be modified because strings are immutable
    }

}
