import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 */
public class Node implements Serializable {
    private static final int MAX_NODE_RECORDS = calculateMaxRecordsInNode();
    private static final int MIN_NODE_RECORDS = calculateMinRecordsInNode();

    private ArrayList<NodeRecord> nodeRecords;
    private int levelInRstarTree; // The height of the location of the Node
    private long nodeId;


    // Node constructor with height and nodeRecords as parameters
    Node(int nodeId , int levelInRstarTree, ArrayList<NodeRecord> nodeRecords) {
        this.nodeId = nodeId;
        this.levelInRstarTree = levelInRstarTree;
        this.nodeRecords = nodeRecords;
    }

    public ArrayList<NodeRecord> getNodeRecords(){return nodeRecords;}
    public int getLevelInRstarTree() {return levelInRstarTree;}
    public long getNodeId(){ return nodeId;}

    public static int getMaxNodeRecords() { return MAX_NODE_RECORDS; }
    public static int getMinNodeRecords() { return MIN_NODE_RECORDS; }

    // Calculates and return an integer which represents the maximum number of records a block of BLOCK_SIZE can have
    public static int calculateMaxRecordsInNode() {
        ArrayList<NodeRecord> node = new ArrayList<>();
        long blockSize = FilesHandler.getBlockSize();
        int totalRecordsInNode = 0;
        do {
            ArrayList<Bounds> boundsForEachDimension = new ArrayList<>();
            for (int d = 0; d < FilesHandler.getDataDimensions(); d++)
                boundsForEachDimension.add(new Bounds(0.0, 0.0));

            NodeRecord nodeRecord = new NodeRecord( boundsForEachDimension, new Random().nextLong(), new Random().nextLong()); //leaf node records hold more fields than regular noderecords
            node.add(nodeRecord);
            byte[] nodeToBytes = new byte[0];
            byte[] realNodeBytes = new byte[0];
            try {
                nodeToBytes = FilesHandler.serialize(new Node(new Random().nextInt(), new Random().nextInt(),node));
                realNodeBytes = FilesHandler.serialize(nodeToBytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (realNodeBytes.length + nodeToBytes.length > blockSize)
                break;
            totalRecordsInNode++;
        }while(true);
        return totalRecordsInNode;
    }

    public static int calculateMinRecordsInNode(){
        return  (int) (0.4 * MAX_NODE_RECORDS);
    }

}
