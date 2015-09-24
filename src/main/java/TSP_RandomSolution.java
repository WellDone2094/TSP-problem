import java.util.Random;

/**
 * Created by WellDone2044 on 24/09/15.
 */
public class TSP_RandomSolution implements TSP_algorithm{

    private int number_nodes;

    public TSP_RandomSolution(int number_nodes){
       this.number_nodes = number_nodes;
    }

    @Override
    public int[] solve() {
        int[] arr = Main.generateArr(number_nodes);

        Random rand= new Random();
        for (int i = arr.length - 1; i > 0; i--){
            int n = rand.nextInt(i + 1);
            Main.switchElement(arr, i, n);
        }

        return arr;
    }
}
