import java.util.ArrayList;
import java.util.List;

public class Op {



    public class Add extends Instruction{ //all operations extend hasduration also by proxy

        private static final int DURATION = 2;

        Add(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
            checkShape(rd, rs, rt);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }


        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            add.setId(getId());
            return add;
        }

    }

    public class AddI extends Instruction{
        private static final int DURATION = 2;

        AddI(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            addi.setId(getId());
            return addi;
        }

    }

    public class Mul extends Instruction{
        private static final int DURATION = 3;

        Mul(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
            checkShape(rd, rs, rt);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            mul.setId(getId());
            return mul;
        }

    }

    public class MulI extends Instruction{
        private static final int DURATION = 3;

        MulI(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            muli.setId(getId());
            return muli;
        }

    }

    public class Cmp extends Instruction{

        private static final int DURATION = 2;

        Cmp(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, null, rd, rs, rt);
            checkShape(rd, rs, rt);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            cmp.setId(getId());
            return cmp;
        }

    }

    public class Ld extends Instruction{
        private static final int DURATION = 2;

        Ld(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) { return v.accept(this); }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            Ld ld = new Ld(getRd(), getRs(), getImVal());
            ld.setRdVal(getRdVal());
            ld.setRsVal(getRsVal());
            ld.setResult(result);
            ld.setId(getId());
            return ld;
        }

    }

    public class LdC extends Instruction{
        private static final int DURATION = 2;

        LdC(RegisterName rd, Integer immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            ldc.setId(getId());
            return ldc;
        }

    }

    //load post indexing
    public class LdI extends Instruction{
        private static final int DURATION = 2;

        LdI(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) { return v.accept(this); }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            LdI ldi = new LdI(getRd(), getRs(), getImVal());
            ldi.setRdVal(getRdVal());
            ldi.setRsVal(getRsVal());
            ldi.setResult(result);
            ldi.setId(getId());
            return ldi;
        }
    }

    public class St extends Instruction{
        private static final int DURATION = 2;

        St(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            st.setId(getId());
            return st;
        }
    }

    //store post indexing
    public class StI extends Instruction{
        private static final int DURATION = 2;

        StI(RegisterName rd, RegisterName rs, Integer immediate){
            super(DURATION, immediate, rd, rs);
            checkShape(rd, rs, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            StI sti = new StI(getRd(), getRs(), getImVal());
            sti.setRdVal(getRdVal());
            sti.setRsVal(getRsVal());
            sti.setResult(result);
            sti.setId(getId());
            return sti;
        }
    }

    public class BrLZ extends Instruction{

        private static final int DURATION = 1;

        BrLZ(RegisterName rd, Integer immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }

        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            brlz.setId(getId());
            return brlz;
        }
    }

    public class JpLZ extends Instruction{
        private static final int DURATION = 1;

        JpLZ(RegisterName rd, Integer immediate){
            super(DURATION, immediate, rd);
            checkShape(rd, immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) { return v.accept(this); }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            JpLZ jplz = new JpLZ(getRd(), getImVal());
            jplz.setRdVal(getRdVal());
            jplz.setResult(result);
            jplz.setId(getId());
            return jplz;
        }

    }

    public class Br extends Instruction{
        private static final int DURATION = 1;

        Br(Integer immediate){
            super(DURATION, immediate);
            checkShape(immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            br.setId(getId());
            return br;
        }
    }

    public class Jp extends Instruction{
        private static final int DURATION = 1;

        Jp(Integer immediate){
            super(DURATION, immediate);
            checkShape(immediate);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v){
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
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
            jp.setId(getId());
            return jp;
        }
    }

    public class No extends Instruction{
        private static final int DURATION = 1;

        No(){
            super(DURATION, 0);

            List<Integer> regDest = new DestLocVisitor().accept(this);
            List<RegisterName> regName = new ArrayList<RegisterName>();
            for(Integer regNum : regDest){
                if(regNum != ReservationStation.NO_DEPENDENCY) regName.add(RegisterName.values()[regNum]);
            }
            regsList.addAll(regName);
            physicalRegistersNeeded = regsList.size();
        }

        @Override
        public Opcode visit(InstructionCodeVisitor v) {
            return v.accept(this);
        }
        @Override
        public List<Integer> visit(InstructionLocVisitor v) {
            return v.accept(this);
        }
        @Override
        public void visit(InstructionVoidVisitor v) {
            v.accept(this);
        }

        @Override
        public Instruction copy() {
            Instruction no = new No();
            no.setId(getId());
            return no;
        }
    }
}
