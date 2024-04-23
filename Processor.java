

import java.io.PrintStream;
import java.util.*;

public class Processor {
    private final Map<Integer, List<Integer>> cdb;
    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final IssueUnit isu;
    private final ArithmeticLogicUnit alu1, alu2, alu3;
    private final RegisterFile rf;
    private final Memory mem;
    private final FetchUnit fec;
    private final DecodeUnit dec;
    private final LoadStoreUnit lsu1, lsu2;
    private final BranchUnit bru;
    private final WriteBackUnit wbu;
    private final ReorderBuffer rob;
    private final RegisterAliasTable rat;
    private int tally;

    private static final int ALU_RS_COUNT = 4;
    private static final int LSU_RS_COUNT = 2;
    private static final int BRU_RS_COUNT = 2;
    private static final int DP_ACC = 2;
    private static final int ROB_INTIATES_FLUSH = -1;
    private static final int ROB_ENTRIES = 8;

    public static final int FLUSH_ALL = -1;

//    private final List<ReservationStation> aluRs = new ArrayList<ReservationStation>();
//    private final List<ReservationStation> lsuRs = new ArrayList<ReservationStation>();
//    //lsu? load store buffers?
    private final ReservationGroup exeRss;
    private final ReservationGroup lsuRss;
    private final ReservationGroup bruRss;

    private final PipelineRegister prefec = new PipelineRegister(8); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister(8);
    private final PipelineRegister decIsu = new PipelineRegister(8); //instruction queue
    private final PipelineRegister exeWbu = new PipelineRegister(8);
    //private final PipelineRegister aluLsu = new PipelineRegister();
    private final PipelineRegister rtired = new PipelineRegister(1); //ignored pipe register to satisfy Unit inheritence
    private final PipelineRegister delete = new PipelineRegister(100);

    Processor(InstructionCache ic, Memory... mem) throws RuntimeException{
        if(mem.length > 1) throw new RuntimeException("Processor: this constructor cannot have more than one memories");
        this.cdb = new HashMap<Integer, List<Integer>>();
        this.mem = mem.length > 0 ? mem[0] : new Memory();
        this.rf = new RegisterFile(cdb);
        this.pc = new ProgramCounter(ic.numInstrs());
        this.rob = new ReorderBuffer(ROB_ENTRIES, cdb, rf, this.mem, this.pc);
        this.ic = ic;
        this.tally = 0;
        this.rat = new RegisterAliasTable(cdb, rob);
        this.rob.setRat(rat); //avoid circular dependency

        this.exeRss = new ReservationGroup(ALU_RS_COUNT, cdb, rob, rf, rat);
        this.lsuRss = new ReservationGroup(LSU_RS_COUNT, cdb, rob, rf, rat);
        this.bruRss = new ReservationGroup(BRU_RS_COUNT, cdb, rob, rf, rat);

        this.fec = new FetchUnit(
                this.ic,
                this.pc,
                new PipeLike[]{prefec},
                new PipeLike[]{fecDec});
        this.dec = new DecodeUnit(
                this.rf,
                new PipeLike[]{fecDec},
                new PipeLike[]{decIsu}); //loadstores go down the latter pipe
        this.isu = new IssueUnit(
                this.rf,
                this.rob,
                this.rat,
                new PipeLike[]{decIsu},
                new PipeLike[]{exeRss, lsuRss, bruRss});
        this.alu1 = new ArithmeticLogicUnit(
                this.cdb,
                this.rob,
                this.rf,
                this.rat,
                new PipeLike[]{exeRss},
                new PipeLike[]{exeWbu});
        this.alu2 = new ArithmeticLogicUnit(
                this.cdb,
                this.rob,
                this.rf,
                this.rat,
                new PipeLike[]{exeRss},
                new PipeLike[]{exeWbu});
        this.alu3 = new ArithmeticLogicUnit(
                this.cdb,
                this.rob,
                this.rf,
                this.rat,
                new PipeLike[]{exeRss},
                new PipeLike[]{exeWbu});
        this.lsu1 = new LoadStoreUnit(
                this.mem,
                this.rf,
                this.rat,
                this.cdb,
                this.rob,
                new PipeLike[]{lsuRss},
                new PipeLike[]{exeWbu});
        this.lsu2 = new LoadStoreUnit(
                this.mem,
                this.rf,
                this.rat,
                this.cdb,
                this.rob,
                new PipeLike[]{lsuRss},
                new PipeLike[]{exeWbu});
        this.bru = new BranchUnit(
                this.pc,
                this.fec,
                new PipeLike[]{bruRss},
                new PipeLike[]{exeWbu}
        );
        this.wbu = new WriteBackUnit(
                this.rf,
                this.rob,
                this.rat,
                this.cdb,
                new PipeLike[]{exeWbu},
                new PipeLike[]{delete});
    }

//    private void sendSingleInstruction(){
//        //System.out.println("push");
//        if(prefec.canPull()) prefec.pull(); //empty register
//        prefec.push(Utils.opFactory.new No());
//        prefec.setPcVal(pc.getCount());
//    }


    private boolean isPipelineBeingUsed(){
        return prefec.canPull() || fecDec.canPull() || decIsu.canPull() ||
                exeWbu.canPull() || rtired.canPull() || !wbu.isDone() || !lsu1.isDone() || !lsu2.isDone() ||
                !alu1.isDone() || !alu2.isDone() || !alu3.isDone() || !dec.isDone() || !fec.isDone() || !isu.isDone() || !rob.isEmpty();
    }

    private void flushPipeline(int branchIdInRob, PrintStream debugOut){
        debugOut.println("flush from robEntry " + branchIdInRob);
        fec.flush(branchIdInRob);
        dec.flush(branchIdInRob);
        isu.flush(branchIdInRob);
        alu1.flush(branchIdInRob);
        alu2.flush(branchIdInRob);
        alu3.flush(branchIdInRob);
        lsu1.flush(branchIdInRob);
        lsu2.flush(branchIdInRob);
        wbu.flush(branchIdInRob);
        bru.flush(branchIdInRob);
        prefec.flush(branchIdInRob);
        fecDec.flush(branchIdInRob);
        decIsu.flush(branchIdInRob);
        exeWbu.flush(branchIdInRob);
        rtired.flush(branchIdInRob);
        exeRss.flush(branchIdInRob);
        lsuRss.flush(branchIdInRob);
        bruRss.flush(branchIdInRob);
        rat.flushFrom(branchIdInRob);
        //if(branchIdInRob != ROB_INTIATES_FLUSH) rob.flushFrom(branchIdInRob); always initiates flush so we dont call it here
    }

    private String pipelineToString(){
        return "\t[" + prefec +
                fec + " " + fecDec + " " +
                dec + " " + decIsu + " " +
                isu + " " + "(" + exeRss + "," + lsuRss + "," + bruRss + ") ("
                + alu1 + alu2 + alu3 + ", " + lsu1 + lsu2 + ", " + bru + ") (" + exeWbu + ") "
                + wbu + "]\t@"
                + tally + "\tpc " + pc.getCount() + "\t" + "\t" + rob;
    }

    public Memory run(PrintStream debugOut, Integer divergenceLim){
        debugOut.println(ic);
//        rtired.push(new PipelineEntry(Utils.opFactory.new No(), 0, false));
        int retiredInstrCount = 0;
        List<Instruction> retiredInstrs = new ArrayList<Instruction>();
        pc.set(0);

        //AbstractMap<Instruction, Integer> inFlights = new HashMap<Instruction, Integer>();
        while((isPipelineBeingUsed() || !pc.isDone()) && (divergenceLim == null || tally < divergenceLim)){

            wbu.clk();
            bru.clk();
            lsu1.clk();
            lsu2.clk();
            alu1.clk();
            alu2.clk();
            alu3.clk();
            debugOut.println(pipelineToString());

            int lastCountInDecIsu = -1;
            isu.clk();
            while(decIsu.canPull() && lastCountInDecIsu != decIsu.getCount()){
                lastCountInDecIsu = decIsu.getCount();
                isu.clk();
            }
            exeRss.update(); //update reservation groups! only once because the cdb doesnt change mid-cycle
            lsuRss.update();
            bruRss.update();

            rob.clk(); //read off the cdb
            if(rob.needsFlushing()) {
                flushPipeline(rob.getShouldFlushWhere(), debugOut);
            }
            rob.doneFlushing();

            dec.clk();
            while(fecDec.canPull() && decIsu.canPush()){ //they all just shift a block along <=> they wont be able to do more than one pipeline buffer's worth!
                dec.clk();
            }

            fec.clk();
            while(fecDec.canPush() && !pc.isDone()){ //they all just shift a block along <=> they wont be able to do more than one pipeline buffer's worth!
                prefec.push(new PipelineEntry(Utils.opFactory.new No(), pc.getCount(), false));
                fec.clk();
            }

            tally++;
//            while(rtired.canPull()) { //if we retire more that one instruction per cycle
//                retiredInstrs.add(rtired.pull().getOp());
//                retiredInstrCount++;
//            } //delete whats inside (voided is used to detect when writebacks are finished)
            delete.flush(FLUSH_ALL); // any instructions we want to throw away can be put into delete

            if(tally % 1000 == 0) debugOut.print("\r" + tally / 1000 + "K cycles");
            debugOut.println(cdb.keySet().toString() + cdb.values().toString());
            cdb.clear();
        }
        if(divergenceLim != null && tally >= divergenceLim) throw new RuntimeException("run: program considered to diverge after " + divergenceLim + " instrs");
        debugOut.println("registers (dirty): " + rf);
        debugOut.println("memory: " + mem);
        debugOut.println("run: program finished in " + tally + " cycles");
        debugOut.println("run: instructions per cycle " + Utils.toDecimalPlaces((float) rob.getCommitted() / tally, DP_ACC));
        debugOut.println(Arrays.toString(mem.getData()));
        //debugOut.println("run: instructions \n" +  Utils.writeList(rob.getCommittedInstrs()));
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
