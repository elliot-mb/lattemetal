import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//circular queue
public class CircluarQueue<T> {
    private List<T> xs;
    private int head;
    private int tail;
    private final int size;
    private int elementsIn;

    CircluarQueue(int size){
        if(size < 1) throw new RuntimeException("CQueue: size must be one or more");
        this.size = size;
        empty();
    }

    public void empty(){
        xs = new ArrayList<T>();
        for(int i = 0; i < size; i++){
            xs.add(null);
        }
        head = 0;
        tail = 0;
        elementsIn = 0;
    }

    public int getSize(){
        return size;
    }

    public int getElementsIn(){
        return elementsIn;
    }

    public void setElement(int i, T newE){
        if(i >= elementsIn) throw new RuntimeException("setElement: setting element outside of queue");
        xs.set(itemIFromHead(i), newE);
    }

    public void push(T x){
        if(isFull()) {
            //System.out.println("CQueue.push: warning: queue is full");
            return;
        }
        xs.set(head, x);
        head = (head + 1) % size;
        elementsIn++;
    }

    public T pop(){
        if(isEmpty()){
            System.out.println("CQueue.pop: warning: queue is empty");
            return null;
        }
        elementsIn--;
        T ret = xs.get(tail);
        tail = (tail + 1) % size;
        return ret;
    }

    public T shift(){
        if(isEmpty()){
            System.out.println("CQueue.pop: warning: queue is empty");
            return null;
        }
        elementsIn--;
        head = ((head - 1) + size) % size;
        T ret = xs.get(head);
        return ret;
    }

    public boolean isEmpty(){
        return elementsIn == 0;
    }

    public boolean isFull(){
        return elementsIn >= size;
    }

    private int itemIFromHead(int i){
        return (head - i - 1 + size) % size;
    }

    //returns a list line [HEAD, x_1, x_2, ..., TAIL] where TAIL things come off of, HEAD things are added on, and
    //both head and tail are populated elements in this list
    public List<T> peekXs(){
        List<T> ret = new ArrayList<T>();

        for(int i = 0; i < elementsIn; i++){
            ret.add(xs.get(itemIFromHead(i)));
        }

        return ret;
    }

    public T peek(){
        return xs.get(itemIFromHead(elementsIn - 1));
    }

    public T peekHead(){
        return xs.get(itemIFromHead(0));
    }

    private String appendComma(String uncommad){
        return uncommad + ",";
    }

    @Override
    public String toString(){
        String gaps = "";
        for(int i = 0; i < getSize() - getElementsIn(); i++ ){
            gaps += ",__";
        }
        return "[" + gaps + "," + peekXs().stream().map(T::toString).map(this::appendComma).collect(Collectors.joining()) + "]";
    }
}
