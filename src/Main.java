import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        userInput();
        printMetaData();
        printSpecificBLock(1);
        RStarTree tree = new RStarTree();

    }
    private static void userInput() throws FileNotFoundException {
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
    }

    private static void printMetaData(){
        ArrayList<Integer> metadata;
        metadata = FilesHandler.readMetaDataBlock();

        if (metadata != null){
            int i = 0;
            System.out.println("Metadata that are stored in first block of the .dat file: ");
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

    private static void printSpecificBLock(int blockId){
        ArrayList<Record> data;
        data = FilesHandler.readBlockInDataFile(blockId);


        if (data!= null){
            for (Record record: data){
                System.out.println(record.getSlotId() + " , " +record.getRecordsCoordinates());
            }
        }else {
            System.out.println("null records");
        }
    }
}
