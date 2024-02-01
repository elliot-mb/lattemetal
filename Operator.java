public class Operator {

    public class Add extends Instruction{

        Add(Operand rd, Operand rs, Operand rt) throws RuntimeException{
            super(rd, rs, rt);
            this.duration = 2; //add takes two
            this.defined = new boolean[]{true, true, true};
            if(!definedOperands()) throw new RuntimeException("Add(): required operands not provided");
        }
    }
}
