
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final char[] WHITESPACE = new char[]{' ', '\n', '\r', '\t'};

    private static final ConcreteCodeVisitor preDecoder = new ConcreteCodeVisitor();

    public static final Op opFactory = new Op();

    public static <T> T last(ArrayList<T> xs) throws RuntimeException{
        int size = xs.size();
        if(size == 0) throw new RuntimeException("last: cannot take last element of the empty array");
        return xs.get(size - 1);
    }

    public static <T> T last(T[] xs) throws RuntimeException{
        int size = xs.length;
        if(size == 0) throw new RuntimeException("last: cannot take last element of the empty array");
        return xs[size - 1];
    }

    public static <T> String writeList(List<T> xs){
        List<String> shows = xs.stream().map(Object::toString).toList();
        return shows.stream().reduce("", (acc, x) -> acc + "'" + x.toString() + "'" + "\r\n");
    }

    public static boolean isSpace(String maybeSpace){
        String trimmed = maybeSpace.trim();
        return trimmed.length() == 0;
    }

    public static int digitsInInt(int val){
        return (int) Math.round(Math.floor(Math.log10(Math.abs(val))) + 1);
    }

    public static boolean isBranch(Instruction op){
        Opcode code = op.visit(preDecoder);
        return code == Opcode.br || code == Opcode.brlz || code == Opcode.jp || code == Opcode.jplz;
    }

    public static boolean isConditionalBranch(Instruction op){
        Opcode code = op.visit(preDecoder);
        return code == Opcode.brlz || code == Opcode.jplz;
    }

    public static boolean isLoadStore(Instruction op){
        Opcode code = op.visit(preDecoder);
        return code == Opcode.ld || code == Opcode.ldc || code == Opcode.st || code == Opcode.ldi || code == Opcode.sti;
    }

    public static double toDecimalPlaces(double n, int dp){
        double factor = Math.pow(10, dp);
        return Math.round(n * factor) / factor;
    }

    public static Memory runKern(String filePath, Memory mem, boolean quiet, Integer divergenceLim, boolean quietStats) throws FileNotFoundException {
        System.out.println(filePath);
        PrintStream silencer = new PrintStream("/dev/null");
        Assembler assembler = new Assembler(filePath);
        boolean didRead = assembler.readFile();
        if(!didRead){
            throw new RuntimeException("runKern: program assembler failed to read program '" + filePath + "'");
        }
        InstructionCache ic = new InstructionCache(assembler.assemble());
        Processor p = new Processor(ic, mem); //memory can be set if you like
        return p.run(quiet ? silencer : System.out, divergenceLim, quietStats);
    }

    public static boolean isNoOP(Instruction op){
        return op.visit(new ConcreteCodeVisitor()).equals(Opcode.no);
    }

    public static String twoDigitInstrId(Instruction op){
        if(op == null) return "__";
        if(Utils.isNoOP(op)) return "NO";
        String pad = op.getId() % 100 < 10 ? "0" : "";
        return pad + (op.getId() % 100);
    }

    public static void assertTrue(boolean b){
        if(!b) System.out.println("assertTrue: provided false argument");
    }
}
