import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        userInput();
        printMetaData();
        printSpecificBLock(34);
        RStarTree tree = new RStarTree();

    }
    private static void userInput() throws FileNotFoundException {
        Scanner scan = new Scanner(System.in);
        int dimensions;
        do{
            System.out.println("Input dimensions (must be higher than zero): ");
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
            for (Integer element : metadata ) {
                System.out.println("meta data element: " + element);
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



//    private static void sth(){
//        try {
//            byte[] blockToBytes = new byte[32 * 1024];
//
//            FileOutputStream fos = new FileOutputStream("./files/small-datafile.dat",true);
//            BufferedOutputStream bout = new BufferedOutputStream(fos);
//            bout.write(blockToBytes);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
