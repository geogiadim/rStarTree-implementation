import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main Class off the program
 * initialize all processes
 * Handles the interaction with the user
 */
public class Main {
    public static void main(String[] args) {
        askDimensionsInput();
        // testing
        printMetaData();
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        //-------------------------------------printSpecificBLock(2);
        // end testing

        RStarTree tree = new RStarTree();
        printIndexMetaData();
        System.out.println("------------------------------------");
        System.out.println("------------------------------------");
        printSpecificNode(1);
        System.out.println("------------------------------------");
        printSpecificNode(2);
        System.out.println("------------------------------------");
        printSpecificNode(3);
    }

    /**
     * ask user to give dimensions of data and initialize data file
     */
    private static void askDimensionsInput() {
        Scanner scan = new Scanner(System.in);
        int dimensions;
        do{
            System.out.println("Dimensions must be equal to number of coordinates in your csv file.");
            System.out.println("Give data dimensions: ");
            dimensions = scan.nextInt();
            if (dimensions <= 0 )
                System.out.println("Wrong input! Try again: ");
         }while(dimensions <= 0);

        FilesHandler.initializeDataFile(dimensions);
        FilesHandler.initializeIndexFile();
    }

    /**
     * print meta data of data file
     */
    private static void printMetaData(){
        ArrayList<Integer> metadata;
        metadata = FilesHandler.readMetaDataBlock(FilesHandler.getPathToDataFile());

        if (metadata != null){
            int i = 0;
            System.out.println("Metadata that are stored in first block of the data.dat file: ");
            for (Integer element : metadata ) {
                if (i == 0)
                    System.out.println("Data dimensions: " + element);
                else if (i == 1)
                    System.out.println("Size of each block in data file: " + element);
                else if (i == 2)
                    System.out.println("Total blocks inside data file: " + element);
                else if (i == 3)
                    System.out.println("Max num of records that fit in a single block: " + element);
                i++;
            }
        }else {
            System.out.println("null meta data");
        }
    }

    /**
     * Print all records that includes a specific block
     */
    private static void printSpecificBLock(int blockId){
        ArrayList<Record> data;
        data = FilesHandler.readBlockInDataFile(blockId);

        if (data!= null){
            for (Record record: data){
                System.out.println(record.getRecordId() + " , " +record.getRecordsCoordinates());
            }
        }else {
            System.out.println("null records");
        }
    }

    private static void printSpecificNode(int blockId){
        Node node;
        node = FilesHandler.readNodeInIndexFile(blockId);

        if (node!= null){
            for (NodeRecord nodeRecord: node.getNodeRecords()){
                System.out.println(nodeRecord.getChildNodeId() + " , " +nodeRecord.getRecordIdPointer() );
            }
        }else {
            System.out.println("null records");
        }
    }

    private static void printIndexMetaData (){
        ArrayList<Integer> metadata;
        metadata = FilesHandler.readMetaDataBlock(FilesHandler.getPathToIndexFile());

        if (metadata != null){
            int i = 0;
            System.out.println("Metadata that are stored in first block of the index.dat file: ");
            for (Integer element : metadata ) {
                if (i == 0)
                    System.out.println("Data dimensions: " + element);
                else if (i == 1)
                    System.out.println("Size of each node in index file: " + element);
                else if (i == 2)
                    System.out.println("Total nodes inside index file: " + element);
                else if (i ==3)
                    System.out.println("Total height of R star tree: "+ element);
                else if (i == 4)
                    System.out.println("Max num of node records that fit in a single node: " + element);
                else if (i == 5)
                    System.out.println("Min num of node records that has a single node: " + element);

                i++;
            }
        }else {
            System.out.println("null meta data");
        }
    }
}
