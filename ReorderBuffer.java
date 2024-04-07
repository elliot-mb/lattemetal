import java.util.List;
import java.util.stream.Collectors;

public class ReorderBuffer {

    public static final int NO_DEST = -1;
    private final CircluarQueue<ReorderEntry> buffer;

    ReorderBuffer(int size){
        buffer = new CircluarQueue<ReorderEntry>(size);
    }

    public void add(ReorderEntry re){
        buffer.push(re);
    }

    public boolean isFull(){
        return buffer.isFull();
    }

    private String appendComma(String s){
        return s + ",";
    }

    public String toString(){
        String gaps = "";
        for(int i = 0; i < buffer.getSize() - buffer.getElementsIn(); i++ ){
            gaps += ",__";
        }
        return "[" + gaps + buffer.peekXs().stream().map(ReorderEntry::toString).map(this::appendComma).collect(Collectors.joining()) + "]";
    }

    public Integer tailId(){
        if(buffer.isEmpty()) return null;
        List<ReorderEntry> re = buffer.peekXs();
        return re.get(re.size() - 1).id;
    }

}
