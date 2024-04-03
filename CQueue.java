import java.util.ArrayList;
import java.util.List;
//circular queue
public class CQueue<T> {
    private List<T> xs;
    private int head;
    private int tail;
    private final int size;
    private int elementsIn;

    CQueue(int size){
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
        tail = size - 1;
        elementsIn = 0;
    }

    public void push(T x){
        if(isFull()) {
            System.out.println("CQueue.push: warning: queue is full");
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
        tail = (tail + 1) % size;
        T ret = xs.get(tail);
        xs.set(tail, null);
        return ret;
    }

    public boolean isEmpty(){
        return elementsIn == 0;
    }

    public boolean isFull(){
        return elementsIn >= size;
    }

    public List<T> peekXs(){
        List<T> ret = new ArrayList<T>();
        for(int i = 0; i < elementsIn; i++){
            ret.add(xs.get((i + tail + 1) % size));
        }

        return ret;
    }
}
