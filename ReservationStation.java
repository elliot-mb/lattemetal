public class ReservationStation {
    private Instruction op;
    private ReservationStation qJ, qK;
    private int vJ, vK;
    private boolean rJ, rK, busy;
    ReservationStation(){
        this.busy = false;
        this.op = null;
        this.qJ = null;
        this.qK = null;
        this.vJ = 0;
        this.vK = 0;
        this.rJ = false;
        this.rK = false;
    }

}
