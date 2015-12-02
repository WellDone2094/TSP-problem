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
    double q0 = 0.78;                            // follow pheromone  0.8 best


    boolean debug = true;

    public TSP_AntColony(int nodes_number, DistanceMatrix distanceMatrix, FileParser file, int len_nn, int[] init_solution){
        this.nodes_number = nodes_number;
        this.distanceMatrix = distanceMatrix;
        this.t0 = 1.0/(nodes_number*len_nn);
        this.pheromoneMap = new PheromoneMap(this.nodes_number, t0, alpha);
        this.seed = System.currentTimeMillis();
        this.ants_number = (int)(24);           // 24 best
        this.random = new Random(seed);
        this.file = file;

        LinkedListNode first = new LinkedListNode(init_solution[0], null, null);
        first.setNext(first); first.setPreview(first);
        LinkedListNode node = first;
        for (int i = 1; i < nodes_number; i++) {
            node.setNext(new LinkedListNode(init_solution[i], first, node));
            node = node.getNext();
            first.setPreview(node);
        }

        twoOpt(first);
        twoHOpt(first);
        int len = calculateLength(first);
        pheromoneMap.updatePath(first, len);
        System.out.println("end constructor");

    }

    @Override
    public int[] solve() {
        int max = 10000;
        LinkedListNode absolute_best_path = null;
        double absolute_best_cost = -1;
        int[] costs = new int[ants_number];
        LinkedListNode[] paths = new LinkedListNode[ants_number];
        Thread[] threads = new Thread[8];

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

            for (int i = 0; i < ants_number; i++) {
                paths[i] = ants.get(i).getPath();
            }

            for (int i = 0; i < 1; i++) {
//                threads[i] = new Thread(new TwoOptThread(paths, i*ants_number/8, (i+1)*ants_number/8, distanceMatrix, costs));
                threads[i] = new Thread(new TwoOptThread(paths, 0, ants_number, distanceMatrix, costs));
                threads[i].start();
            }

            for (int i = 0; i < 1; i++) {
                try {
                    threads[i].join();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //choose best ant
            LinkedListNode best_path = null;
            double best_cost = -1;
            for (int i = 0; i < ants_number; i++) {
                if(best_cost == -1 || best_cost > costs[i]){
                    best_cost = costs[i];
                    best_path = paths[i];
                }


//                LinkedListNode path = ants.get(i).getPath();
//                twoOpt(path);
//                twoHOpt(path);
//                int len = calculateLength(path);
//                if(best_cost == -1 || best_cost > len) {
//                    best_path = path;
//                    best_cost = len;
//                }
            }

//            threeOpt(best_path);

            best_cost = calculateLength(best_path);


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

//            if(k%10==0){
//                ants_number+=2;
//            }

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

//        LinkedListNode first = new LinkedListNode(0, null,null);
//        first.setNext(first);
//        first.setPreview(first);
//
//        LinkedListNode node = first;
//
//        for (int i = 1; i < nodes_number; i++) {
//            node.setNext(new LinkedListNode(i, first, node));
//            node = node.getNext();
//            first.setPreview(node);
//        }
//
//        twoOpt(first);
//        twoHOpt(first);
//        threeOpt(first);
//        node = first;
//        LinkedListNode nodePrev = first.getPreview();
//        LinkedListNode tmp;
//
//        do{
//            System.out.println(node.getValue()+1);
//            tmp = node;
//            node = node.getNext(nodePrev);
//            nodePrev = tmp;
//        }while(node!=first);
//
//        return new int[0];

    }






    public int calculateLength(LinkedListNode path){
        int tot = 0;
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
                    int c1 = distanceMatrix.getDistance(n1.getValue(),n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue());

                    int c2 = distanceMatrix.getDistance(n1.getValue(),n2.getValue()) +
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






    public void threeOpt(LinkedListNode first){

        LinkedListNode n1, n1prev, n2, n2prev, n3, n3prev, tmp;
        LinkedListNode bn1=null, bn1prev=null, bn2=null, bn2prev=null, bn3=null, bn3prev=null;
        boolean dir = false;

        int bestGain = -1;

        while(bestGain!=0) {
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
                } while ( n2 != first.getPreview());

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            } while (n1 != first.getPreview().getPreview(first));

            // exchange edge
            if(bestGain>0){
                if(dir){
                    LinkedListNode tmp1, tmp2, tmp3;
                    tmp1 = bn1.getNext(bn1prev);
                    tmp2 = bn2.getNext(bn2prev);
                    tmp3 = bn3.getNext(bn3prev);

                    if(bn1 == tmp3){
                        bn1.setNext(tmp2);
                        bn1.setPreview(bn2);
                    }else{
                        tmp3.substitute(bn3, bn2);
                        bn1.setNextFrom(bn1prev, tmp2);
                    }

                    if(bn2 == tmp1){
                        bn2.setNext(tmp3);
                        bn2.setPreview(bn3);
                    }else{
                        tmp1.substitute(bn1, bn3);
                        bn2.setNextFrom(bn2prev, tmp3);
                    }

                    if(bn3 == tmp2){
                        bn3.setNext(tmp1);
                        bn3.setPreview(bn1);
                    }else{
                        tmp2.substitute(bn2, bn1);
                        bn3.setNextFrom(bn3prev, tmp1);
                    }
                }else{
                    LinkedListNode tmp1, tmp2, tmp3;
                    tmp1 = bn1.getNext(bn1prev);
                    tmp2 = bn2.getNext(bn2prev);
                    tmp3 = bn3.getNext(bn3prev);

                    if(bn1 == tmp3){
                        bn1.setNext(bn3);
                        bn1.setPreview(bn2);
                    }else{
                        tmp3.substitute(bn3, bn2);
                        bn1.setNextFrom(bn1prev, bn3);
                    }
                    if(bn2 == tmp1){
                        bn2.setNext(tmp2);
                        bn2.setPreview(tmp3);
                    }else{
                        tmp1.substitute(bn1, tmp2);
                        bn2.setNextFrom(bn2prev,tmp3);
                    }
                    if(bn3 == tmp2){
                        bn3.setPreview(tmp1);
                        bn3.setNext(bn1);
                    }else{
                        tmp2.substitute(bn2, tmp1);
                        bn3.setNextFrom(bn3prev, bn1);
                    }
                }
            }
        }
    }

    public void twoHOpt(LinkedListNode first){
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
                n2 = n1.getNext(n1prev).getNext(n1);
                n2prev = n1.getNext(n1prev);
                do{
                    int c1 = distanceMatrix.getDistance(n1.getValue(),n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2prev.getValue());

                    int c2 = distanceMatrix.getDistance(n1.getValue(),n2.getValue()) +
                            distanceMatrix.getDistance(n2.getValue(),n1.getNext(n1prev).getValue())+
                            distanceMatrix.getDistance(n2prev.getValue(), n2.getNext(n2prev).getValue());

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
                }while(n2 != n1);

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            }while(n1 != first);

//            System.out.println(bestGain);
            if(bestGain > 0) {
                LinkedListNode bn1next = bn1.getNext(bn1prev);
                LinkedListNode bn2next = bn2.getNext(bn2prev);

                bn2next.substitute(bn2, bn2prev);
                bn2prev.substitute(bn2, bn2next);
                bn1.substitute(bn1next, bn2);
                bn1next.substitute(bn1, bn2);
                bn2.setPreview(bn1);
                bn2.setNext(bn1next);
            }
        }
    }
}
