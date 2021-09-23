import java.io.Serializable;
import java.util.ArrayList;

public class MinBoundingRectangle implements Serializable {
    // first column = lower bound, second column = higher bound
    // rows = dimensions , columns = 2
    private double[][] boundsArray;
    private double area;
    private double margin;
    private ArrayList<Double> center;

    MinBoundingRectangle (double[][] boundsArray){
        this.boundsArray= boundsArray;
        this.area = calculateMBRArea();
        this.margin = calculateMBRMargin();
        this.center = calculateMBRCenter();
    }

    double[][] getBoundsArray(){ return boundsArray; }

    private double calculateMBRArea(){
        double areaOfMBR = 1;
        for (int i = 0; i < FilesHandler.getDataDimensions(); i++){
                areaOfMBR *= boundsArray[i][1]-boundsArray[i][0];
        }
        return java.lang.Math.abs(areaOfMBR);
    }
    private double calculateMBRMargin(){
        double marginofMBR = 0;
        for (int i = 0; i < FilesHandler.getDataDimensions(); i++){
               marginofMBR +=  boundsArray[i][1] - boundsArray[i][0];
        }
        return marginofMBR;
    }

    private ArrayList<Double> calculateMBRCenter(){
        ArrayList<Double> centerOfMBR = new ArrayList<>();
        for (int i = 0; i < FilesHandler.getDataDimensions(); i++){
            centerOfMBR.add((boundsArray[i][1] + boundsArray[i][0]) / 2 );
        }
        return centerOfMBR;
    }
}
