/**
 * Created by WellDone2044 on 28/11/15.
 */
public class PheromoneMap {

    private double matrix[][];
    private double initValue;
    private int nodes_number;
    private double p;
    private double alpha;

    public  PheromoneMap(int nodes_number, double initValue, double p, double alpha) {

        this.nodes_number = nodes_number;
        this.initValue = initValue;
        this.p = p;
        this.alpha = alpha;
        matrix = new double[nodes_number][nodes_number];

        for (int i = 0; i < this.nodes_number; i++){
            for (int j = 0; j < this.nodes_number; j++) {
                matrix[i][j] = 1;
            }
        }
    }

    public void updateConnection(int a, int b){
        matrix[a][b] = (1-p)*matrix[a][b]+p;
        matrix[b][a] = matrix[a][b];
    }

    public void evaporation(){
        for (int i = 0; i < nodes_number; i++) {
            for (int j = 0; j < nodes_number; j++) {
                matrix[i][j] = matrix[i][j]*(1-alpha)+alpha*0.1;
            }
        }
    }

    void updatePath(LinkedListNode node, double length){
        LinkedListNode currentNode = node;

        while(currentNode.getPreview() != null) {
            int a = currentNode.getValue();
            currentNode = currentNode.getPreview();
            int b = currentNode.getValue();
            matrix[a][b] += alpha/length;
            matrix[b][a] = matrix[a][b];
        }

        int a = currentNode.getValue();
        int b = node.getValue();
        matrix[a][b] +=  (alpha / length)/initValue;
        matrix[b][a] = matrix[a][b];
    }

    double getPheromone(int a, int b){
        return matrix[a][b];
    }

    public String toString(){
        String s = "";
        for (int i = 0; i < nodes_number; i++) {
            for (int j = 0; j < nodes_number; j++) {
                s += matrix[i][j]+"\t";
            }
            s+="\n";
        }
        return s;
    }
}
