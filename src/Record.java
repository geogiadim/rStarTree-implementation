import java.util.ArrayList;

public class Record {
    private int slotId;
    private ArrayList<Double> recordsCoordinates;

    public Record(int slotId, ArrayList<Double> recordsCoordinates){
        this.slotId = slotId;
        this.recordsCoordinates = recordsCoordinates;
    }

    public int getSlotId() {
        return slotId;
    }

    public ArrayList<Double> getRecordsCoordinates() {
        return recordsCoordinates;
    }
}
