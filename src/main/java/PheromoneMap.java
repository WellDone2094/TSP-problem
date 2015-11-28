/**
 * Created by WellDone2044 on 28/11/15.
 */
public class PheromoneMap {

    private double matrix[][];
    private double initValue;
    private int nodes_number;
    private double p;
    private double alpha;

    void PheromoneMap(int nodes_number, double initValue, double p, double alpha) {

        this.nodes_number = nodes_number;
        this.initValue = initValue;
        this.p = p;
        this.alpha = alpha;

        for (int i = 0; i < this.nodes_number; i++){
            for (int j = 0; j < this.nodes_number; j++) {
                matrix[i][j] = this.initValue;
            }
        }
    }

    void updateConnection(int a, int b){
        matrix[a][b] = (1-p)*matrix[a][b]+p*initValue;
        matrix[b][a] = matrix[a][b];
    }

    void updatePath(int[] path, double length){
        for (int i = 0; i < path.length; i++) {
            int a = path[i];
            int b = path[(i+1)%path.length)];
            matrix[a][b] = (1-alpha)*matrix[a][b]+alpha/length;
            matrix[b][a] = matrix[a][b];
        }
    }

    double getPheromone(int a, int b){
        return matrix[a][b];
    }
}
