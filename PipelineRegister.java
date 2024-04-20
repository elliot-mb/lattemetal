import java.util.List;
import java.util.Map;

//these will become little queues now
public class PipelineRegister implements PipeLike {

    private final int size;
    private CircluarQueue<PipelineEntry> queue;

    PipelineRegister(int size){
        this.size = size;
        this.queue = new CircluarQueue<PipelineEntry>(size);
    }

    public void flush(int fromRobEntry){
        if(fromRobEntry == Processor.FLUSH_ALL){
            queue.empty();
        }else {
            CircluarQueue<PipelineEntry> newQ = new CircluarQueue<PipelineEntry>(size);
            while (!queue.isEmpty() && (queue.peek().hasEntry() && queue.peek().getEntry() < fromRobEntry)) { //as soon as we meet or exceed fromRobEntry we stop transferring *(after the flush in program order)
                newQ.push(queue.pop()); //transfer those who're
            }
            queue = newQ; //queue with all values after the higher value in program order removed, and also those that are null (perhaps this is wrong)??
        }
    }

//    public void setPcVal(int count){
//        pcVal = count;
//    }

//    public Integer getPcVal(){
//        if(pcVal == null) throw new RuntimeException("getPc: pc is null and was not set");
//        return pcVal;
//    }

//    public void setFlag(boolean val){
//        flag = val;
//    }

//    public boolean isFlag(){
//        return flag;
//    }

    //push can stall (just if inFlight is not null)
    public void push(PipelineEntry e){
        if(queue.isFull()) throw new RuntimeException("push: pushing to a full pipereg");
        queue.push(e.copy()); //make sure to copy
    }

    //can return null, indicating a stall
    public PipelineEntry pull(){
        if(queue.isEmpty()) throw new RuntimeException("pull: pulling from an empty pipereg");
        return queue.pop().copy();
    }

    public boolean canPush(){
        return !queue.isFull();
    }

    public boolean canPull(){
        return !queue.isEmpty();
    }

    public PipelineEntry peek() {
        List<PipelineEntry> xs = queue.peekXs();
        return xs.get(xs.size() - 1);
    }

    public String toString(){
        return queue.toString();
    }
}