/**
 * Created by WellDone2044 on 28/11/15.
 */
public class PheromoneMap {

    private double matrix[][];
    private double t0;
    private int nodes_number;
    private double alpha;

    public  PheromoneMap(int nodes_number, double t0, double alpha) {

        this.nodes_number = nodes_number;
        this.t0 = t0;
        this.alpha = alpha;
        matrix = new double[nodes_number][nodes_number];

        for (int i = 0; i < this.nodes_number; i++){
            for (int j = 0; j < this.nodes_number; j++) {
                matrix[i][j] = t0;
            }
        }
    }

    public void updateConnection(int a, int b){
        matrix[a][b] = (1-alpha)*matrix[a][b];
        matrix[b][a] = matrix[a][b];
    }

    void updatePath(LinkedListNode node, int length){
        LinkedListNode currentNode = node;
        LinkedListNode previousNode = currentNode.getPreview();
        LinkedListNode nextNode = currentNode.getNext();

        do{
            int a = currentNode.getValue();
            nextNode = currentNode.getNext(previousNode);
            previousNode = currentNode;
            currentNode = nextNode;
            int b = currentNode.getValue();
            matrix[a][b] = matrix[a][b]*(1-alpha) + alpha/length;
            matrix[b][a] = matrix[a][b];
        }while(currentNode != node);

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
