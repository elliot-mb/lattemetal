

import java.io.PrintStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final IssueUnit isu;
    private final ArithmeticLogicUnit alu;
    private final RegisterFile rf;
    private final Memory mem;
    private final FetchUnit fe;
    private final DecodeUnit de;
    private final LoadStoreUnit lsu;
    private final Scoreboard sb;
    private int tally;
    private final WriteBackUnit wb;

    private final PipelineRegister prefec = new PipelineRegister(); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister();
    private final PipelineRegister decIsu = new PipelineRegister();
    private final PipelineRegister isuExe = new PipelineRegister();
    private final PipelineRegister exeMem = new PipelineRegister();
    private final PipelineRegister memWrt = new PipelineRegister();
    private final PipelineRegister voided = new PipelineRegister(); //ignored pipe register to satisfy Unit inheritence

    private final int DP_ACC = 2;

    Processor(InstructionCache ic, Memory... mem) throws RuntimeException{
        if(mem.length > 1) throw new RuntimeException("Processor: this constructor cannot have more than one memories");
        this.sb = new Scoreboard();
        this.ic = ic;
        this.tally = 0;
        this.pc = new ProgramCounter(ic.numInstrs());
        this.rf = new RegisterFile();
        this.mem = mem.length > 0 ? mem[0] : new Memory();
        this.alu = new ArithmeticLogicUnit(isuExe, exeMem);
        this.fe = new FetchUnit(ic, prefec, fecDec);
        this.de = new DecodeUnit(this.rf, fecDec, decIsu);
        this.wb = new WriteBackUnit(this.rf, this.sb, memWrt, voided);
        this.lsu = new LoadStoreUnit(this.mem, this.pc, exeMem, memWrt);
        this.isu = new IssueUnit(this.sb, decIsu, isuExe);
    }

//    private void sendSingleInstruction(){
//        //System.out.println("push");
//        if(prefec.canPull()) prefec.pull(); //empty register
//        prefec.push(Utils.opFactory.new No());
//        prefec.setPcVal(pc.getCount());
//    }


    private boolean isPipelineBeingUsed(){
        return prefec.canPull() || fecDec.canPull() || decIsu.canPull() || isuExe.canPull() || exeMem.canPull() || memWrt.canPull() || voided.canPull() ||
                !wb.isDone() || !lsu.isDone() || !alu.isDone() || !de.isDone() || !fe.isDone() || !isu.isDone();
    }

    private void flushPipeline(){
        System.out.println("flush");
        fe.flush();
        de.flush();
        isu.flush();
        alu.flush();
        lsu.flush();
        wb.flush();
        prefec.flush();
        fecDec.flush();
        decIsu.flush();
        isuExe.flush();
        exeMem.flush();
        memWrt.flush();
        voided.flush();
    }

    public Memory run(PrintStream debugOut){
        debugOut.println(ic);
        voided.push(Utils.opFactory.new No());
        voided.setPcVal(0);
        int retiredInstrCount = 0;
        List<Instruction> retiredInstrs = new ArrayList<Instruction>();

        //AbstractMap<Instruction, Integer> inFlights = new HashMap<Instruction, Integer>();
        while(isPipelineBeingUsed() || !pc.isDone()){
            wb.clk();
            lsu.clk();
            if(lsu.needsFlushing()) flushPipeline();
            alu.clk();
            //include some sort of issue stage that works from a scoreboard and tomasulos algorithm
            isu.clk();
            de.clk();
            fe.clk();
            debugOut.println("\t[" + fe + fecDec + de + decIsu + isu + isuExe + alu + exeMem + lsu + memWrt + wb + "]\t@" + tally + "\tpc " + pc.getCount());
            if(prefec.canPush() && !pc.isDone()){//&& !(!voided.canPull() && fe.getIsBranch())) {
                prefec.push(Utils.opFactory.new No());
                prefec.setPcVal(pc.getCount());
                pc.incr();
            }
            tally++;
            if(voided.canPull()) {
                retiredInstrs.add(voided.pull());
                retiredInstrCount++;
            } //delete whats inside (voided is used to detect when writebacks are finished)
        }
        debugOut.println("registers (dirty): " + rf);
        debugOut.println("memory: " + mem);
        debugOut.println("run: program finished in " + tally + " cycles");
        debugOut.println("run: instructions per cycle " + Utils.toDecimalPlaces((float) retiredInstrCount / tally, DP_ACC));
        return mem;
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
