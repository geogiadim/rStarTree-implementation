import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 */
public class NodeRecord implements Serializable {
    private MinBoundingRectangle mbr;
    private long childNodeId;
    // for leaf node record
    //private double[][] boundsArray;
    private boolean isLeafRecord;
    private long blockIdPointer;
    private long recordIdPointer;

    // Internal Node record constructor
    NodeRecord(ArrayList<NodeRecord> nodeRecords , long childNodeId){
        //this.mbr = mbr;
        this.mbr = calculateMBR(nodeRecords);
        this.childNodeId = childNodeId;
        isLeafRecord = false;
    }

    // Leaf record constructor
    NodeRecord(double[][] boundsArray, long blockIdPointer, long recordIdPointer){
        //this.boundsArray = boundsArray;
        this.mbr = new MinBoundingRectangle(boundsArray);
        this.blockIdPointer = blockIdPointer;
        this.recordIdPointer = recordIdPointer;
        isLeafRecord = true;
    }

    public MinBoundingRectangle getMbr(){ return mbr; }
    public long getChildNodeId(){ return childNodeId; }
    public boolean getIsLeafRecord(){ return isLeafRecord; }
    public long getBlockIdPointer(){ return blockIdPointer;}
    public long getRecordIdPointer(){ return recordIdPointer; }

    /**
     * Calculate MBR given either leaf node records or internal node records
     */
    static MinBoundingRectangle calculateMBR(ArrayList<NodeRecord> nodeRecords){
        double[][] bounds = new double[FilesHandler.getDataDimensions()][2];
        for (int i = 0; i < FilesHandler.getDataDimensions(); i++){
            double max = Double.MIN_VALUE;
            double min = Double.MAX_VALUE;
            for (NodeRecord rec : nodeRecords){
                if (rec.getMbr().getBoundsArray()[i][0] < min){
                    min = rec.getMbr().getBoundsArray()[i][0];
                }
                if (rec.getMbr().getBoundsArray()[i][1] > max){
                    max = rec.getMbr().getBoundsArray()[i][1];
                }
            }
            bounds[i][0] = min;
            bounds[i][1] = max;
        }
        return new MinBoundingRectangle(bounds);
    }

}
