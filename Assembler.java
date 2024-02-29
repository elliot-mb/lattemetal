import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Assembler {
    // this class contains the basic assembler that turns our raw instructions into machine readable objects

    private final String fileName;
    private final ArrayList<String> rawLines;
    private static final Boolean FAIL = false;
    private static final Boolean PASS = true;
    private static final int EOF = -1;
    private static final String DOT = ".";
    private static final String LABEL = ":";
    private static final char IMMEDIATE = '#';
    public static final Integer IMM_UNSET = null; //used globally
    private static final String EXT = "latte";
    private static final int ARG_REGS = 3; //rd, rs and rt

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
                String[] leftOfComment =  ln.split("--");
                if(leftOfComment.length > 0){
                    String noComment = ln.split("--")[0];
                    String trimmed   = noComment.trim();
                    rawLines.add(trimmed);
                }
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

    private Instruction makeInstr(Opcode code, RegisterName[] regs, Integer immediate){
        RegisterName rd = regs[0];
        RegisterName rs = regs[1];
        RegisterName rt = regs[2];
        switch(code){
            case add: return Utils.opFactory.new Add(rd, rs, rt);
            case addi: return Utils.opFactory.new AddI(rd, rs, immediate);
            case mul: return Utils.opFactory.new Mul(rd, rs, rt);
            case muli: return Utils.opFactory.new MulI(rd, rs, immediate);
            case cmp: return Utils.opFactory.new Cmp(rd, rs, rt);
            case ld: return Utils.opFactory.new Ld(rd, rs, immediate);
            case ldc: return Utils.opFactory.new LdC(rd, immediate);
            case st: return Utils.opFactory.new St(rd, rs, immediate);
            case brlz: return Utils.opFactory.new BrLZ(rd, immediate);
            case jplz: return Utils.opFactory.new JpLZ(rd, immediate);
            case br: return Utils.opFactory.new Br(immediate);
            default: return Utils.opFactory.new Jp(immediate); //jp
        }

    }

    private String errorPrefix(int lnNo){
        return fileName + " @ ln " + lnNo + ": assemble: ";
    }

    private ArrayList<String> getTkns(String ln){
        String[] tokensEmpties = ln.split("\s");
        ArrayList<String> tokens = new ArrayList<String>(){};
        for(String tkn : tokensEmpties){
            if(!tkn.equals("")) tokens.add(tkn);
        }
        return tokens;
    }

    private String lineFromTokens(ArrayList<String> tkns){
        String newLine = "";
        for(String tkn : tkns) newLine += "\s" + tkn;
        return newLine;
    }

    private AbstractMap<String, Integer> labelToLnNums(){
        int lnNum = 0;
        int fileLnNum = 0;
        AbstractMap<String, Integer> labelToLnNum = new HashMap<String, Integer>();
        for(String ln : this.rawLines){
            ArrayList<String> tkns = getTkns(ln);
            if(tkns.size() > 0){
                //if there is a label at the start
                String maybeLabelReg = tkns.get(0);
                int len = maybeLabelReg.length();
                if(String.valueOf(maybeLabelReg.charAt(len - 1)).equals(LABEL)){
                    String label = maybeLabelReg.substring(0, len - 1);
                    if(Lookup.reg.containsKey(label) || Lookup.op.containsKey(label)) throw new RuntimeException(errorPrefix(lnNum + 1) + "replaceLabels: illegal label name '" + label + "'");
                    labelToLnNum.put(label, lnNum);
                    tkns.remove(0); //remove the label
                }

                this.rawLines.set(fileLnNum, lineFromTokens(tkns));
                lnNum++;
            }
            fileLnNum++;
        }
        return labelToLnNum;
    }

    private void replaceLabels() throws RuntimeException{
        int lnNum = 0;
        int fileLnNum = 0;
        AbstractMap<String, Integer> labelToLnNum = labelToLnNums();
        for(String ln : this.rawLines){
            ArrayList<String> tkns = getTkns(ln);
            if(tkns.size() > 0){
                String maybeLabelReg = tkns.get(0);
                boolean br = Lookup.op.get(maybeLabelReg).equals(Opcode.br) || Lookup.op.get(maybeLabelReg).equals(Opcode.brlz);
                boolean jp = Lookup.op.get(maybeLabelReg).equals(Opcode.jp) || Lookup.op.get(maybeLabelReg).equals(Opcode.jplz);
                //if there is a label not at the start (it is an argument)
                if(Lookup.op.containsKey(maybeLabelReg) && (jp || br)){
                    //now we can look for a label, but there does not /need/ to be one
                    String last = tkns.get(tkns.size() - 1);
                    if (labelToLnNum.containsKey(last)) {
                        int labelLoc = labelToLnNum.get(last);
                        //if(labelLoc < lnNum) labelLoc++; //increment if its going backwards (not entirely sure why it works but it does!)
                        int pcChange = labelLoc - lnNum;
                        tkns.set(tkns.size() - 1, String.valueOf(IMMEDIATE) + (jp ? pcChange : labelLoc));
                    }
                }
                //rebuild line
                this.rawLines.set(fileLnNum, lineFromTokens(tkns));
                lnNum++;
            }
            fileLnNum++;
        }

    }

    public ArrayList<Instruction> assemble() throws RuntimeException{
        // line by line
            // split up line on spaces
            // select opcode, rd, rs, rt, immediate expecting them in that order
            // so the first register to appear is rd in the spec, the next is rs, finally rt, and any immediate fills the immediate slot
            // pack into objects

        replaceLabels(); //mutates rawLines

        ArrayList<Instruction> program = new ArrayList<Instruction>();
        int lnNum = 1;
        for(String ln : this.rawLines){
            if(ln.length() != 0) { //add it just if it isnt blank! we dont remove them earlier because we need to know the actual line for syntax error messages
                ArrayList<String> tokens = getTkns(ln);
                String op = tokens.get(0);
                if (!Lookup.op.containsKey(op))
                    throw new RuntimeException(errorPrefix(lnNum) + "there is no such opcode '" + op + "'");
                Opcode code = Lookup.op.get(op);
                RegisterName[] regs = new RegisterName[ARG_REGS]; //between zero to three registers can be specified
                int regI = 0;
                Integer immediate = IMM_UNSET; //has it been set
                for (int i = 1; i < tokens.size(); i++) {
                    String regOrImmediate = tokens.get(i);
                    if (Lookup.reg.containsKey(regOrImmediate)) { //it must be a register
                        if (regI >= ARG_REGS)
                            throw new RuntimeException(errorPrefix(lnNum) + "more than three registers were provided to this operator");
                        regs[regI] = Lookup.reg.get(regOrImmediate);
                        regI++;
                    }else if (regOrImmediate.charAt(0) == IMMEDIATE) {
                        if (immediate != null)
                            throw new RuntimeException(errorPrefix(lnNum) + "more than one immediate isn't allowed");
                        String imm = regOrImmediate.substring(1); //throw away the hash
                        int immNum;
                        try {
                            immNum = Integer.parseInt(imm);
                        } catch (NumberFormatException err) {
                            throw new RuntimeException(errorPrefix(lnNum) + "immediate was not recognised as an integer");
                        }
                        immediate = immNum;
                    }else{
                        throw new RuntimeException(errorPrefix(lnNum) + "'" + code.name() +"' does not recognise argument " + i); //i is the 1-indexed argument, or just the plain argument number
                    }
                }

                try{
                    Instruction instr = makeInstr(code, regs, immediate);
                    program.add(instr);
                }
                catch(RuntimeException err){
                    throw new RuntimeException(errorPrefix(lnNum) + err.getMessage());
                }
            }
            lnNum++;
        }

        return program;
    }

    public ArrayList<String> getRawLines(){
        return rawLines; //this cant be modified because strings are immutable
    }

}
