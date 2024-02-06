public class Op {

    Op(){}

    public class Add extends Instruction{ //all operations extend hasduration also by proxy

        private static final int DURATION = 2;

        Add(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
            checkShape(rd, rs, rt);
        }


        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public void setRtVal(int v) { rtVal = v; }
        public int getRtVal() { return rtVal; }

    }

    public class AddI extends Instruction{
        private static final int DURATION = 2;

        AddI(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public int getImVal() { return im; }
    }

    public class Mul extends Instruction{
        private static final int DURATION = 4;

        Mul(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
            checkShape(rd, rs, rt);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public void setRtVal(int v) { rtVal = v; }
        public int getRtVal() { return rtVal; }
    }

    public class MulI extends Instruction{
        private static final int DURATION = 4;

        MulI(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public int getImVal() { return im; }
    }

    public class Cmp extends Instruction{

        private static final int DURATION = 2;

        Cmp(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
            checkShape(rd, rs, rt);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public void setRtVal(int v) { rtVal = v; }
        public int getRtVal() { return rtVal; }
    }

    public class Ld extends Instruction{
        private static final int DURATION = 2;

        Ld(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) { return v.accept(this); }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public int getImVal() { return im; }
    }

    public class LdC extends Instruction{
        private static final int DURATION = 4;

        LdC(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public int getImVal() { return im; }
    }

    public class St extends Instruction{
        private static final int DURATION = 4;

        St(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public void setRsVal(int v) { rsVal = v; }
        public int getRsVal() { return rsVal; }
        public int getImVal() { return im; }
    }

    public class BrLZ extends Instruction{

        private static final int DURATION = 2;

        BrLZ(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public int getImVal() { return im; }
    }

    public class JpLZ extends Instruction{
        private static final int DURATION = 2;

        JpLZ(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) { return v.accept(this); }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public void setRdVal(int v) { rdVal = v; }
        public int getRdVal() { return rdVal; }
        public int getImVal() { return im; }
    }

    public class Br extends Instruction{
        private static final int DURATION = 1;

        Br(int immediate){
            super(DURATION, immediate);
            checkShape(immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public int getImVal() { return im; }
    }

    public class Jp extends Instruction{
        private static final int DURATION = 1;

        Jp(int immediate){
            super(DURATION, immediate);
            checkShape(immediate);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        public int getImVal() { return im; }
    }
}
