import java.util.List;
import java.util.stream.Collectors;

//these will become little queues now
public class PipelineRegister {

    private final int size;
    private CircluarQueue<PipeRegEntry> queue;

    PipelineRegister(int size){
        this.size = size;
        this.queue = new CircluarQueue<PipeRegEntry>(size);
    }

    public void flush(){
        queue.empty();
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
    public void push(PipeRegEntry e){
        if(queue.isFull()) throw new RuntimeException("push: pushing to a full pipereg");
        queue.push(e.copy()); //make sure to copy
    }

    //can return null, indicating a stall
    public PipeRegEntry pull(){
        if(queue.isEmpty()) throw new RuntimeException("pull: pulling from an empty pipereg");
        return queue.pop().copy();
    }

    public boolean canPush(){
        return !queue.isFull();
    }

    public boolean canPull(){
        return !queue.isEmpty();
    }

    public PipeRegEntry peek() {
        List<PipeRegEntry> xs = queue.peekXs();
        return xs.get(xs.size() - 1);
    }

    private String instrToId(Instruction op){
        String pad = canPull() && (op.getId() % 100) < 10 ? "0" : "";
        return "," + (canPull() ? pad + (op.getId() % 100) : "__");
    }

    public String toString(){
        String gaps = "";
        for(int i = 0; i < queue.getSize() - queue.getElementsIn(); i++ ){
            gaps += ",__";
        }
        return "[" + gaps + queue.peekXs().stream().map(PipeRegEntry::getOp).map(this::instrToId).collect(Collectors.joining()) + "]";
    }
}