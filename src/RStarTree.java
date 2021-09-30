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
        overflowCalledInLevel = new boolean[totalHeight];
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
                    FilesHandler.updateIndexFileBlock(bestNode, false);
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
            NodeRecord bestNodeRecord = findLeastAreaEnlargement(newNodeRecord, nodeRecords);

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

    /**
     * Finds smallest area if newNodeRecord is included in MBR (nodeRecords)
     */
    private NodeRecord findSmallestMBRArea(NodeRecord newNodeRecord, ArrayList<NodeRecord> nodeRecords){
        ArrayList<NodeRecord> temp = new ArrayList<>();
        temp.add(newNodeRecord);
        //double minMBRarea = NodeRecord.calculateMBR().getArea();
        NodeRecord minNodeRecord = nodeRecords.get(0);
        double minMBRarea = Double.MAX_VALUE;
        for (int i = 0; i < nodeRecords.size(); i++){
            temp.add(nodeRecords.get(i));
            if (NodeRecord.calculateMBR(temp).getArea() < minMBRarea){
                minMBRarea = NodeRecord.calculateMBR(temp).getArea();
                minNodeRecord = nodeRecords.get(i);
            }
            temp.remove(temp.size() - 1);
        }
        return minNodeRecord;
    }

    /**
     * OverflowTreatment algorithm of R star tree
     */
    private void overflowTreatment(Node bestNode, NodeRecord newNodeRecord){
        int nodeLevel = bestNode.getLevelInRstarTree();
        if (nodeLevel != this.root.getLevelInRstarTree() && !overflowCalledInLevel[nodeLevel - 1]){
            overflowCalledInLevel[nodeLevel - 1] = true;
            reInsert();
        }
        else{
            ArrayList<NodeRecord> splitNodeRecords = bestNode.getNodeRecords();
            splitNodeRecords.add(newNodeRecord);
            ArrayList<Node> newNodes = split(splitNodeRecords);
            bestNode = newNodes.get(0);
            Node splitNode = newNodes.get(1);
            // if root split occurred
            if (bestNode.getNodeId() == 1){
                FilesHandler.writeIndexFileBlock(bestNode);
                FilesHandler.writeIndexFileBlock(splitNode);

                ArrayList<NodeRecord> newRootRecords = new ArrayList<>();
                newRootRecords.add(new NodeRecord(bestNode.getNodeRecords(),bestNode.getNodeId()));
                newRootRecords.add(new NodeRecord(splitNode.getNodeRecords(),splitNode.getNodeId()));
                Node newRoot = new Node(1,++totalHeight,newRootRecords);
                FilesHandler.updateIndexFileBlock(newRoot,true);
            }

        }
    }

    /**
     *  Reinsert algorithm of R star tree
     */
    private void reInsert(){

    }

    /**
     *  Split algorithm of R star tree
     */
    private ArrayList<Node> split(ArrayList<NodeRecord> nodeRecords){
        // S1 Invoke chooseSplitAxis to determine the axis, perpendicular to which the split is performed
        ArrayList<Distribution> splitAxis = chooseSplitAxis(nodeRecords);
        // S2 Invoke chooseSplitIndex to determine the best distribution into two groups along that axis
        return chooseSplitIndex(splitAxis);
    }

    /**
     *  ChooseSplitAxis algorithm of R star tree
     */
    private ArrayList<Distribution> chooseSplitAxis(ArrayList<NodeRecord> nodeRecords){
        double splitAxisMarginsSum = Double.MAX_VALUE;
        ArrayList<Distribution> splitAxisDistributions = new ArrayList<>();
        for (int i = 0; i < FilesHandler.getDataDimensions(); i++) {
            ArrayList<NodeRecord> nodeRecordsByUpper = new ArrayList<>();
            ArrayList<NodeRecord> nodeRecordsByLower = new ArrayList<>();

            for (NodeRecord nodeRecord: nodeRecords){
                nodeRecordsByLower.add(nodeRecord);
                nodeRecordsByUpper.add(nodeRecord);
            }

            nodeRecordsByLower = sortRecords(nodeRecordsByLower, true, i);
            nodeRecordsByUpper = sortRecords(nodeRecordsByUpper, false, i);

            ArrayList<ArrayList<NodeRecord>> bothSortedLists = new ArrayList<>();
            bothSortedLists.add(nodeRecordsByLower);
            bothSortedLists.add(nodeRecordsByUpper);

            double sumOfMBRMargins = 0;
            ArrayList<Distribution>  totalDistros = new ArrayList<>();

            //Total number of different distributions = M-2*m+2 for each sorted arraylist
            int numOfDistros = Node.getMaxNodeRecords() - 2 * Node.getMinNodeRecords() + 2 ;

            for (ArrayList<NodeRecord> sortedList : bothSortedLists){
                for (int k=1; k<= numOfDistros; k++){
                    ArrayList<NodeRecord> firstGroup = new ArrayList<>();
                    ArrayList<NodeRecord> secondGroup = new ArrayList<>();
                    // The first group includes the first (m-1)+k entries, the second group includes remaining entries
                    for (int j = 0; j < (Node.getMinNodeRecords() -1)+k; j++)
                        firstGroup.add(sortedList.get(j));
                    for (int j = (Node.getMinNodeRecords() -1)+k; j < nodeRecords.size(); j++)
                        secondGroup.add(sortedList.get(j));

                    MinBoundingRectangle mbrGroupA = NodeRecord.calculateMBR(firstGroup);
                    MinBoundingRectangle mbrGroupB = NodeRecord.calculateMBR(secondGroup);

                    sumOfMBRMargins += mbrGroupA.getMargin() + mbrGroupB.getMargin();

                    DistroGroup distroGroupA = new DistroGroup(firstGroup, mbrGroupA);
                    DistroGroup distroGroupB = new DistroGroup(secondGroup, mbrGroupB);
                    Distribution distribution = new Distribution(distroGroupA, distroGroupB);
                    totalDistros.add(distribution);
                }
                // Choose the axis with the minimum sum as split axis
                if (splitAxisMarginsSum > sumOfMBRMargins) {
                    splitAxisMarginsSum = sumOfMBRMargins;
                    splitAxisDistributions = totalDistros;
                }
            }
        }
        return splitAxisDistributions;
    }

    /**
     * Sort by lower or by upper nodeRecords
     */
    private ArrayList<NodeRecord> sortRecords(ArrayList<NodeRecord> nodeRecords,boolean isByLower, int dim) {
        if (isByLower){
            for(int i=0; i < nodeRecords.size(); i++){
                for (int j = 1; j < (nodeRecords.size() - i); j++) {
                    if(nodeRecords.get(j).getMbr().getBoundsArray()[dim][0] < nodeRecords.get(j-1).getMbr().getBoundsArray()[dim][0]){
                        NodeRecord temp = nodeRecords.get(j-1);
                        nodeRecords.set(j-1, nodeRecords.get(j));
                        nodeRecords.set(j, temp);
                    }
                }
            }
        }
        else{
            for(int i=0; i < nodeRecords.size(); i++){
                for (int j = 1; j  < (nodeRecords.size() -i ); j++) {
                    if(nodeRecords.get(i).getMbr().getBoundsArray()[dim][1] > nodeRecords.get(j).getMbr().getBoundsArray()[dim][1]){
                        NodeRecord temp = nodeRecords.get(j-1);
                        nodeRecords.set(j-1, nodeRecords.get(j));
                        nodeRecords.set(j, temp);
                    }
                }
            }
        }
        return nodeRecords;
    }


    /**
     * returns the two nodes that created by split of parent node
     */
    private ArrayList<Node> chooseSplitIndex(ArrayList<Distribution> splitAxisDistros){
        double minOverlapValue = Double.MAX_VALUE;
        double minAreaValue = Double.MAX_VALUE;

        int bestIndex = 0;

        // choose the distro with min overlap value
        for (int i =0 ; i < splitAxisDistros.size(); i++){
            DistroGroup distroGroupA = splitAxisDistros.get(i).getFirstGroup();
            DistroGroup distroGroupB = splitAxisDistros.get(i).getSecondGroup();

            double mbrOverlap = MinBoundingRectangle.calculateOverlapValue(distroGroupA.getMBR(), distroGroupB.getMBR());

            if (mbrOverlap < minOverlapValue){
                minOverlapValue = mbrOverlap;
                minAreaValue = distroGroupA.getMBR().getArea() + distroGroupB.getMBR().getArea();
                bestIndex = i;
            }
            // resolve ties by minimum area value
            else if (mbrOverlap == minOverlapValue){
                double mbrArea = distroGroupA.getMBR().getArea() + distroGroupB.getMBR().getArea();
                if (mbrArea < minAreaValue){
                    minAreaValue = mbrArea;
                    bestIndex = i;
                }
            }
        }

        ArrayList<Node> splitNodes = new ArrayList<>();
        DistroGroup distroGroupA = splitAxisDistros.get(bestIndex).getFirstGroup();
        DistroGroup distroGroupB = splitAxisDistros.get(bestIndex).getSecondGroup();
        ArrayList<NodeRecord> firsGroupRecords = distroGroupA.getNodeRecords();
        ArrayList<NodeRecord> secondGroupRecords = distroGroupB.getNodeRecords();
        splitNodes.add(new Node(FilesHandler.getTotalNodesInIndexFile(), totalHeight, firsGroupRecords));
        splitNodes.add(new Node(FilesHandler.getTotalNodesInIndexFile()+1, totalHeight, secondGroupRecords));

        return splitNodes;
    }
}
