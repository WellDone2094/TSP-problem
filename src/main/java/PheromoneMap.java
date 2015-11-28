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

    void updatePath(LinkedListNode node, double length, boolean reverse){
        LinkedListNode currentNode = node;

        if(reverse){
            while(node.getPreview() != null) {
                int a = currentNode.getValue();
                currentNode = currentNode.getPreview();
                int b = currentNode.getValue();
                matrix[a][b] = (1 - alpha) * matrix[a][b] + alpha / length;
                matrix[b][a] = matrix[a][b];
            }
            int a = currentNode.getValue();
            int b = node.getValue();
            matrix[a][b] = (1 - alpha) * matrix[a][b] + alpha / length;
            matrix[b][a] = matrix[a][b];
        }else{
            while(node.getNext() != null) {
                int a = currentNode.getValue();
                currentNode = currentNode.getNext();
                int b = currentNode.getValue();
                matrix[a][b] = (1 - alpha) * matrix[a][b] + alpha / length;
                matrix[b][a] = matrix[a][b];
            }
            int a = currentNode.getValue();
            int b = node.getValue();
            matrix[a][b] = (1 - alpha) * matrix[a][b] + alpha / length;
            matrix[b][a] = matrix[a][b];

        }
    }

    double getPheromone(int a, int b){
        return matrix[a][b];
    }
}
