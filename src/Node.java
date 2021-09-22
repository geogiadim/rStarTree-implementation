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

    public ArrayList<NodeRecord> getNodeRecords(){
        return nodeRecords;
    }

    public int getLevelInRstarTree() {
        return levelInRstarTree;
    }
}
