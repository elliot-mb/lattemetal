

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Processor {
    private final Map<Integer, List<Integer>> cdb;
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
    private final ReorderBuffer rob;
    private final RegisterAliasTable rat;
    private int tally;

    private static final int ALU_RS_COUNT = 2;
    private static final int LSU_RS_COUNT = 2;
    private static final int BRU_RS_COUNT = 2;
    private static final int DP_ACC = 2;
    private static final int ROB_INTIATES_FLUSH = -1;
    private static final int ROB_ENTRIES = 8;

    public static final int FLUSH_ALL = -1;

//    private final List<ReservationStation> aluRs = new ArrayList<ReservationStation>();
//    private final List<ReservationStation> lsuRs = new ArrayList<ReservationStation>();
//    //lsu? load store buffers?
    private final ReservationGroup execRss;
    private final ReservationGroup lsuRss;
    private final ReservationGroup bruRss;

    private final PipelineRegister prefec = new PipelineRegister(1); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister(1);
    private final PipelineRegister deuIsu = new PipelineRegister(1); //instruction queue!!
    private final PipelineRegister aluWbu = new PipelineRegister(1);
    private final PipelineRegister lsuWbu = new PipelineRegister(1);
    private final PipelineRegister bruWbu = new PipelineRegister(1);
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

        this.execRss = new ReservationGroup(ALU_RS_COUNT, cdb, rob, rf, rat);
        this.lsuRss = new ReservationGroup(LSU_RS_COUNT, cdb, rob, rf, rat);
        this.bruRss = new ReservationGroup(BRU_RS_COUNT, cdb, rob, rf, rat);

        this.feu = new FetchUnit(
                this.ic,
                this.pc,
                new PipeLike[]{prefec},
                new PipeLike[]{fecDec});
        this.deu = new DecodeUnit(
                this.rf,
                new PipeLike[]{fecDec},
                new PipeLike[]{deuIsu}); //loadstores go down the latter pipe
        this.isu = new IssueUnit(
                this.rf,
                this.rob,
                this.rat,
                new PipeLike[]{deuIsu},
                new PipeLike[]{execRss, lsuRss, bruRss});
        this.alu = new ArithmeticLogicUnit(
                this.cdb,
                this.rob,
                this.rf,
                this.rat,
                new PipeLike[]{execRss},
                new PipeLike[]{aluWbu});
        this.lsu = new LoadStoreUnit(
                this.mem,
                this.rf,
                this.rat,
                this.cdb,
                this.rob,
                new PipeLike[]{lsuRss},
                new PipeLike[]{lsuWbu});
        this.bru = new BranchUnit(
                this.pc,
                new PipeLike[]{bruRss},
                new PipeLike[]{bruWbu}
        );
        this.wbu = new WriteBackUnit(
                this.rf,
                this.rob,
                this.rat,
                this.cdb,
                new PipeLike[]{aluWbu, lsuWbu, bruWbu},
                new PipeLike[]{delete});
    }

//    private void sendSingleInstruction(){
//        //System.out.println("push");
//        if(prefec.canPull()) prefec.pull(); //empty register
//        prefec.push(Utils.opFactory.new No());
//        prefec.setPcVal(pc.getCount());
//    }


    private boolean isPipelineBeingUsed(){
        return prefec.canPull() || fecDec.canPull() || deuIsu.canPull() ||
                bruWbu.canPull() || aluWbu.canPull() || rtired.canPull() || !wbu.isDone() || !lsu.isDone() ||
                lsuWbu.canPull() || !alu.isDone() || !deu.isDone() || !feu.isDone() || !isu.isDone() || !rob.isEmpty();
    }

    private void flushPipeline(int branchIdInRob){
        System.out.println("flush from robEntry " + branchIdInRob);
        feu.flush(branchIdInRob);
        deu.flush(branchIdInRob);
        isu.flush(branchIdInRob);
        alu.flush(branchIdInRob);
        lsu.flush(branchIdInRob);
        wbu.flush(branchIdInRob);
        bru.flush(branchIdInRob);
        prefec.flush(branchIdInRob);
        fecDec.flush(branchIdInRob);
        deuIsu.flush(branchIdInRob);
        aluWbu.flush(branchIdInRob);
        lsuWbu.flush(branchIdInRob);
        bruWbu.flush(branchIdInRob);
        rtired.flush(branchIdInRob);
        execRss.flush(branchIdInRob);
        lsuRss.flush(branchIdInRob);
        bruRss.flush(branchIdInRob);
        rat.flushFrom(branchIdInRob);
        if(branchIdInRob != ROB_INTIATES_FLUSH) rob.flushFrom(branchIdInRob);
    }

    private String pipelineToString(){
        return "\t[" + prefec +
                feu + " " + fecDec + " " +
                deu + " " + deuIsu+ " " +
                isu + " " + "(" + execRss + "," + lsuRss + "," + bruRss + ") ("
                + alu + ", " + lsu + ", " + bru + ") (" + aluWbu + "," + lsuWbu + "," + bruWbu + ") "
                + wbu + "]\t@"
                + tally + "\tpc " + pc.getCount() + "\t" + "\t" + rob;
    }

    public Memory run(PrintStream debugOut){
        debugOut.println(ic);
//        rtired.push(new PipelineEntry(Utils.opFactory.new No(), 0, false));
        int retiredInstrCount = 0;
        List<Instruction> retiredInstrs = new ArrayList<Instruction>();
        pc.set(0);

        //AbstractMap<Instruction, Integer> inFlights = new HashMap<Instruction, Integer>();
        while(isPipelineBeingUsed() || !pc.isDone()){
            debugOut.println(pipelineToString());
            wbu.clk();
            bru.clk();
            if(bru.needsFlushing()) flushPipeline(bru.whereFlushAt());
            bru.doneFlushing();
            lsu.clk();
            alu.clk();
            isu.clk();
            deu.clk();
            feu.clk();
            //update these once the cdb has been fully utilised!
            execRss.update(); //update reservation groups!
            lsuRss.update();
            bruRss.update();

            rob.clk(); //read off the cdb
            if(rob.needsFlushing()) flushPipeline(ROB_INTIATES_FLUSH);
            rob.doneFlushing();

            if (prefec.canPush() && !pc.isDone()) {//&& !(!voided.canPull() && fe.getIsBranch())) {
                prefec.push(new PipelineEntry(Utils.opFactory.new No(), pc.getCount(), false));
                //pc.incr();
            }

            tally++;
//            while(rtired.canPull()) { //if we retire more that one instruction per cycle
//                retiredInstrs.add(rtired.pull().getOp());
//                retiredInstrCount++;
//            } //delete whats inside (voided is used to detect when writebacks are finished)
            delete.flush(FLUSH_ALL); // any instructions we want to throw away can be put into delete

            if(tally % 1000 == 0) debugOut.print("\r" + tally / 1000 + "K cycles");
            System.out.println(cdb.keySet().toString() + cdb.values().toString());
            cdb.clear();
        }
        debugOut.println("registers (dirty): " + rf);
        debugOut.println("memory: " + mem);
        debugOut.println("run: program finished in " + tally + " cycles");
        debugOut.println("run: instructions per cycle " + Utils.toDecimalPlaces((float) rob.getCommitted() / tally, DP_ACC));
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
