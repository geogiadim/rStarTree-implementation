import java.util.ArrayList;

/**
 * Class that holds the nodeRecords and theirs MBR's for chooseSplitIndex algorithm
 */
public class DistroGroup {
    private ArrayList<NodeRecord> nodeRecords;
    private MinBoundingRectangle mbr;

    DistroGroup(ArrayList<NodeRecord> nodeRecords, MinBoundingRectangle mbr) {
        this.nodeRecords = nodeRecords;
        this.mbr = mbr;
    }

    ArrayList<NodeRecord> getNodeRecords() {
        return nodeRecords;
    }

    MinBoundingRectangle getMBR() {
        return mbr;
    }
}
