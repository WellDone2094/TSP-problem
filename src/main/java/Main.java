import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by WellDone2044 on 17/09/15.
 */
public class Main {
    public static void main(String[] args) {
        FileParser fileParser = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            fileParser = new FileParser(br);
        }catch (Exception ex){
            ex.printStackTrace();
            return;
        }

        DistanceMatrix distanceMatrix = new DistanceMatrix(fileParser.getCoordinates());
        TSP_algorithm nn = new TSP_NearestNeighborHeuristic(fileParser.getDimension(), distanceMatrix);
        int[] sol = nn.solve();
        int sol_size = 0;
        for (int i = 0; i < sol.length; i++) {
            sol_size += distanceMatrix.getDistance(sol[i],sol[(i+1)%sol.length]);
        }

        TSP_algorithm solver = new TSP_AntColony(fileParser.getDimension(), distanceMatrix, fileParser, sol_size);
//        TSP_algorithm solver = new TSP_FarthestNearestInsertionHeuristic(fileParser.getDimension(), distanceMatrix, false);

        int[] solution = solver.solve();

        for(int i=0; i<solution.length; i++) {
            System.out.println(solution[i]+1);
        }

    }

    public static int[] generateArr(int len){
        int arr[] = new int [len];
        for (int i=0; i<len; i++){
            arr[i] = i;
        }
        return arr;
    }


    public static void switchElement(int[] arr, int i, int j) {
        int supp = arr[i];
        arr[i] = arr[j];
        arr[j] = supp;
    }



}
