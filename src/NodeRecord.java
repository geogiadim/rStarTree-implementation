import java.io.Serializable;

/**
 *
 */
public class NodeRecord implements Serializable {
    private MinBoundingRectangle mbr ;
    private long childNodeId;
    // for leaf node record
    private double[][] boundsArray;
    private long blockIdPointer;
    private long recordIdPointer;

    // Internal Node record constructor
    NodeRecord(MinBoundingRectangle mbr, long childNodeId){
        this.mbr = mbr;
        this.childNodeId = childNodeId;
    }

    // Leaf record constructor
    NodeRecord(double[][] boundsArray, long blockIdPointer, long recordIdPointer){
        this.boundsArray = boundsArray;
        this.blockIdPointer = blockIdPointer;
        this.recordIdPointer = recordIdPointer;
    }

    public MinBoundingRectangle getMbr(){ return mbr; }
    public long getChildNodeId(){ return childNodeId; }
    public double[][] getBoundsArray(){ return boundsArray; }
    public long getBlockIdPointer(){ return blockIdPointer;}
    public long getRecordIdPointer(){ return recordIdPointer; }
}
