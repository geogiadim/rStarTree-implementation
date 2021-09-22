import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represent a point in the k-dimensional space
 */
public class Record implements Serializable {
    private long recordId;
    private ArrayList<Double> recordsCoordinates;

    public Record(long recordId, ArrayList<Double> recordsCoordinates){
        this.recordId = recordId;
        this.recordsCoordinates = recordsCoordinates;
    }

    public long getRecordId() {
        return recordId;
    }

    public ArrayList<Double> getRecordsCoordinates() {
        return recordsCoordinates;
    }
}
