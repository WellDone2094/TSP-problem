import java.util.ArrayList;

/**
 * Created by WellDone2044 on 17/09/15.
 */
public class DistanceMatrix {

    private int matrix[][];

    public DistanceMatrix(ArrayList<Point> arr){
        this.matrix = new int[arr.size()][arr.size()];

        for(int i=0; i<arr.size(); i++){
            matrix[i][i] = 0;
            for(int j=i+1; j<arr.size(); j++){
                matrix[i][j] = pointDistance(arr.get(i), arr.get(j));
                matrix[j][i] = pointDistance(arr.get(i), arr.get(j));
            }
        }
    }

    public static int pointDistance(Point p1, Point p2){
        double x2 = Math.pow((p1.getX()-p2.getX()),2);
        double y2 = Math.pow((p1.getY()-p2.getY()),2);
        return (int) Math.round(Math.sqrt(x2+y2));
    }

    public int getDistance(int p1, int p2){
        return matrix[p1][p2];
    }
}
