public class ReorderBuffer {

    private final CircluarQueue<Instruction> buffer;

    ReorderBuffer(int size){
        buffer = new CircluarQueue<Instruction>(size);
    }



}
