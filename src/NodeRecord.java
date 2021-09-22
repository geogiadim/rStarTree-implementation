import java.io.Serializable;

/**
 *
 */
public class NodeRecord implements Serializable {
    private MinBoundingRectangle mbr ;
    private long childNodeId;
    private long blockIdPointer;

    // Node constructor
    NodeRecord(MinBoundingRectangle mbr, long childNodeId){
        this.mbr = mbr;
        this.childNodeId = childNodeId;
    }

    // Leaf constructor
//    NodeRecord(MinBoundingRectangle mbr, long blockIdPointer){
//        this.mbr = mbr;
//        this.blockIdPointer = blockIdPointer;
//    }

    public MinBoundingRectangle getMbr(){ return mbr; }
    public long getChildNodeId(){ return childNodeId; }
}
