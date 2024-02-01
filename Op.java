public class Op {

    Op(){}

    public class Add extends Instruction{

        private static final int DURATION = 2;

        Add(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class AddI extends Instruction{
        private static final int DURATION = 2;

        AddI(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class Mul extends Instruction{
        private static final int DURATION = 4;

        Mul(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }

    public class MulI extends Instruction{
        private static final int DURATION = 4;

        MulI(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }

    public class Cmp extends Instruction{

        private static final int DURATION = 2;

        Cmp(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class Ld extends Instruction{
        private static final int DURATION = 2;

        Ld(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class LdC extends Instruction{
        private static final int DURATION = 4;

        LdC(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }

    public class St extends Instruction{
        private static final int DURATION = 4;

        St(RegisterName rd, RegisterName rs, int immediate){
            super(DURATION, immediate, rd, rs);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }

    public class BrLZ extends Instruction{

        private static final int DURATION = 2;

        BrLZ(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class JpLZ extends Instruction{
        private static final int DURATION = 2;

        JpLZ(RegisterName rd, int immediate){
            super(DURATION, immediate, rd);
        }

        @Override
        public Opcode visit(InstructionVisitor v) {
            return v.accept(this);
        }
    }

    public class Br extends Instruction{
        private static final int DURATION = 1;

        Br(int immediate){
            super(DURATION, immediate);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }

    public class Jp extends Instruction{
        private static final int DURATION = 1;

        Jp(int immediate){
            super(DURATION, immediate);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }
}
