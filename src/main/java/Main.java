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

        DistanceMatrix distanceMatrix = new DistanceMatrix(fileParser.getCoordinates(), 30);
//        int[][] candidateList = distanceMatrix.getCandidateList();
//        for (int i = 0; i < candidateList.length; i++) {
//            String s = "";
//            for (int j = 0; j < candidateList[0].length; j++) {
//                s += candidateList[i][j] + "\t";
//            }
//            System.out.println(s);
//        }


        TSP_algorithm nn = new TSP_NearestNeighborHeuristic(fileParser.getDimension(), distanceMatrix);
        int[] nn_sol = nn.solve();
        int sol_len = solutionSize(nn_sol, distanceMatrix);
        System.out.println(sol_len);

        TSP_algorithm solver = new TSP_AntColony(fileParser.getDimension(), distanceMatrix, fileParser, sol_len);

        int[] solution = solver.solve();

        for(int i=0; i<solution.length; i++) {
            System.out.println(solution[i]+1);
        }

    }

    public static int solutionSize(int[] solution, DistanceMatrix dm){
        int len = 0;
        for (int i = 0; i < solution.length; i++) {
            len += dm.getDistance(solution[i], solution[(i+1)%solution.length]);
        }
        return len;
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
