import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by WellDone2044 on 28/11/15.
 */
public class TSP_AntColony implements TSP_algorithm{

    private DistanceMatrix distanceMatrix;
    private PheromoneMap pheromoneMap;
    private int nodes_number;
    private double t0;
    private Random random;
    private ArrayList<Ant> ants;

    private FileParser file;

    int ants_number = 40;
    double alpha = 0.1;                         // best path update
    double beta = 2;                            // pheromon power
    double p = 0.2;                             // single edge update
    double q0 = 0.85;                            // follow pheromone

    public TSP_AntColony(int nodes_number, DistanceMatrix distanceMatrix, FileParser file){
        this.nodes_number = nodes_number;
        this.distanceMatrix = distanceMatrix;
        this.t0 = 1.0/(nodes_number*7500);
        this.pheromoneMap = new PheromoneMap(this.nodes_number, t0, p, alpha);
        this.random = new Random(System.currentTimeMillis());
        this.file = file;
    }

    @Override
    public int[] solve() {
        int max = 100000;
        LinkedListNode absolute_best_path = null;
        double absolute_best_cost = -1;
        for (int k = 0; k < max; k++) {
            ants = new ArrayList<>();
            for (int i = 0; i < ants_number; i++) {
                ants.add(new Ant(nodes_number, distanceMatrix, pheromoneMap, q0, beta, random.nextInt(nodes_number)));
            }
            for(int i=0; i<nodes_number-1; i++){
                for(int j=0; j<ants_number; j++){
                    ants.get(j).chooseNextNode(random, false, null);
                }
            }
            Ant best = null;
            for (int i = 0; i < ants_number; i++) {
                if(best == null || best.getCurrentLength() > ants.get(i).getCurrentLength()) {
                    best = ants.get(i);
                }
            }
            if(absolute_best_cost == -1 || absolute_best_cost > best.getCurrentLength()){
                absolute_best_cost = best.getCurrentLength();
                absolute_best_path = best.getPath();
            }
            pheromoneMap.evaporation();
            pheromoneMap.updatePath(absolute_best_path, absolute_best_cost);
            if(k%1000 == 0) {
                System.out.println("current: "+best.getCurrentLength());
                System.out.println("length: "+absolute_best_cost);
                System.out.println("error");
                System.out.println((absolute_best_cost-file.getBestKnow())/file.getBestKnow());
                System.out.println(k);
                pheromoneMap = new PheromoneMap(nodes_number, t0, p, alpha);
//                absolute_best_path = twoOpt(absolute_best_path);
//                absolute_best_cost = calculateLength(absolute_best_path);
            }
        }

        System.out.println(pheromoneMap);
        int[] sol = new int[nodes_number];
        LinkedListNode n = absolute_best_path;
        int i = 0;
        while(n != null){
            sol[i] = n.getValue();
            i++;
            n = n.getPreview();
        }
        return sol;

//        int[] prob = new int[nodes_number];
//        for (int i = 0; i < nodes_number*100; i++) {
//            Ant a = new Ant(nodes_number, distanceMatrix, pheromoneMap, 0, beta, 0);
//            a.chooseNextNode(random, true, prob);
//        }
//        return prob;
    }

    public LinkedListNode twoOpt(LinkedListNode path){
        double best = calculateLength(path);
        LinkedListNode node1 = path;
        while(node1 != null){
            LinkedListNode node2 = node1.getPreview();
            while(node2 != null){
                exchangeNode(node1, node2);
                double length;
                if(node1 == path){
                    length = calculateLength(node2);
                }else{
                    length = calculateLength(path);
                }
                if(length<best){
                    if(node1 == path){
                        return node2;
                    }
                    return path;
                }
                exchangeNode(node1,node2);
                node2 = node2.getPreview();
            }
            node1 = node1.getPreview();
        }
        return path;
    }

    public void exchangeNode(LinkedListNode node1, LinkedListNode node2){
        LinkedListNode supp = node1.getPreview();

        supp = node1.getNext();
    }


    public double calculateLength(LinkedListNode path){
        double tot = 0;
        LinkedListNode node = path;
        while( node.getPreview()!= null){
            tot += distanceMatrix.getDistance(node.getValue(), node.getPreview().getValue());
            node = node.getPreview();
        }
        tot += distanceMatrix.getDistance(node.getValue(), path.getValue());
        return tot;

    }
}
