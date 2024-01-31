public abstract class Instruction {

    private int duration;
    private int rd; //always not null
    private int rs;
    private int rt;

    // all instructions can be seen as
    Instruction(int rd, int rs, int rt){
        this.rd = rd;
        this.rs = rs;
        this.rt = rt;
    }



}
