

import java.io.PrintStream;
import java.util.*;

public class Processor {
    //@@@SETTINGS@@@
    private static final double CLOCK_SPEED_MHZ = 500;
    private static final int SUPERSCALAR_WIDTH = 1;
    private static final int ALU_COUNT = 1;
    private static final int LSU_COUNT = 1;
    private static final int BRU_COUNT = 1;
    private static final int ALU_RS_COUNT = 4;
    private static final int LSU_RS_COUNT = 2;
    private static final int BRU_RS_COUNT = 2;
    private static final int DP_ACC = 4;
    private static final int ROB_ENTRIES = 32;
    public static final int FLUSH_ALL = -1;
    public static final int PHYSICAL_REGISTER_FACTOR = 4; //how many times more physical registers we have than architectural ones
    //@@@DEPENDANT SETTINGS@@@
    private static final double ASSUMED_CYCLE_TIME = Math.pow(10, 3) / CLOCK_SPEED_MHZ;//ns
    public static final int PHYSICAL_REGISTER_COUNT = PHYSICAL_REGISTER_FACTOR * RegisterName.values().length;
    //@@@@@@
    private final ArrayList<ArithmeticLogicUnit> alusInUse;

    private final ArrayList<BranchUnit> brusInUse;

    private final ArrayList<LoadStoreUnit> lsusInUse;

    private final Map<Integer, List<Integer>> cdb;
    private final ProgramCounter pc;
    private final InstructionCache ic;
    private final IssueUnit isu;
    private final ArithmeticLogicUnit alu1, alu2, alu3, alu4;
    private final RegisterFile rf;
    private final Memory mem;
    private final FetchUnit fec;
    private final DecodeUnit dec;
    private final LoadStoreUnit lsu1, lsu2;
    private final BranchUnit bru1, bru2;
    private final WriteBackUnit wbu;
    private final ReorderBuffer rob;
    private final RegisterAliasTable rat;
    private int tally;

//    private final List<ReservationStation> aluRs = new ArrayList<ReservationStation>();
//    private final List<ReservationStation> lsuRs = new ArrayList<ReservationStation>();
//    //lsu? load store buffers?
    private final ReservationGroup exeRss;
    private final ReservationGroup lsuRss;
    private final ReservationGroup bruRss;

    private final PipelineRegister prefec = new PipelineRegister(SUPERSCALAR_WIDTH); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister(SUPERSCALAR_WIDTH);
    private final PipelineRegister decIsu = new PipelineRegister(SUPERSCALAR_WIDTH); //instruction queue
    private final PipelineRegister exeWbu = new PipelineRegister(SUPERSCALAR_WIDTH);
    //private final PipelineRegister aluLsu = new PipelineRegister();
    private final PipelineRegister rtired = new PipelineRegister(1); //ignored pipe register to satisfy Unit inheritence
    private final PipelineRegister delete = new PipelineRegister(SUPERSCALAR_WIDTH);

    Processor(InstructionCache ic, Memory... mem) throws RuntimeException{
        if(mem.length > 1) throw new RuntimeException("Processor: this constructor cannot have more than one memories");
        this.cdb = new HashMap<Integer, List<Integer>>();
        this.mem = mem.length > 0 ? mem[0] : new Memory();
        this.rf = new RegisterFile(cdb);
        this.pc = new ProgramCounter(ic.numInstrs());

        this.dec = new DecodeUnit(
                this.rf,
                new PipeLike[]{fecDec},
                new PipeLike[]{decIsu}); //loadstores go down the latter pipe

        this.rob = new ReorderBuffer(ROB_ENTRIES, cdb, rf, this.mem, this.pc, this.dec);
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
        this.alu4 = new ArithmeticLogicUnit(
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
        this.bru1 = new BranchUnit(
                this.pc,
                this.fec,
                new PipeLike[]{bruRss},
                new PipeLike[]{exeWbu}
        );
        this.bru2 = new BranchUnit(
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


        alusInUse = new ArrayList<ArithmeticLogicUnit>();
        alusInUse.add(alu1);
        if(ALU_COUNT >= 2) alusInUse.add(alu2);
        if(ALU_COUNT >= 3) alusInUse.add(alu3);
        if(ALU_COUNT >= 4) alusInUse.add(alu4);

        brusInUse = new ArrayList<BranchUnit>();
        brusInUse.add(bru1);
        if(BRU_COUNT >= 2) brusInUse.add(bru2);

        lsusInUse = new ArrayList<LoadStoreUnit>();
        lsusInUse.add(lsu1);
        if(LSU_COUNT >= 2) lsusInUse.add(lsu2);
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
                !alu1.isDone() || !alu2.isDone() || !alu3.isDone() || !alu4.isDone() || !bru1.isDone() || !bru2.isDone()
                || !dec.isDone() || !fec.isDone() || !isu.isDone() || !rob.isEmpty();
    }

    private void flushPipeline(int branchIdInRob, PrintStream debugOut){
        debugOut.println("flush from robEntry " + branchIdInRob);
        fec.flush(branchIdInRob);
        dec.flush(branchIdInRob);
        isu.flush(branchIdInRob);
        alu1.flush(branchIdInRob);
        alu2.flush(branchIdInRob);
        alu3.flush(branchIdInRob);
        alu4.flush(branchIdInRob);
        lsu1.flush(branchIdInRob);
        lsu2.flush(branchIdInRob);
        wbu.flush(branchIdInRob);
        bru1.flush(branchIdInRob);
        bru2.flush(branchIdInRob);
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
                + alu1 + alu2 + alu3 + alu4 + ", " + lsu1 + lsu2 + ", " + bru1 + bru2 + ") (" + exeWbu + ") "
                + wbu + "]\t@"
                + tally + "\tpc " + pc.getCount() + "\t" + "\t" + rob;
    }

    public Memory run(PrintStream debugOut, Integer divergenceLim, boolean quietStats){
        debugOut.println(ic);
//        rtired.push(new PipelineEntry(Utils.opFactory.new No(), 0, false));
        int retiredInstrCount = 0;
        List<Instruction> retiredInstrs = new ArrayList<Instruction>();
        pc.set(0);

        //AbstractMap<Instruction, Integer> inFlights = new HashMap<Instruction, Integer>();
        while((isPipelineBeingUsed() || !pc.isDone()) && (divergenceLim == null || tally < divergenceLim)){
            debugOut.println(pipelineToString());
            boolean flushFlag = false;
            Durate counter = new Durate(SUPERSCALAR_WIDTH);
            counter.rst();

            for(BranchUnit bru : brusInUse) bru.clk(); //just turning them off like this should be fine because we only ever put instructions into a unit if they ask for them themselves
            for(LoadStoreUnit lsu : lsusInUse) lsu.clk();
            for(ArithmeticLogicUnit alu : alusInUse) alu.clk();

            //debugOut.println(dec.physicalRegisters);

            int lastCountInDecIsu = -1;
            isu.clk();
            while(decIsu.canPull() && lastCountInDecIsu != decIsu.getCount() && !counter.isDone()){
                lastCountInDecIsu = decIsu.getCount();
                isu.clk();
                counter.decr();
            }
            counter.rst();

            while(exeWbu.canPull() && !flushFlag && !counter.isDone()){ //can only be up to eight in one cycle due to queue length
                wbu.clk();
                exeRss.update(); //update reservation groups! only once because the cdb doesnt change mid-cycle
                lsuRss.update();
                bruRss.update();

                rob.clk(); //read off the cdb
                if(rob.needsFlushing()) {
                    flushPipeline(rob.getShouldFlushWhere(), debugOut);
                    flushFlag = true; //exit this loop NOW
                }
                rob.doneFlushing();
                counter.decr();
                cdb.clear();
            }

            //though the rob is updated when we update the common data bus, it updates separately even when we cant writeback
            // it may do this for the remaining cycles in the counter
            // it may do this for as long as it has entries left
            // it may do this just if it did not flush beforehand (because otherwise there are no instructions)
            while(!counter.isDone() && !rob.isEmpty() && !flushFlag){
                rob.clk();
                if(rob.needsFlushing()) {
                    flushPipeline(rob.getShouldFlushWhere(), debugOut);
                    flushFlag = true; //exit this loop NOW
                }
                rob.doneFlushing();
                counter.decr();
            }

            counter.rst();

            dec.clk();
            while(fecDec.canPull() && decIsu.canPush() && !counter.isDone()){ //they all just shift a block along <=> they wont be able to do more than one pipeline buffer's worth!
                dec.clk();
                counter.decr();
            }
            counter.rst();

            fec.clk();
            while(fecDec.canPush() && !pc.isDone() && !counter.isDone()){ //they all just shift a block along <=> they wont be able to do more than one pipeline buffer's worth!
                prefec.push(new PipelineEntry(Utils.opFactory.new No(), pc.getCount(), false));
                fec.clk();
                counter.decr();
            }
            counter.rst();

            tally++;
//            while(rtired.canPull()) { //if we retire more that one instruction per cycle
//                retiredInstrs.add(rtired.pull().getOp());
//                retiredInstrCount++;
//            } //delete whats inside (voided is used to detect when writebacks are finished)
            delete.flush(FLUSH_ALL); // any instructions we want to throw away can be put into delete

            if(tally % 1000 == 0) debugOut.print("\r" + tally / 1000 + "K cycles");

            cdb.clear();
        }
        if(divergenceLim != null && tally >= divergenceLim) throw new RuntimeException("run: program considered to diverge after " + divergenceLim + " instrs");
        debugOut.println("registers (dirty): " + rf);
        debugOut.println("memory: " + mem);

        if(!quietStats){
            System.out.println("run: program finished in " + tally + " cycles");
            double ipc = Utils.toDecimalPlaces( (double) rob.getCommitted() / tally, DP_ACC);
            double time = (rob.getCommitted() * (1 / ipc) * ASSUMED_CYCLE_TIME) / Math.pow(10, 3);
            double percentMispredicts = (double) rob.getMispredicted() / rob.getCommitted();
            System.out.println("run: instructions per cycle " + ipc);
            System.out.println("run: cpu time " + Utils.toDecimalPlaces(time, DP_ACC) + "μs @ " + CLOCK_SPEED_MHZ + "MHz");
            System.out.println("run: percentage mispredicted " + Utils.toDecimalPlaces(percentMispredicts, DP_ACC) +"%");
            System.out.println(Arrays.toString(mem.getData()));
        }


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
