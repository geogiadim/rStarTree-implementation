import java.io.Serializable;
import java.util.ArrayList;

public class MinBoundingRectangle implements Serializable {
    //private double upperLimit, lowerLimit;
    private ArrayList<Bounds> boundsArray;

    MinBoundingRectangle (ArrayList<Bounds> boundsArray){
        this.boundsArray= boundsArray;
    }


}
