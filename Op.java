public class Op {

    Op(){}

    // term shapes:
    //      three registers
    //      two registers, one immediate
    //      one register, one immediate
    //      one immediate


    // operator argument validation on the four instruction shapes
    private static void shapeRdRsRt(Instruction instr, RegisterName rd, RegisterName rs, RegisterName rt){
        if(rd == null || rs == null || rt == null)
            throw new RuntimeException(instr.visit(new Id()) + ": missing at least one register reference");
    }

    private static void shapeRdRsIm(Instruction instr, RegisterName rd, RegisterName rs, int im){
        if(rd == null || rs == null)
            throw new RuntimeException(instr.visit(new Id()) +": missing at least one register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(instr.visit(new Id()) +": missing immediate");
    }

    private static void shapeRdIm(Instruction instr, RegisterName rd, int im){
        if(rd == null)
            throw new RuntimeException(instr.visit(new Id()) +": missing the register reference");
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(instr.visit(new Id()) +": missing immediate");
    }

    private static void shapeIm(Instruction instr, int im){
        if(im == Assembler.IMM_UNSET)
            throw new RuntimeException(instr.visit(new Id()) +": missing immediate");
    }

    public class Add extends Instruction{

        private static final int DURATION = 2;

        Add(RegisterName rd, RegisterName rs, RegisterName rt){
            super(DURATION, Assembler.IMM_UNSET, rd, rs, rt);
            shapeRdRsRt(this, rd, rs, rt);
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
            shapeRdRsIm(this, rd, rs, immediate);
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
            shapeRdRsRt(this, rd, rs, rt);
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
            shapeRdRsIm(this, rd, rs, immediate);
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
            shapeRdRsRt(this, rd, rs, rt);
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
            shapeRdRsIm(this, rd, rs, immediate);
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
            shapeRdIm(this, rd, immediate);
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
            shapeRdRsIm(this, rd, rs, immediate);
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
            shapeRdIm(this, rd, immediate);
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
            shapeRdIm(this, rd, immediate);
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
            shapeIm(this, immediate);
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
            shapeIm(this, immediate);
        }

        @Override
        public Opcode visit(InstructionVisitor v){
            return v.accept(this);
        }
    }
}
