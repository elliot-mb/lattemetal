public class ReservationStation {
    public static int uId = 0;
    public int id;
    public Instruction op;
    public ReservationStation qJ, qK;
    public int vJ, vK;
    public boolean rJ, rK, busy;
    ReservationStation(){
        flush();
        this.id = uId;
        uId++;
    }

    public int getId(){
        return id;
    }

    public boolean isBusy(){
        return busy;
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
