import java.util.ArrayList;
import java.util.List;

public class Utils {

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
        char[] whitespace = new char[]{' ', '\n', '\r', '\t'};

        for(char c : maybeSpace.toCharArray()){
            boolean isWhitespace = false;
            for(char ws : whitespace){
                isWhitespace = c == ws || isWhitespace;
            }
            if(!isWhitespace) return false; //if it was not any of them
        }
        return true;
    }
}
