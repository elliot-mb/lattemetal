public class ReorderBuffer {

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

}
