public class Op {

    Op(){}

    public class Add extends Instruction{ //all operations extend hasduration also by proxy

        private static final int DURATION = 1;

        Add(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
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
        public Instruction copy(){
            Add add = new Add(getRd(), getRs(), getRt());
            add.setRdVal(getRdVal());
            add.setRsVal(getRsVal());
            add.setRtVal(getRtVal());
            add.setResult(result);
            return add;
        }

    }

    public class AddI extends Instruction{
        private static final int DURATION = 1;

        AddI(RegisterName rd, RegisterName rs, Integer immediate){
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
        public Instruction copy(){
            AddI addi = new AddI(getRd(), getRs(), getImVal());
            addi.setRdVal(getRdVal());
            addi.setRsVal(getRsVal());
            addi.setResult(result);
            return addi;
        }

    }

    public class Mul extends Instruction{
        private static final int DURATION = 3;

        Mul(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
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
        public Instruction copy(){
            Mul mul = new Mul(getRd(), getRs(), getRt());
            mul.setRdVal(getRdVal());
            mul.setRsVal(getRsVal());
            mul.setRtVal(getRtVal());
            mul.setResult(result);
            return mul;
        }

    }

    public class MulI extends Instruction{
        private static final int DURATION = 3;

        MulI(RegisterName rd, RegisterName rs, Integer immediate){
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
        public Instruction copy(){
            MulI muli = new MulI(getRd(), getRs(), getImVal());
            muli.setRdVal(getRdVal());
            muli.setRsVal(getRsVal());
            muli.setResult(result);
            return muli;
        }

    }

    public class Cmp extends Instruction{

        private static final int DURATION = 2;

        Cmp(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
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
        public Instruction copy(){
            Cmp cmp = new Cmp(getRd(), getRs(), getRt());
            cmp.setRdVal(getRdVal());
            cmp.setRsVal(getRsVal());
            cmp.setRtVal(getRtVal());
            cmp.setResult(result);
            return cmp;
        }

    }

    public class Ld extends Instruction{
        private static final int DURATION = 1;

        Ld(RegisterName rd, RegisterName rs, Integer immediate){
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
        public Instruction copy(){
            Ld ld = new Ld(getRd(), getRs(), getImVal());
            ld.setRdVal(getRdVal());
            ld.setRsVal(getRsVal());
            ld.setResult(result);
            return ld;
        }

    }

    public class LdC extends Instruction{
        private static final int DURATION = 1;

        LdC(RegisterName rd, Integer immediate){
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
        public Instruction copy(){
            LdC ldc = new LdC(getRd(), getImVal());
            ldc.setRdVal(getRdVal());
            ldc.setResult(result);
            return ldc;
        }

    }

    public class St extends Instruction{
        private static final int DURATION = 1;

        St(RegisterName rd, RegisterName rs, Integer immediate){
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
        public Instruction copy(){
            St st = new St(getRd(), getRs(), getImVal());
            st.setRdVal(getRdVal());
            st.setRsVal(getRsVal());
            st.setResult(result);
            return st;
        }
    }

    public class BrLZ extends Instruction{

        private static final int DURATION = 1;

        BrLZ(RegisterName rd, Integer immediate){
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
        public Instruction copy() {
            BrLZ brlz = new BrLZ(getRd(), getImVal());
            brlz.setRdVal(getRdVal());
            brlz.setResult(result);
            return brlz;
        }
    }

    public class JpLZ extends Instruction{
        private static final int DURATION = 1;

        JpLZ(RegisterName rd, Integer immediate){
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
        public Instruction copy(){
            JpLZ jplz = new JpLZ(getRd(), getImVal());
            jplz.setRdVal(getRdVal());
            jplz.setResult(result);
            return jplz;
        }
    }

    public class Br extends Instruction{
        private static final int DURATION = 1;

        Br(Integer immediate){
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
        public Instruction copy(){
            Br br = new Br(getImVal());
            br.setResult(result);
            return br;
        }
    }

    public class Jp extends Instruction{
        private static final int DURATION = 1;

        Jp(Integer immediate){
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
        public Instruction copy(){
            Jp jp = new Jp(getImVal());
            jp.setResult(result);
            return jp;
        }
    }

    public class No extends Instruction{
        private static final int DURATION = 1;

        No(){
            super(DURATION, 0);
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }

        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        @Override
        public Instruction copy() {
            return new No();
        }
    }
}
