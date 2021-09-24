import java.util.ArrayList;

/**
 * Implementation of R star Tree
 */
public class RStarTree {
    private int totalHeight = 1;
    private final int LEAF_HEIGHT = 1;
    private Node root;
    private boolean[] overflowCalledInLevel;
    private ArrayList<Node> listOfNodes = new ArrayList<>();

    public RStarTree(){
        ArrayList<NodeRecord> nodeRecords = new ArrayList<>();
        root = new Node(1,1, nodeRecords); // Create root node

        FilesHandler.writeIndexFileBlock(root);
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

    /**
     * Implementation of insertData Algorithm of R*-Tree
     *
     */
    private void insertData(Record blockRecord, int blockPointer ){
        // Filling bounds
        double[][] boundsForEachDimension = new double[FilesHandler.getDataDimensions()][2];
        for (int d = 0; d < FilesHandler.getDataDimensions(); d++) {
            for (int k = 0; k < 2; k++) {
                boundsForEachDimension[d][k] = blockRecord.getCoordinateForSingleDimension(d);
            }
        }
        // create a node record
        NodeRecord nodeRecord = new NodeRecord(boundsForEachDimension, blockPointer,blockRecord.getRecordId());
        // ID1 Invoke Insert starting with the leaf level as a parameter to insert a new data rectangle
        insert(nodeRecord, LEAF_HEIGHT);
    }

    /**
     * Implementation of insert Algorithm of R*-Tree
     */
    private void insert(NodeRecord nodeRecord, int nodeHeight){
        // I1 Invoke ChooseSubTree, with the level as a parameter
        // to find an appropriate node N, in which to place the new entry E
        Node node = root; //CS1 Set N to be the root
        Node bestNode = chooseSubTree(nodeRecord, node);
        //I2 If N has less than Max entries accommodate E in N
        // if N has Max entries invoke OverflowTreatment with the level of N as a parameter
        // [for reinsertion or split]

        if (bestNode != null){
            if (bestNode.getNodeRecords() != null) {
                if (bestNode.getNodeRecords().size() < Node.getMaxNodeRecords()) {
                    bestNode.addRecordInNode(nodeRecord);
                    FilesHandler.updateIndexFileBlock(bestNode);
                } else if (bestNode.getNodeRecords().size() == Node.getMaxNodeRecords()){
                    overflowTreatment(bestNode, nodeRecord);
                }
            }
        }
    }

    /**
     * Implementation of chooseSubTree Algorithm of R*-Tree
     */
    private Node chooseSubTree(NodeRecord newNodeRecord, Node node){
        // CS2 If N is a leaf return N
        if (node.getLevelInRstarTree() == LEAF_HEIGHT )
            return node;
        // Choose the entry in the node whose rectangle needs least overlap enlargement to include the new data rectangle
        // Resolve ties by choosing the entry whose rectangle needs least area enlargement,
        // then the entry with the rectangle of smallest area
        else if (node.getLevelInRstarTree() == 2){
            ArrayList<NodeRecord> nodeRecords = node.getNodeRecords();
            NodeRecord bestNodeRecord = findLeastOverlapEnlargement(newNodeRecord, nodeRecords);

            //CS3 Set N to be the child node pointed to by the
            // child pointer of the chosen entry and repeat from CS2
            Node childNode = FilesHandler.readNodeInIndexFile(bestNodeRecord.getChildNodeId());
            return chooseSubTree(newNodeRecord, childNode);

        }
        // If the child pointers in N do not point to leaves: [determine the minimum area cost],
        // choose the leaf in N whose rectangle needs least area enlargement to include the new data
        // rectangle. Resolve ties by choosing the leaf with the rectangle of smallest area
        else if (node.getLevelInRstarTree() > 2 && node.getLevelInRstarTree() < totalHeight){
            ArrayList<NodeRecord> nodeRecords = node.getNodeRecords();
            NodeRecord bestNodeRecord = findLeastAreaEnlargement(nodeRecords);

            //CS3 Set N to be the child node pointed to by the
            // child pointer of the chosen entry and repeat from CS2
            Node childNode = FilesHandler.readNodeInIndexFile(bestNodeRecord.getChildNodeId());
            return chooseSubTree(newNodeRecord, childNode);
        }
        return null;
    }

    /**
     * Finds least overlap enlargement if newNodeRecord is included in MBR (noderecords)
     */
    private NodeRecord findLeastOverlapEnlargement(NodeRecord newNodeRecord, ArrayList<NodeRecord> nodeRecords){
        // 1st column overlap-before ---- 2nd column overlap-after newNodeRecord
        double[][] overlapArray = new double[nodeRecords.size()][2];
        int counter = 0;
        // Calculating overlap for every MBR before adding newNodeRecord
        for (NodeRecord nodeRecord: nodeRecords){
            for(NodeRecord nodeRecord2: nodeRecords){
                if(nodeRecord != nodeRecord2)
                    overlapArray[counter][0] += MinBoundingRectangle.calculateOverlapValue(nodeRecord.getMbr(), nodeRecord2.getMbr());
            }
            counter++;
        }
        // Calculating overlap for every MBR after adding newNodeRecord
        for (NodeRecord nodeRecord: nodeRecords){
            ArrayList<NodeRecord> pointAndRectangle = new ArrayList<>();
            pointAndRectangle.add(nodeRecord);
            pointAndRectangle.add(newNodeRecord);
            MinBoundingRectangle newMBR = NodeRecord.calculateMBR(pointAndRectangle);

            for(NodeRecord nodeRecord2: nodeRecords){
                if(nodeRecord != nodeRecord2)
                    overlapArray[counter][1] += MinBoundingRectangle.calculateOverlapValue(newMBR, nodeRecord2.getMbr());
            }
        }
        // Finding the minimum difference
        int minIndex = 0;
        for (int i=1; i< nodeRecords.size(); i++){
            if (overlapArray[i][1] - overlapArray[i][0] < overlapArray[minIndex][1] - overlapArray[minIndex][0] ){
                minIndex = i;
            }
        }

        ArrayList<NodeRecord> equaloverlaprecords = new ArrayList<>(); //Array list with tied NodeRecords
        // Finding overlap enlargement ties
        for (int i=0; i< nodeRecords.size(); i++) {
            if (overlapArray[i][1] - overlapArray[i][0] == overlapArray[minIndex][1] - overlapArray[minIndex][0]) {
                equaloverlaprecords.add(nodeRecords.get(i));
            }
        }
        // and if there are ties then resolve equality calling findLeastAreaEnlargement function
        if ( equaloverlaprecords.size() > 1)
           return findLeastAreaEnlargement( newNodeRecord,equaloverlaprecords);

        return nodeRecords.get(minIndex);
    }

    /**
     * Finds least area enlargement if newNodeRecord is included in MBR (nodeRecords)
     */
    private NodeRecord findLeastAreaEnlargement(NodeRecord newNodeRecord, ArrayList<NodeRecord> nodeRecords){
        // 1st column area before adding newNodeRecord - 2nd column area after adding newNodeRecord
        double [][] compareEnlargement = new double[nodeRecords.size()][2];
        ArrayList<NodeRecord> temp = new ArrayList<>();
        temp.add(newNodeRecord);
        for (int i = 0; i < nodeRecords.size(); i++){
            temp.add(nodeRecords.get(i));
            compareEnlargement[i][0] = nodeRecords.get(i).getMbr().getArea();
            compareEnlargement[i][1] = NodeRecord.calculateMBR(temp).getArea();
            temp.remove(temp.size()-1);
        }

        // Finding the minimum difference
        int minIndex = 0;
        for (int i=1; i< nodeRecords.size(); i++){
            if(compareEnlargement[i][1] - compareEnlargement[i][0] < compareEnlargement[minIndex][1] - compareEnlargement[minIndex][0]){
                minIndex = i;
            }
        }
        ArrayList<NodeRecord> equalEnlargementRecords = new ArrayList<>();
        // Finding area enlargement ties
        for (int i=0; i< nodeRecords.size(); i++){
            if(compareEnlargement[i][1] - compareEnlargement[i][0] == compareEnlargement[minIndex][1] - compareEnlargement[minIndex][0]){
                equalEnlargementRecords.add(nodeRecords.get(i));
            }
        }
        if (equalEnlargementRecords.size() > 1){
            return findSmallestMBRArea(newNodeRecord, equalEnlargementRecords);
        }

        return nodeRecords.get(minIndex);
    }

}
