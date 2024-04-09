

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
    private final PhysicalRegFile prf;
    private int tally;

    private static final int ALU_RS_COUNT = 2;
    private static final int LSU_RS_COUNT = 2;

    private final List<ReservationStation> aluRs = new ArrayList<ReservationStation>();
    private final List<ReservationStation> lsuRs = new ArrayList<ReservationStation>();
    //lsu? load store buffers?

    private final PipelineRegister prefec = new PipelineRegister(1); //just to pass the pc value to the fetch unit, and increment it!
    private final PipelineRegister fecDec = new PipelineRegister(1);
    private final PipelineRegister deuIsu = new PipelineRegister(2); //instruction queue!!
    private final PipelineRegister isuAlu = new PipelineRegister(1);
    private final PipelineRegister isuLsu = new PipelineRegister(1);
    private final PipelineRegister aluBru = new PipelineRegister(1);
    private final PipelineRegister lsuBru = new PipelineRegister(1);
    private final PipelineRegister bruWbu = new PipelineRegister(1);
    //private final PipelineRegister aluLsu = new PipelineRegister();
    private final PipelineRegister rtired = new PipelineRegister(1); //ignored pipe register to satisfy Unit inheritence
    private final PipelineRegister delete = new PipelineRegister(100);
    private final int DP_ACC = 2;

    private final int ROB_ENTRIES = 8;

    Processor(InstructionCache ic, Memory... mem) throws RuntimeException{
        if(mem.length > 1) throw new RuntimeException("Processor: this constructor cannot have more than one memories");
        this.cdb = new HashMap<Integer, List<Integer>>();
        this.mem = mem.length > 0 ? mem[0] : new Memory();
        this.rf = new RegisterFile(cdb);
        this.rob = new ReorderBuffer(ROB_ENTRIES, cdb, rf, this.mem);
        for(int i = 0; i < ALU_RS_COUNT; i++){
            aluRs.add(new ReservationStation(cdb, rob));
        }
        for(int i = 0; i < LSU_RS_COUNT; i++){
            lsuRs.add(new ReservationStation(cdb, rob));
        }
        this.ic = ic;
        this.tally = 0;
        this.pc = new ProgramCounter(ic.numInstrs());
        this.prf = new PhysicalRegFile(cdb, rob);
        this.rob.setPrf(prf); //avoid circular dependency
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
                this.rf,
                this.rob,
                this.prf,
                this.aluRs,
                this.lsuRs,
                new PipelineRegister[]{deuIsu},
                new PipelineRegister[]{isuAlu, isuLsu});
        this.alu = new ArithmeticLogicUnit(
                this.cdb,
                this.aluRs,
                this.rf,
                this.prf,
                new PipelineRegister[]{isuAlu},
                new PipelineRegister[]{aluBru});
        this.lsu = new LoadStoreUnit(
                this.mem,
                this.lsuRs,
                this.rf,
                this.prf,
                this.cdb,
                this.rob,
                new PipelineRegister[]{isuLsu},
                new PipelineRegister[]{lsuBru});
        this.bru = new BranchUnit(
                this.pc,
                new PipelineRegister[]{aluBru, lsuBru},
                new PipelineRegister[]{bruWbu}
        );
        this.wbu = new WriteBackUnit(
                this.rf,
                this.rob,
                this.prf,
                this.cdb,
                new PipelineRegister[]{bruWbu},
                new PipelineRegister[]{rtired});
    }

//    private void sendSingleInstruction(){
//        //System.out.println("push");
//        if(prefec.canPull()) prefec.pull(); //empty register
//        prefec.push(Utils.opFactory.new No());
//        prefec.setPcVal(pc.getCount());
//    }


    private boolean isPipelineBeingUsed(){
        return prefec.canPull() || fecDec.canPull() || deuIsu.canPull() || isuAlu.canPull() || isuLsu.canPull() ||
                bruWbu.canPull() || aluBru.canPull() || rtired.canPull() || !wbu.isDone() || !lsu.isDone() ||
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
        rtired.flush();
    }

    public Memory run(PrintStream debugOut){
        debugOut.println(ic);
        rtired.push(new PipelineEntry(Utils.opFactory.new No(), 0, false));
        int retiredInstrCount = 0;
        List<Instruction> retiredInstrs = new ArrayList<Instruction>();

        //AbstractMap<Instruction, Integer> inFlights = new HashMap<Instruction, Integer>();
        while(isPipelineBeingUsed() || !pc.isDone()){
            debugOut.println("\t[" + prefec + feu + " " + fecDec + " " + deu + " " + deuIsu+ " " + isu + " " + "(" + isuAlu + ","
                    + isuLsu + ") (" + alu + ", " + lsu + ") (" + aluBru + "," + lsuBru + ") " + bru + " " + bruWbu + " "
                    + wbu + "]\t@" + tally + "\tpc " + pc.getCount() + "\t" + retiredInstrCount + "\t" + rob);
            wbu.clk();
            bru.clk();
            if(bru.needsFlushing()) flushPipeline();
            lsu.clk();
            alu.clk();
            isu.clk();
            deu.clk();
            feu.clk();
            if(prefec.canPush() && !pc.isDone()){//&& !(!voided.canPull() && fe.getIsBranch())) {
                prefec.push(new PipelineEntry(Utils.opFactory.new No(), pc.getCount(), false));
                //pc.incr();
            }
            tally++;
            while(rtired.canPull()) { //if we retire more that one instruction per cycle
                retiredInstrs.add(rtired.pull().getOp());
                retiredInstrCount++;
            } //delete whats inside (voided is used to detect when writebacks are finished)
            delete.flush(); // any instructions we want to throw away can be put into delete

            if(tally % 1000 == 0) debugOut.print("\r" + tally / 1000 + "K cycles");
            System.out.println(cdb.keySet().toString() + cdb.values().toString());
            //cdb.clear(); instead of this, do .remove whenever an instruction commits from the ROB
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
