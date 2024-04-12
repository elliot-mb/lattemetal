import java.util.ArrayList;
import java.util.List;

public class ReservationGroup { //to set up grouped reservation stations!

    private final List<ReservationStation> rss;

    ReservationGroup(int size){
        this.rss = new ArrayList<ReservationStation>(size);
        for(int i = 0; i < size; i++){
            //rss.add(new ReservationStation());
        }
    }

}
