import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class NodeRecord implements Serializable {
    private MinBoundingRectangle mbr ;
    private long childNodeId;
    // for leaf node record
    private ArrayList<Bounds> boundsArray;
    private long blockIdPointer;
    private long recordIdPointer;

    // Internal Node record constructor
    NodeRecord(MinBoundingRectangle mbr, long childNodeId){
        this.mbr = mbr;
        this.childNodeId = childNodeId;
    }

    // Leaf record constructor
    NodeRecord(ArrayList<Bounds> boundsArray, long blockIdPointer, long recordIdPointer){
        this.boundsArray = boundsArray;
        this.blockIdPointer = blockIdPointer;
        this.recordIdPointer = recordIdPointer;
    }

    public MinBoundingRectangle getMbr(){ return mbr; }
    public long getChildNodeId(){ return childNodeId; }
}
