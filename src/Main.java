import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        userInput();
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
        FilesHandler.readBlockInDataFile(95);
    }
}
