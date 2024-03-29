import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

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

        long seed = Long.parseLong(args[1]);
        double q0 = Double.parseDouble(args[2]);
        double q1 = Double.parseDouble(args[3]);
        double q2 = Double.parseDouble(args[4]);
        int thread_num = Integer.parseInt(args[5]);

        Random random = new Random(System.currentTimeMillis());
        DistanceMatrix distanceMatrix = new DistanceMatrix(fileParser.getCoordinates());

        TSP_algorithm solver = new TSP_AntColony(fileParser.getDimension(), distanceMatrix, fileParser, seed, q0, q1, q2, thread_num);

        int[] solution = solver.solve();

        System.out.println("Tour:");
        for(int i=0; i<solution.length; i++) {
            System.out.print(solution[i] + 1 + " ");
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
