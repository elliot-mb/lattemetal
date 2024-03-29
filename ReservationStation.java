public class ReservationStation {
    private static int uId = 0;
    private int id;
    private Instruction op;
    private ReservationStation qJ, qK;
    private int vJ, vK;
    private boolean rJ, rK, busy;
    ReservationStation(){
        flush();
        this.id = uId;
        uId++;
    }

    public int getId(){
        return id;
    }

    public void flush(){
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
