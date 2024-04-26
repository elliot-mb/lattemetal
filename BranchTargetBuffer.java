import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchTargetBuffer {


    private final Map<Integer, Integer> pcToPredictionTarget;
    private final Map<Integer, twoBitPred> pcToPredState;
    private CircluarQueue<Integer> orderAdded;

    private final int size;

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
        this.size = size;
    }

    public boolean shouldBranch(int pc){
        return Processor.PREDICTOR.equals(Processor.predictor.twoBit)
                ? pcToPredictionTarget.containsKey(pc) && (pcToPredState.get(pc).equals(twoBitPred.weakTaken) || pcToPredState.get(pc).equals(twoBitPred.strongTaken))
                : pcToPredictionTarget.containsKey(pc);
    }

    private void addTo(int pc, int target){
        if(!pcToPredictionTarget.containsKey(pc) && orderAdded.isFull()){
            pcToPredictionTarget.remove(orderAdded.pop()); //remove oldest mapping
        }
        if(!pcToPredictionTarget.containsKey(pc)) orderAdded.push(pc);
        pcToPredictionTarget.put(pc, target);

    }

    private void removeInsideOrderAdded(int pc){
        List<Integer> xs = orderAdded.peekXsReverse();
        CircluarQueue<Integer> newOrder = new CircluarQueue<Integer>(size);
        for(Integer x : xs){
            if(!x.equals(pc)) newOrder.push(x);
        }
        orderAdded = newOrder;
    }

    //should be interacted with in program order
    public void predictForThisBranch(int pc, boolean taken, int target){
        if(taken){
            //do some prediction
            if(Processor.PREDICTOR.equals(Processor.predictor.twoBit)){
                //strong taken does not get modified
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakTaken)) pcToPredState.put(pc, twoBitPred.strongTaken); //upgrade
                else if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakNotTaken)) pcToPredState.put(pc, twoBitPred.weakTaken); //upgrade
                else if(!pcToPredState.containsKey(pc)) {
                    pcToPredState.put(pc, twoBitPred.weakNotTaken);
                    addTo(pc, target);
                }
            }else{
                addTo(pc, target);
            }
            //to do a static predictor we can compare pc and target to discern direction
            //to do a dynamic predictor we can store some state for each branch to delay the decisions we make
        }else{
            //do some other prediction
            if(Processor.PREDICTOR.equals(Processor.predictor.twoBit)){
                if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.strongTaken)) pcToPredState.put(pc, twoBitPred.weakTaken); //downgrade
                else if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakTaken)) pcToPredState.put(pc, twoBitPred.weakNotTaken); //downgrade
                else if(pcToPredState.containsKey(pc) && pcToPredState.get(pc).equals(twoBitPred.weakNotTaken)) {
                    pcToPredState.remove(pc); //annihilate this predictor
                    pcToPredictionTarget.remove(pc);
                    removeInsideOrderAdded(pc);
                }
            }else{
                pcToPredictionTarget.remove(pc);
                removeInsideOrderAdded(pc);
            }
        }
    }

    public int getPredictionTarget(int pc){
        if(!pcToPredictionTarget.containsKey(pc)) throw new RuntimeException("getPredictionTarget: no entry for pc '" + pc + "'");
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
