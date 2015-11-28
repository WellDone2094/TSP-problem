import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by WellDone2044 on 28/11/15.
 */
public class Ant {

    private DistanceMatrix distanceMatrix;
    private PheromoneMap pheromoneMap;
    private int nodes_number;
    private double q0;
    private double beta
    private double currentLeght;
    private int initNode;
    private LinkedListNode currentPath;
    private LinkedListNode availableNodes;

    public Ant(int nodes_number, DistanceMatrix distanceMatrix, PheromoneMap pheromoneMap, double q0, double beta, int initNode){
        this.nodes_number = nodes_number;
        this.distanceMatrix = distanceMatrix;
        this.pheromoneMap = pheromoneMap;
        this.q0 = q0;
        this.beta = beta;
        this.currentLeght = 0;
        this.initNode = initNode;
        this.currentPath = new LinkedListNode(initNode, null);
        this.availableNodes = null;
        LinkedListNode lastNode = null;
        for(int i=0; i<nodes_number; i++){
            if(i==initNode) continue;
            if(lastNode == null){
                availableNodes = new LinkedListNode(i, null, null);
                lastNode = availableNodes;
            }else{
                lastNode.setNext(new LinkedListNode(i, null, lastNode));
                lastNode = lastNode.getNext();
            }
        }
    }

    void chooseNextNode(Random random){
        double tot = 0;
        LinkedListNode best = null;
        LinkedListNode node = availableNodes;

        // sum all the value for each possible next edge
        while(node != null)){
            double dist = distanceMatrix.getDistance(currentPath.getValue(), node.getValue());
            double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), node.getValue()), beta);
            tot += pheromone/dist;

            if(best == null || distanceMatrix.getDistance(currentPath.getValue(), best.getValue()) > dist){
                best = node;
            }
            node = node.getNext();
        }

        // remove best from total
        tot -= pheromoneMap.getPheromone(currentPath.getValue(), best.getValue()) /
                Math.pow(distanceMatrix.getDistance(currentPath.getValue(), best.getValue()), beta);

        // if < q0 choose best otherwise choose between others
        if(random.nextFloat() < q0){
            node = best;
        }else{
            double select = random.nextFloat()*tot;
            double sum = 0;
            node = availableNodes;

            // loop until sum > select
            while(node != null) {
                double dist = distanceMatrix.getDistance(currentPath.getValue(), node.getValue());
                double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), node.getValue()), beta);
                sum += pheromone/dist;
                if (sum > select) break;
            }
        }

        //add node to current path;
        currentLeght += distanceMatrix.getDistance(currentPath.getValue(), node.getValue());
        currentPath.setNext(new LinkedListNode(node.getValue(), null, currentPath));
        currentPath = currentPath.getNext();

        // remove node from available
        node.getPreview().setNext(node.getNext());
    }

    public double getCurrentLength(){
        return currentLeght;
    }

    public LinkedListNode getPath(){
        return currentPath;
    }

}
