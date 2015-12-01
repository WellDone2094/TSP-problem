import org.w3c.dom.NodeList;
import sun.awt.image.ImageWatched;
import sun.jvm.hotspot.opto.Node_List;
import sun.util.resources.cldr.ka.LocaleNames_ka;

import javax.xml.soap.Node;
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
    private long seed;

    private FileParser file;

    int ants_number;
    double alpha = 0.1;                         // best path update
    double beta = 2;                            // pheromon power
    double q0 = 0.85;                            // follow pheromone


    boolean debug = true;

    public TSP_AntColony(int nodes_number, DistanceMatrix distanceMatrix, FileParser file, int len_nn){
        this.nodes_number = nodes_number;
        this.distanceMatrix = distanceMatrix;
        this.t0 = 1.0/(nodes_number*len_nn);
        this.pheromoneMap = new PheromoneMap(this.nodes_number, t0, alpha);
        this.seed = System.currentTimeMillis();
        this.ants_number = (int)(nodes_number*0.6);
        this.random = new Random(seed);
        this.file = file;
    }

    @Override
    public int[] solve() {
        int max = 10000;
        LinkedListNode absolute_best_path = null;
        double absolute_best_cost = -1;
        for (int k = 0; k < max; k++) {


            // generate ants
            ants = new ArrayList<>();
            for (int i = 0; i < ants_number; i++) {
                ants.add(new Ant(nodes_number, distanceMatrix, pheromoneMap, q0, beta, random.nextInt(nodes_number)));
            }

            // generate tours
            for(int i=0; i<nodes_number-1; i++){
                for(int j=0; j<ants_number; j++){
                    ants.get(j).chooseNextNode(random, false, null);
                }
            }

            //choose best ant
            LinkedListNode best_path = null;
            double best_cost = -1;
            for (int i = 0; i < ants_number; i++) {
                LinkedListNode path = ants.get(i).getPath();
                twoOpt(path);
                double len = calculateLength(path);
                if(best_cost == -1 || best_cost > len) {
                    best_path = path;
                    best_cost = len;
                }
            }

            if(absolute_best_cost == -1 || absolute_best_cost > best_cost){
                absolute_best_cost = best_cost;
                absolute_best_path = best_path;
                System.out.println(k + " - " + absolute_best_cost + " - " + ((absolute_best_cost - file.getBestKnow()) / file.getBestKnow()));
            }

            if(absolute_best_cost == file.getBestKnow()){
                System.out.println("Seed: "+seed);
                break;
            }

            pheromoneMap.updatePath(absolute_best_path, absolute_best_cost);

            if(debug && k%1000 == 0) {
                System.out.println(k+" - "+absolute_best_cost+" - "+((absolute_best_cost-file.getBestKnow())/file.getBestKnow()));
            }
        }



        int[] sol = new int[nodes_number];
        LinkedListNode n = absolute_best_path;
        LinkedListNode prev = n.getPreview();
        LinkedListNode next = n.getNext();

        int i = 0;
        while(n.getNext(prev) != absolute_best_path ){
            sol[i] = n.getValue();
            i++;
            next = n.getNext(prev);
            prev = n;
            n = next;
        }
        sol[i] = n.getValue();
        return sol;

    }

    public double calculateLength(LinkedListNode path){
        double tot = 0;
        LinkedListNode currentNode = path;
        LinkedListNode previousNode = currentNode.getPreview();
        LinkedListNode nextNode = currentNode.getNext();

        while(currentNode.getNext(previousNode) != path) {
            int a = currentNode.getValue();
            nextNode = currentNode.getNext(previousNode);
            previousNode = currentNode;
            currentNode = nextNode;
            int b = currentNode.getValue();
            tot += distanceMatrix.getDistance(a,b);
        }

        int a = currentNode.getValue();
        nextNode = currentNode.getNext(previousNode);
        previousNode = currentNode.getPreview(nextNode);
        currentNode = nextNode;
        int b = currentNode.getValue();
        tot += distanceMatrix.getDistance(a, b);

        return tot;
    }

    public void twoOpt(LinkedListNode first){
        double gain = 1;
        double bestGain = 1;

        while(bestGain != 0){
            gain = 0;
            bestGain = 0;
            LinkedListNode n1, n1prev, n2, n2prev, tmp;
            LinkedListNode bn1 = null, bn1prev = null, bn2 = null, bn2prev = null;
            n1 = first;
            n1prev = n1.getPreview();

            do{
                n2 = n1.getNext(n1prev);
                n2prev = n1;
                do{
                    double c1 = distanceMatrix.getDistance(n1.getValue(),n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue());

                    double c2 = distanceMatrix.getDistance(n1.getValue(),n2.getValue()) +
                            distanceMatrix.getDistance(n1.getNext(n1prev).getValue(), n2.getNext(n2prev).getValue());

                    gain = c1-c2;
                    if(gain > bestGain){
                        bestGain = gain;
                        bn1 = n1;
                        bn2 = n2;
                        bn1prev = n1prev;
                        bn2prev = n2prev;
                    }

                    tmp = n2;
                    n2 = n2.getNext(n2prev);
                    n2prev = tmp;
                }while(n2 != first);

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            }while(n1 != first.getPreview());

            if(bestGain > 0) {
                bn1.getNext(bn1prev).substitute(bn1, bn2.getNext(bn2prev));
                bn2.getNext(bn2prev).substitute(bn2, bn1.getNext(bn1prev));
                bn1.setNextFrom(bn1prev, bn2);
                bn2.setPrevFrom(bn2prev, bn1);
            }
        }
    }
}
