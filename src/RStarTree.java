import java.util.ArrayList;

public class RStarTree {
    private int totalBlocksInDataFile = 2;

    public RStarTree(){
        ArrayList<Double> recCoordinates = new ArrayList<>();
        recCoordinates.add(-100.0);
        recCoordinates.add(1.0);
        insertRecord(new Record(1, recCoordinates),2);
    }

    private void insertRecord(Record record, int blockId){

    }

    private void chooseSubTree(Node node, Record record){

    }

}
