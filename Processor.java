public class Processor {

    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final ArithmeticLogicUnit alu;
    private final RegisterFile rf;
    private final Memory mem;
    private final Decoder de;
    private int tally;
    private WriteBackUnit wb;

    /**
     * parts that read and write to these buffers should check that they are null first before writing (otherwise assume
     * a stall)
     */
    private final PipelineRegister IF_ID = new PipelineRegister(); //in-flight register between fetch, and decode
    private final PipelineRegister ID_EX = new PipelineRegister(); //in-flight register between decode, and execute
    private final PipelineRegister EX_MM = new PipelineRegister(); //in-flight register between execute, and memory unit
    private final PipelineRegister MM_WB = new PipelineRegister(); //in-flight register between memory unit, and write back

    Processor(InstructionCache ic){
        this.ic = ic;
        this.pc = new ProgramCounter(ic.numInstrs());
        this.rf = new RegisterFile();
        this.mem = new Memory();
        this.alu = new ArithmeticLogicUnit(ID_EX, EX_MM);
        this.de = new Decoder(this.rf);
        this.tally = 0;
        this.wb = new WriteBackUnit(this.rf);
    }

    public void run(){
        OpCoder preDecoder = new OpCoder();
        System.out.println(ic);
        while(!pc.isDone()){
            Instruction fetched = ic.getInstruction(pc.getCount());
            //IF/ID
            OpCode code = fetched.visit(preDecoder); //if at a branch, we stall until the operand is done
            Instruction decoded = de.decode(fetched);
            //ID/EX
            alu.loadFilledOp(decoded);
            while(!alu.isDone()){
                alu.clk();
                tally++;
            }
            Instruction finished = alu.requestOp();
            //EX/MEM
            if(code != OpCode.br && code != OpCode.brlz && code != OpCode.jp && code != OpCode.jplz){
                pc.incr();
            }
            //load/store unit
            //MEM/WB
            wb.go(finished);
        }
        System.out.println("run: program finished in " + tally + " cycles");
        System.out.println("registers: " + rf);
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
