import java.util.HashMap;
import java.util.Map;

public class BranchTargetBuffer {

    private final Map<Integer, Integer> pcToPrediction;
    //private final int size;

    BranchTargetBuffer(){
        //this.size = size; //one entry will get replaced for every entry we add over size
        this.pcToPrediction = new HashMap<Integer, Integer>();
    }

    public boolean hasEntry(int pc){
        return pcToPrediction.containsKey(pc);
    }

    //should be interacted with in program order
    public void predictForThisBranch(int pc, boolean taken, int target){
        boolean predictedTaken;
        if(taken){
            //do some prediction
            predictedTaken = true;
        }else{
            //do some other prediction
            predictedTaken = false;
        }
        if(predictedTaken){
            //make an entry assigned to this pc with the new prediction, if it is predicted taken (or do nothing if it existed before)
            pcToPrediction.put(pc, target);
        }else{
            //if we find it is not taken after our prediction, remove it from the btb (or do nothing if it did not exist.)
            pcToPrediction.remove(pc);
        }
    }

}
