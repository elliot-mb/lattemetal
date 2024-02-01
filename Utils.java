import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final char[] WHITESPACE = new char[]{' ', '\n', '\r', '\t'};

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


}
