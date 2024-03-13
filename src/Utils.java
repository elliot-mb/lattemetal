package src;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final char[] WHITESPACE = new char[]{' ', '\n', '\r', '\t'};

    private static final Id preDecoder = new Id();

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

    public static <T> String writeList(ArrayList<T> xs){
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

    public static boolean isLoadStore(Instruction op){
        Opcode code = op.visit(preDecoder);
        return code == Opcode.ld || code == Opcode.ldc || code == Opcode.st;
    }

    public static double toDecimalPlaces(float n, int dp){
        double factor = Math.pow(10, dp);
        return Math.round(n * factor) / factor;
    }
}
