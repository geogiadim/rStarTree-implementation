import java.util.ArrayList;

public class RStarTree {


    public RStarTree(){
        root = new Node(1,1, null); // Create root node
        // Adding the data of datafile in the RStarTree (to the indexFile)
        for (int blockPointer = 1; blockPointer< FilesHandler.getTotalBlocksInDataFile(); blockPointer++) {
            ArrayList<Record> records = FilesHandler.readBlockInDataFile(blockPointer);
            if (records != null) {
                for (Record record : records)
                    insertData(record,blockPointer);
            }
            else
                throw new IllegalStateException("Could not read records properly from the datafile");
        }
    }

    private void insertData(Record blockRecord, int blockPointer ){
        // Filling bounds
        double[][] boundsForEachDimension = new double[FilesHandler.getDataDimensions()][2];
        for (int d = 0; d < FilesHandler.getDataDimensions(); d++) {
            for (int k = 0; k < 2; k++) {
                boundsForEachDimension[d][k] = blockRecord.getCoordinateForSingleDimension(d);
            }
        }

        NodeRecord nodeRecord = new NodeRecord(boundsForEachDimension, blockPointer,blockRecord.getRecordId()   );
        insert(nodeRecord, LEAF_HEIGHT);
    }
    private void insert(NodeRecord nodeRecord, int nodeHeight){
        //I1
        Node node = root; //CS1 Set N to be the root
        Node bestNode = chooseSubTree(nodeRecord, node);
        //I2
        if (bestNode!= null)
            bestNode.addRecordInNode(nodeRecord);
    }

    private Node chooseSubTree(NodeRecord newNodeRecord, Node node){

        NodeRecord bestNodeRecord;

        // CS2 If N is a leaf return N
        if (node.getLevelInRstarTree() == LEAF_HEIGHT )
            return node;
        // Choose the entry in the node whose rectangle needs least overlap enlargement to include the new data rectangle
        // Resolve ties by choosing the entry whose rectangle needs least area enlargement,
        // then the entry with the rectangle of smallest area
        else if (node.getLevelInRstarTree() == 2){
            ArrayList<NodeRecord> nodeRecords = node.getNodeRecords();

            bestNodeRecord = findLeastOverlapEnlargement(newNodeRecord, nodeRecords);

            //bestNodeRecord = Collections.min(node.getEntries(), new EntryComparator.EntryOverlapEnlargementComparator(node.getEntries(),boundingBoxToAdd,node.getEntries()));

        }
        else if (node.getLevelInRstarTree() > 2 && node.getLevelInRstarTree() < totalHeight){
            ArrayList<NodeRecord> nodeRecords = node.getNodeRecords();

            bestNodeRecord = findLeastAreaEnlargement(nodeRecords);

        }
        chooseSubTree( ,node);

        return null;
    }

    private void overflowTreatment(){

    }

    private NodeRecord findLeastOverlapEnlargement(NodeRecord newNodeRecord, ArrayList<NodeRecord> nodeRecords){
        newNodeRecord.getBoundsArray();
        for (NodeRecord nodeRecord: nodeRecords){
            nodeRecord.
        }



        NodeRecord bestRecord = nodeRecords.get(1);
        return bestRecord;
    }

    private NodeRecord findLeastAreaEnlargement(ArrayList<NodeRecord> nodeRecords){
        NodeRecord bestRecord = nodeRecords.get(1);
        return bestRecord;
    }

}
