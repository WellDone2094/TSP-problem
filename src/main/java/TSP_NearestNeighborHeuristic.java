import java.util.Random;

/**
 * Created by WellDone2044 on 24/09/15.
 */
public class TSP_NearestNeighborHeuristic implements TSP_algorithm{
    private DistanceMatrix dm;
    private int number_nodes;

    public TSP_NearestNeighborHeuristic(int number_nodes, DistanceMatrix distanceMatrix){
        this.number_nodes = number_nodes;
        this.dm = distanceMatrix;
    }


    @Override
    public int[] solve() {
        int[] arr = Main.generateArr(number_nodes);
        Random rand = new Random();
        int n = rand.nextInt(arr.length);
        Main.switchElement(arr, 0, n);
        for(int i=0; i< arr.length-1; i++){
            int min = i+1;
            for(int j = min+1; j<arr.length; j++){
                if(dm.getDistance(arr[i],arr[j])<dm.getDistance(arr[i], arr[min])){
                    min = j;
                }
            }
            Main.switchElement(arr, i + 1, min);
        }
        return arr;
    }
}
