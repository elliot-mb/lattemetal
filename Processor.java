

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class Processor {
    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final IssueUnit isu;
    private final ArithmeticLogicUnit alu;
    private final RegisterFile rf;
    private final Memory mem;
    private final FetchUnit feu;
    private final DecodeUnit deu;
    private final LoadStoreUnit lsu;
    private final BranchUnit bru;
    private final WriteBackUnit wbu;
    private final Scoreboard sb;
    private int tally;

    private static final int ALU_RS_COUNT = 2;
    private static final int LSU_RS_COUNT = 2;

    private final List<ReservationStation> aluRs = new ArrayList<ReservationStation>(ALU_RS_COUNT);
    //lsu? load store buffers?

    private final PipelineRegister prefec = new PipelineRegister(); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister();
    private final PipelineRegister deuIsu = new PipelineRegister();
    private final PipelineRegister isuAlu = new PipelineRegister();
    private final PipelineRegister isuLsu = new PipelineRegister();
    private final PipelineRegister aluBru = new PipelineRegister();
    private final PipelineRegister lsuBru = new PipelineRegister();
    private final PipelineRegister bruWbu = new PipelineRegister();
    //private final PipelineRegister aluLsu = new PipelineRegister();
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
        this.feu = new FetchUnit(
                this.ic,
                this.pc,
                new PipelineRegister[]{prefec},
                new PipelineRegister[]{fecDec});
        this.deu = new DecodeUnit(
                this.rf,
                new PipelineRegister[]{fecDec},
                new PipelineRegister[]{deuIsu}); //loadstores go down the latter pipe
        this.isu = new IssueUnit(
                this.sb,
                this.rf,
                new PipelineRegister[]{deuIsu},
                new PipelineRegister[]{isuAlu, isuLsu});
        this.alu = new ArithmeticLogicUnit(
                this.sb,
                new PipelineRegister[]{isuAlu},
                new PipelineRegister[]{aluBru});
        this.lsu = new LoadStoreUnit(
                this.sb,
                this.mem,
                new PipelineRegister[]{isuLsu},
                new PipelineRegister[]{lsuBru});
        this.bru = new BranchUnit(
                this.pc,
                new PipelineRegister[]{aluBru, lsuBru},
                new PipelineRegister[]{bruWbu}
        );
        this.wbu = new WriteBackUnit(
                this.rf,
                this.sb,
                new PipelineRegister[]{bruWbu},
                new PipelineRegister[]{voided});
    }

//    private void sendSingleInstruction(){
//        //System.out.println("push");
//        if(prefec.canPull()) prefec.pull(); //empty register
//        prefec.push(Utils.opFactory.new No());
//        prefec.setPcVal(pc.getCount());
//    }


    private boolean isPipelineBeingUsed(){
        return prefec.canPull() || fecDec.canPull() || deuIsu.canPull() || isuAlu.canPull() || isuLsu.canPull() ||
                bruWbu.canPull() || aluBru.canPull() || voided.canPull() || !wbu.isDone() || !lsu.isDone() ||
                lsuBru.canPull() || !alu.isDone() || !deu.isDone() || !feu.isDone() || !isu.isDone();
    }

    private void flushPipeline(){
        //System.out.println("flush");
        feu.flush();
        deu.flush();
        isu.flush();
        alu.flush();
        lsu.flush();
        wbu.flush();
        prefec.flush();
        fecDec.flush();
        deuIsu.flush();
        isuAlu.flush();
        isuLsu.flush();
        aluBru.flush();
        lsuBru.flush();
        bruWbu.flush();
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
            wbu.clk();
            bru.clk();
            if(bru.needsFlushing()) flushPipeline();
            lsu.clk();
            alu.clk();
            isu.clk();
            deu.clk();
            feu.clk();
            debugOut.println("\t[" + feu + " " + fecDec + " " + deu + " " + deuIsu+ " " + isu + " " + "(" + isuAlu + ","
                    + isuLsu + ") (" + alu + "," + lsu + ") (" + aluBru + "," + lsuBru + ") " + bru + " " + bruWbu + " "
                    + wbu + "]\t@" + tally + "\tpc " + pc.getCount() + "\t" + rf);
            if(prefec.canPush() && !pc.isDone()){//&& !(!voided.canPull() && fe.getIsBranch())) {
                prefec.push(Utils.opFactory.new No());
                prefec.setPcVal(pc.getCount());
                //pc.incr();
            }
            tally++;
            if(voided.canPull()) {
                retiredInstrs.add(voided.pull());
                retiredInstrCount++;
            } //delete whats inside (voided is used to detect when writebacks are finished)
            if(tally % 1000 == 0) debugOut.print("\r" + tally / 1000 + "K cycles");
        }
        debugOut.println("registers (dirty): " + rf);
        debugOut.println("memory: " + mem);
        debugOut.println("run: program finished in " + tally + " cycles");
        debugOut.println("run: instructions per cycle " + Utils.toDecimalPlaces((float) retiredInstrCount / tally, DP_ACC));
        BinaryOperator<String> newLnConn = new BinaryOperator<String>() {
            @Override
            public String apply(String s, String s2) {
                return s + '\n' + s2;
            }
        };
        //debugOut.println("run: instructions completed like " + retiredInstrs.stream().map(Instruction::toString).reduce(newLnConn));
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
