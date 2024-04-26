import java.util.HashMap;
import java.util.Map;

public class BranchTargetBuffer {


    private final Map<Integer, Integer> pcToPredictionTarget;
    private final Map<Integer, twoBitPred> pcToPredState;
    private final CircluarQueue<Integer> orderAdded;

    private enum twoBitPred {
        strongNotTaken, weakNotTaken, weakTaken, strongTaken
    };

    BranchTargetBuffer(int size){
        this.orderAdded = new CircluarQueue<Integer>(size); //models our size and replacement strategy (oldest evicted)
        //if the rob were full of branches this would be full too, and then its also everything we could put through the
        //fetch unit before it gets added to the rob (twice the superscalar width)
        //this.branchQueue = new CircluarQueue<Boolean>(Processor.ROB_ENTRIES + Processor.SUPERSCALAR_WIDTH * 2);
        this.pcToPredictionTarget = new HashMap<Integer, Integer>();
        this.pcToPredState = new HashMap<Integer, twoBitPred>();
    }

    public boolean hasEntry(int pc){
        return pcToPredictionTarget.containsKey(pc);
    }

    private void addTo(int pc, int target){
        if(!pcToPredictionTarget.containsKey(pc) && orderAdded.isFull()) pcToPredictionTarget.remove(orderAdded.pop()); //remove oldest mapping
        if(!pcToPredictionTarget.containsKey(pc)) orderAdded.push(pc);
        pcToPredictionTarget.put(pc, target);

    }

    //should be interacted with in program order
    public void predictForThisBranch(int pc, boolean taken, int target){
        boolean predictedTaken = false; //set it just to satisfy the compiler
        boolean addOrRemove = false;
        if(taken){
            //do some prediction
            if(Processor.PREDICTOR.equals(Processor.predictor.twoBit)){
                //strong taken does not get modified
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakTaken)) pcToPredState.put(pc, twoBitPred.strongTaken); //upgrade
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakNotTaken)) pcToPredState.put(pc, twoBitPred.weakTaken); //upgrade
                if(!pcToPredState.containsKey(pc)) {
                    pcToPredState.put(pc, twoBitPred.weakTaken);
                    addOrRemove = true;
                    predictedTaken = true;
                }
            }else{
                addOrRemove = true;
                predictedTaken = true; //this works like a 1-bit predictor!
            }
            //to do a static predictor we can compare pc and target to discern direction
            //to do a dynamic predictor we can store some state for each branch to delay the decisions we make
        }else{
            //do some other prediction
            if(Processor.PREDICTOR.equals(Processor.predictor.twoBit)){
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.strongTaken)) pcToPredState.put(pc, twoBitPred.weakTaken); //downgrade
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakTaken)) pcToPredState.put(pc, twoBitPred.weakNotTaken); //downgrade
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakNotTaken)) {
                    pcToPredState.remove(pc); //annihilate this predictor
                    addOrRemove = true;
                }
            }else{
                addOrRemove = true;
            }
        }
        if(addOrRemove) { //the two-bit predictor doesnt always add or remove btb entries
            if (predictedTaken) {
                //make an entry assigned to this pc with the new prediction, if it is predicted taken (or do nothing if it existed before)
                addTo(pc, target);
            } else {
                //if we find it is not taken after our prediction, remove it from the btb (or do nothing if it did not exist.)
                pcToPredictionTarget.remove(pc);
            }
        }
    }

    public int getPrediction(int pc){
        if(!hasEntry(pc)) throw new RuntimeException("getPrediction: no entry for pc!");
        return pcToPredictionTarget.get(pc);
    }

//    public void registerPrediction(boolean p){
//        branchQueue.push(p);
//    }
//
//    public boolean removePrediction(){
//        return branchQueue.pop();
//    }
}
