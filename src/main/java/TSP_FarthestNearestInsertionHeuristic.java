import java.util.Random;

/**
 * Created by WellDone2044 on 24/09/15.
 */
public class TSP_FarthestNearestInsertionHeuristic implements TSP_algorithm{

    private int nodes_number;
    private DistanceMatrix dm;
    private boolean type;

    public TSP_FarthestNearestInsertionHeuristic(int nodes_number, DistanceMatrix distanceMatrix, boolean type){
        this.nodes_number = nodes_number;
        this.dm = distanceMatrix;
        this.type = type;
    }

    @Override
    public int[] solve() {
        int[] nodes = Main.generateArr(nodes_number);
        int[] dist = new int[nodes_number];

        Random rand = new Random();
        int n = rand.nextInt(nodes_number);
        Main.switchElement(nodes, 0, n);

        LinkedListNode first_node = new LinkedListNode(nodes[0], null);
        int lastAddedNode = nodes[0];

        for (int i = 1; i < nodes_number; i++) {
            //calculate distances
            for (int j = i; j < nodes_number; j++) {
               dist[j] += dm.getDistance(lastAddedNode, nodes[j]);
            }

            //choose farthest/nearest
            int selected = i;
            for(int j=i+1; j<nodes_number; j++){

                if(type) {
                    //Nearest
                    if (dist[j] < dist[selected]) {
                        selected = j;
                    }

                }else{
                    //farthest
                    if (dist[j] > dist[selected]) {
                        selected = j;
                    }
                }
            }

            //find best position
            LinkedListNode best_pos = first_node;
            LinkedListNode current_node = first_node;
            while(current_node.getNext() != null){
                int dist1 = dm.getDistance(current_node.getValue(), nodes[selected]);
                int dist2 = dm.getDistance(current_node.getNext().getValue(), nodes[selected]);
                int current_value = dist1 + dist2 - dm.getDistance(current_node.getValue(), current_node.getNext().getValue());

                int bst_dist1 = dm.getDistance(best_pos.getValue(), nodes[selected]);
                int bst_dist2 = dm.getDistance(best_pos.getNext().getValue(), nodes[selected]);
                int bst_value = bst_dist1 + bst_dist2 - dm.getDistance(best_pos.getValue(), best_pos.getNext().getValue());

                if(current_value < bst_value){
                    best_pos = current_node;
                }
                current_node = current_node.getNext();
            }

            if(first_node.getNext()!=null){
                int dist1 = dm.getDistance(current_node.getValue(), nodes[selected]);
                int dist2 = dm.getDistance(first_node.getValue(), nodes[selected]);
                int current_value = dist1 + dist2 - dm.getDistance(current_node.getValue(), first_node.getValue());

                int bst_dist1 = dm.getDistance(best_pos.getValue(), nodes[selected]);
                int bst_dist2 = dm.getDistance(best_pos.getNext().getValue(), nodes[selected]);
                int bst_value = bst_dist1 + bst_dist2 - dm.getDistance(best_pos.getValue(), best_pos.getNext().getValue());

                if(current_value < bst_value){
                    best_pos = current_node;
                }
            }

            best_pos.setNext(new LinkedListNode(nodes[selected], best_pos.getNext()));
            Main.switchElement(nodes, selected, i);
            Main.switchElement(dist, selected, i);
        }

        for(int i=0; i<nodes_number; i++){
            nodes[i] = first_node.getValue();
            first_node = first_node.getNext();
        }

        return nodes;
    }
}
