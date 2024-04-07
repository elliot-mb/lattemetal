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

    public String toString(){
        String gaps = "";
        for(int i = 0; i < buffer.getSize() - buffer.getElementsIn(); i++ ){
            gaps += ",__";
        }
        return "[" + gaps + buffer.peekXs().stream().map(ReorderEntry::toString).collect(Collectors.joining()) + "]";
    }

}
