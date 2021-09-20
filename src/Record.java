import java.io.Serializable;
import java.util.ArrayList;

public class Record implements Serializable {
    private long slotId;
    private ArrayList<Double> recordsCoordinates;

    public Record(long slotId, ArrayList<Double> recordsCoordinates){
        this.slotId = slotId;
        this.recordsCoordinates = recordsCoordinates;
    }

    public long getSlotId() {
        return slotId;
    }

    public ArrayList<Double> getRecordsCoordinates() {
        return recordsCoordinates;
    }
}
