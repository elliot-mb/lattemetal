public class Processor {

    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final ArithmeticLogicUnit alu;
    private final RegisterFile rf;
    private final Memory mem;
    private final DecodeUnit de;
    private final LoadStoreUnit lsu;
    private int tally;
    private final WriteBackUnit wb;

    private final PipelineRegister fecDec = new PipelineRegister();
    private final PipelineRegister decExe = new PipelineRegister();
    private final PipelineRegister exeMem = new PipelineRegister();
    private final PipelineRegister memWrt = new PipelineRegister();

    Processor(InstructionCache ic, Memory... mem) throws RuntimeException{
        if(mem.length > 1) throw new RuntimeException("Processor: this constructor cannot have more than one memories");
        this.ic = ic;
        this.pc = new ProgramCounter(ic.numInstrs());
        this.rf = new RegisterFile();
        this.mem = mem.length > 0 ? mem[0] : new Memory();
        this.alu = new ArithmeticLogicUnit(decExe, exeMem);
        this.de = new DecodeUnit(this.rf, fecDec, decExe);
        this.tally = 0;
        this.wb = new WriteBackUnit(this.rf);
        this.lsu = new LoadStoreUnit(this.mem, this.pc, exeMem, memWrt);
    }

    public void run(){
        Id preDecoder = new Id();
        System.out.println(ic);
        while(!pc.isDone()){
            Instruction fetched = ic.getInstruction(pc.getCount());
            //System.out.println("'" + fetched + "' @ cycle " + Integer.toString(tally));
            tally++;
            Opcode code = fetched.visit(preDecoder);
            if(fecDec.canPush()) fecDec.push(fetched);
            fecDec.setPc(pc.getCount() + 1);
            while(!decExe.canPull()){
                de.clk();
                tally++;
            }
            while(!exeMem.canPull()){
                alu.clk();
                tally++;
            }
//            int pcVal = exeMem.getPc();
//            boolean branchTaken = exeMem.isFlag();
//            Instruction decoded = exeMem.pull();
//            decoded.rst(); //refill the duration for memory operations
//            System.out.println("lsu start for '" + decoded + "' @ cycle " + Integer.toString(tally));
            while(!memWrt.canPull()){
                lsu.clk();
                tally++;
            }
            Instruction decoded = memWrt.pull();
            wb.go(decoded);
            tally++;
//            if(code != Opcode.br && code != Opcode.brlz && code != Opcode.jp && code != Opcode.jplz){
//                pc.incr();
//            }
        }
        System.out.println("run: program finished in " + tally + " cycles");
        System.out.println("registers (dirty): " + rf);
        System.out.println("memory: " + mem);
    }

}

/**
 * PICK AND INTERESTING SET OF EXAMPLE PROGRAMS: come up with more interesting examples (for experiments too)
 * a trick to writing pipelined simulation is write it so that it does the last stage first, then works backwards
 *
 * secure branch predictors are cool (spectre-aware)
 * - the clever your branch predictor the more they value that
 *
 * if you do mulitple kinds of feature:
 * - comparing and contrasting different branch predictors etc. to hot swap between them
 * - run them in the viva and show the analysis live!
 *
 * how configurable do we want the processor to be:
 * - 'pretty configurable' is harder
 * - marks are not allocated for configurability
 *
 * IN THE VIVA:
 * - you have one question you can pick and one picked for you; memory hierarchy is a good one to talk about in the viva
 *
 * first territory:
 * - pipelined & superscalar & out of order with a branch predictor (you could get into the seventies)
 *
 * experiments:
 * - hypotheses like varying reservation station size, etc.
 * -
 */


/**
 * 1) a) (Qualcomm)                                                                     X it was apple, qualcomm has been in bristol for at least 15 years
 * 2) d) (Hennesy and Patterson)                                                        O
 * 3) b) (Instruction Set Architecture)                                                 O
 * 4) Reduced Instruciton Set Computing                                                 O (computers)
 * 5) a) Data and Name                                                                  O
 * 6) Read After Write                                                                  O
* 7) Out of Order                                                                       O
* 8) the other two arrangements are unified, and hybrid(?)                              O (not hybrid, but grouped; grouped is allocating functionally similar/groups of executions)
 * 9) d) conditional branches                                                           O conditional branches
 * 10) b) backwards taken, forwards not taken                                           O forwards branches are not taken about half the time, so always taken appears to be similar
 */
//1) Qualcomm
