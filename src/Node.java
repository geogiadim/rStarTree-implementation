import java.util.ArrayList;

/**
 *
 */
public class Node {
    private static final int MAX_NODE_RECORDS = FilesHandler.calculateMaxRecordsInNode();
    private static final int MIN_NODE_RECORDS = FilesHandler.calculateMinRecordsInNode();

    private ArrayList<NodeRecord> nodeRecords;
    private int levelInRstarTree; // The height of the location of the Node


    // Node constructor with height and nodeRecords parameters
    Node(int nodeId , int levelInRstarTree, ArrayList<NodeRecord> nodeRecords) {
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
