import java.util.ArrayList;

/**
 * Created by WellDone2044 on 17/09/15.
 */
public class DistanceMatrix {

    private int matrix[][];
    private int candidateList[][];

    public DistanceMatrix(ArrayList<Point> arr, int candidateSize){
        this.matrix = new int[arr.size()][arr.size()];
        this.candidateList = new int[arr.size()][candidateSize];

        for(int i=0; i<arr.size(); i++){
            matrix[i][i] = 0;
            for(int j=i+1; j<arr.size(); j++){
                matrix[i][j] = pointDistance(arr.get(i), arr.get(j));
                matrix[j][i] = pointDistance(arr.get(i), arr.get(j));
            }
        }

        for(int i=0; i<arr.size(); i++){
            int last = -1;
            for(int j=0; j<candidateSize; j++){
                int best = -1;
                int best_id = 0;
                for(int k=0; k<arr.size(); k++){
                    if(i!=k && (last == -1 || last < matrix[i][k]) && (best == -1 || matrix[i][k] <best)){
                        best = matrix[i][k];
                        best_id = k;
                    }
                }
                last = best;
                candidateList[i][j] = best_id;
            }
        }
    }

    public static int pointDistance(Point p1, Point p2){
        double x2 = Math.pow((p1.getX()-p2.getX()),2);
        double y2 = Math.pow((p1.getY()-p2.getY()),2);
        return (int) Math.round(Math.sqrt(x2+y2));
    }

    public int[][] getCandidateList(){
        return candidateList;
    }

    public int getDistance(int p1, int p2){
        return matrix[p1][p2];
    }
}
