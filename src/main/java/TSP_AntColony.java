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
public class TSP_AntColony implements TSP_algorithm {

    private DistanceMatrix distanceMatrix;
    private PheromoneMap pheromoneMap;
    private int nodes_number;
    private double t0;
    private Random random;
    private Ant[] ants;
    private long seed;
    private long startTime;

    private FileParser file;

    int ants_number;
    double alpha = 0.1;                         // best path update
    double beta = 2;                            // pheromon power
    double q0 = 0.8;                            // follow pheromone  0.78 best
    double q1 = 0.75;
    double q2 = 0.65;


    public TSP_AntColony(int nodes_number, DistanceMatrix distanceMatrix, FileParser file, long seed) {

        this.startTime = System.currentTimeMillis();
        this.seed = System.currentTimeMillis();
        this.random = new Random(this.seed);
        this.distanceMatrix = distanceMatrix;

        TSP_NearestNeighborHeuristic nn = new TSP_NearestNeighborHeuristic(nodes_number, distanceMatrix, this.random);
        int[] init_solution = nn.solve();
        int len = 0;
        for (int i = 0; i < nodes_number; i++) {
            len += distanceMatrix.getDistance(init_solution[i], init_solution[(i + 1) % nodes_number]);
        }

        this.nodes_number = nodes_number;
        this.t0 = 1.0 / (nodes_number * len);
        this.pheromoneMap = new PheromoneMap(this.nodes_number, t0, alpha);
        this.ants_number = (int) (24);           // 24 best
        this.file = file;

        LinkedListNode first = new LinkedListNode(init_solution[0], null, null);
        first.setNext(first);
        first.setPreview(first);
        LinkedListNode node = first;
        for (int i = 1; i < nodes_number; i++) {
            node.setNext(new LinkedListNode(init_solution[i], first, node));
            node = node.getNext();
            first.setPreview(node);
        }

        len = TwoOptThread.twoOpt(first, distanceMatrix, len);
        len = TwoOptThread.twoHOpt(first, distanceMatrix, len);
        pheromoneMap.updatePath(first, len);

    }

    @Override
    public int[] solve() {
        LinkedListNode absolute_best_path = null;
        double absolute_best_cost = -1;
        int iteration = 0;

        while ((System.currentTimeMillis() - startTime) < 2.9 * 60 * 1000) {
            int[] costs = new int[ants_number];
            LinkedListNode[] paths = new LinkedListNode[ants_number];
            Thread[] threads = new Thread[8];


            // generate ants
            ants = new Ant[ants_number];
            for (int i = 0; i < ants_number; i++) {
                ants[i] = new Ant(nodes_number, distanceMatrix, pheromoneMap, q0, beta, random.nextInt(nodes_number));
            }

            // generate tours
            for (int i = 0; i < nodes_number - 1; i++) {
                for (int j = 0; j < ants_number; j++) {
                    ants[j].chooseNextNode(random, false, null);
                }
            }

            for (int i = 0; i < ants_number; i++) {
                paths[i] = ants[i].getPath();
            }

            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new TwoOptThread(ants, i * ants_number / threads.length, (i + 1) * ants_number / threads.length, distanceMatrix, costs));
                threads[i].start();
            }


            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            //choose best ant
            LinkedListNode best_path = null;
            double best_cost = -1;
            for (int i = 0; i < ants_number; i++) {
                if (best_cost == -1 || best_cost > costs[i]) {
                    best_cost = costs[i];
                    best_path = paths[i];
                }
            }

            if (absolute_best_cost == -1 || absolute_best_cost > best_cost) {
                absolute_best_cost = best_cost;
                absolute_best_path = best_path;
                double err = (absolute_best_cost-file.getBestKnow())/file.getBestKnow();
                if(err<0.01){
                    q0 = q1;
                }
                if(err<0.006){
                    q0 = q2;
                }
            }

            if (absolute_best_cost == file.getBestKnow()) {
                break;
            }

            pheromoneMap.updatePath(absolute_best_path, absolute_best_cost);

            iteration++;

        }

        double err = (absolute_best_cost-file.getBestKnow())/file.getBestKnow();
        System.out.println(err);
        System.out.println(seed);


        int[] sol = new int[nodes_number];
        LinkedListNode n = absolute_best_path;
        LinkedListNode prev = n.getPreview();
        LinkedListNode next = n.getNext();

        int i = 0;
        while (n.getNext(prev) != absolute_best_path) {
            sol[i] = n.getValue();
            i++;
            next = n.getNext(prev);
            prev = n;
            n = next;
        }
        sol[i] = n.getValue();
        return sol;
    }



















    public void threeOpt(LinkedListNode first) {

        LinkedListNode n1, n1prev, n2, n2prev, n3, n3prev, tmp;
        LinkedListNode bn1 = null, bn1prev = null, bn2 = null, bn2prev = null, bn3 = null, bn3prev = null;
        boolean dir = false;

        int bestGain = -1;

        while (bestGain != 0) {
            bestGain = 0;
            n1 = first;
            n1prev = n1.getPreview();
            do {
                n2 = n1.getNext(n1prev);
                n2prev = n1;
                do {
                    n3 = n2.getNext(n2prev);
                    n3prev = n2;
                    do {

                        int c1 = distanceMatrix.getDistance(n1.getValue(), n1.getNext(n1prev).getValue()) +
                                distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue()) +
                                distanceMatrix.getDistance(n3.getValue(), n3.getNext(n3prev).getValue());

                        int c2 = distanceMatrix.getDistance(n1.getValue(), n2.getNext(n2prev).getValue()) +
                                distanceMatrix.getDistance(n2.getValue(), n3.getNext(n3prev).getValue()) +
                                distanceMatrix.getDistance(n3.getValue(), n1.getNext(n1prev).getValue());

                        int c3 = distanceMatrix.getDistance(n1.getValue(), n3.getValue()) +
                                distanceMatrix.getDistance(n1.getNext(n1prev).getValue(), n2.getNext(n2prev).getValue()) +
                                distanceMatrix.getDistance(n2.getValue(), n3.getNext(n3prev).getValue());

                        int gain1 = c1 - c2;
                        int gain2 = c1 - c3;


                        if (gain1 > bestGain) {
                            bestGain = gain1;
                            bn1 = n1;
                            bn2 = n2;
                            bn3 = n3;
                            bn1prev = n1prev;
                            bn2prev = n2prev;
                            bn3prev = n3prev;
                            dir = true;
                        }
                        if (gain2 > bestGain) {
                            bestGain = gain2;
                            bn1 = n1;
                            bn2 = n2;
                            bn3 = n3;
                            bn1prev = n1prev;
                            bn2prev = n2prev;
                            bn3prev = n3prev;
                            dir = false;

                        }

                        tmp = n3;
                        n3 = n3.getNext(n3prev);
                        n3prev = tmp;
                    } while (n3 != first);

                    tmp = n2;
                    n2 = n2.getNext(n2prev);
                    n2prev = tmp;
                } while (n2 != first.getPreview());

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            } while (n1 != first.getPreview().getPreview(first));

            // exchange edge
            if (bestGain > 0) {
                if (dir) {
                    LinkedListNode tmp1, tmp2, tmp3;
                    tmp1 = bn1.getNext(bn1prev);
                    tmp2 = bn2.getNext(bn2prev);
                    tmp3 = bn3.getNext(bn3prev);

                    if (bn1 == tmp3) {
                        bn1.setNext(tmp2);
                        bn1.setPreview(bn2);
                    } else {
                        tmp3.substitute(bn3, bn2);
                        bn1.setNextFrom(bn1prev, tmp2);
                    }

                    if (bn2 == tmp1) {
                        bn2.setNext(tmp3);
                        bn2.setPreview(bn3);
                    } else {
                        tmp1.substitute(bn1, bn3);
                        bn2.setNextFrom(bn2prev, tmp3);
                    }

                    if (bn3 == tmp2) {
                        bn3.setNext(tmp1);
                        bn3.setPreview(bn1);
                    } else {
                        tmp2.substitute(bn2, bn1);
                        bn3.setNextFrom(bn3prev, tmp1);
                    }
                } else {
                    LinkedListNode tmp1, tmp2, tmp3;
                    tmp1 = bn1.getNext(bn1prev);
                    tmp2 = bn2.getNext(bn2prev);
                    tmp3 = bn3.getNext(bn3prev);

                    if (bn1 == tmp3) {
                        bn1.setNext(bn3);
                        bn1.setPreview(bn2);
                    } else {
                        tmp3.substitute(bn3, bn2);
                        bn1.setNextFrom(bn1prev, bn3);
                    }
                    if (bn2 == tmp1) {
                        bn2.setNext(tmp2);
                        bn2.setPreview(tmp3);
                    } else {
                        tmp1.substitute(bn1, tmp2);
                        bn2.setNextFrom(bn2prev, tmp3);
                    }
                    if (bn3 == tmp2) {
                        bn3.setPreview(tmp1);
                        bn3.setNext(bn1);
                    } else {
                        tmp2.substitute(bn2, tmp1);
                        bn3.setNextFrom(bn3prev, bn1);
                    }
                }
            }
        }
    }
}
