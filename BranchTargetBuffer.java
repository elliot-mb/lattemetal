import java.util.HashMap;
import java.util.Map;

public class BranchTargetBuffer {


    private final Map<Integer, Integer> pcToPrediction;
    private final CircluarQueue<Integer> orderAdded;
    BranchTargetBuffer(int size){
        this.orderAdded = new CircluarQueue<Integer>(size); //models our size and replacement strategy (oldest evicted)
        //if the rob were full of branches this would be full too, and then its also everything we could put through the
        //fetch unit before it gets added to the rob (twice the superscalar width)
        //this.branchQueue = new CircluarQueue<Boolean>(Processor.ROB_ENTRIES + Processor.SUPERSCALAR_WIDTH * 2);
        this.pcToPrediction = new HashMap<Integer, Integer>();
    }

    public boolean hasEntry(int pc){
        return pcToPrediction.containsKey(pc);
    }

    private void addTo(int pc, int target){
        if(!pcToPrediction.containsKey(pc) && orderAdded.isFull()) pcToPrediction.remove(orderAdded.pop()); //remove oldest mapping
        if(!pcToPrediction.containsKey(pc)) orderAdded.push(pc);
        pcToPrediction.put(pc, target);
    }

    //should be interacted with in program order
    public void predictForThisBranch(int pc, boolean taken, int target){
        boolean predictedTaken;
        if(taken){
            //do some prediction
            predictedTaken = true; //this works like a 1-bit predictor!
            //to do a static predictor we can compare pc and target to discern direction
            //to do a dynamic predictor we can store some state for each branch to delay the decisions we make
        }else{
            //do some other prediction
            predictedTaken = false;
        }
        if(predictedTaken){
            //make an entry assigned to this pc with the new prediction, if it is predicted taken (or do nothing if it existed before)
            addTo(pc, target);
        }else{
            //if we find it is not taken after our prediction, remove it from the btb (or do nothing if it did not exist.)
            pcToPrediction.remove(pc);
        }
    }

    public int getPrediction(int pc){
        if(!hasEntry(pc)) throw new RuntimeException("getPrediction: no entry for pc!");
        return pcToPrediction.get(pc);
    }

//    public void registerPrediction(boolean p){
//        branchQueue.push(p);
//    }
//
//    public boolean removePrediction(){
//        return branchQueue.pop();
//    }
}
