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
    private double beta;
    private int currentLeght;
    private int initNode;
    private LinkedListNode currentPath;
    private LinkedListNode firstNode;
    private LinkedListNode availableNodes;
    private boolean[] used;

    public Ant(int nodes_number, DistanceMatrix distanceMatrix, PheromoneMap pheromoneMap, double q0, double beta, int initNode){
        this.nodes_number = nodes_number;
        this.distanceMatrix = distanceMatrix;
        this.pheromoneMap = pheromoneMap;
        this.q0 = q0;
        this.beta = beta;
        this.currentLeght = 0;
        this.initNode = initNode;
        this.currentPath = new LinkedListNode(initNode, null);
        this.firstNode = currentPath;
        this.firstNode.setNext(firstNode);
        this.firstNode.setPreview(firstNode);
        this.availableNodes = null;
        this.used = new boolean[nodes_number];
        for (int i = 0; i < nodes_number; i++) {
            this.used[i] = false;
        }
        this.used[initNode] = true;

        LinkedListNode lastNode = null;
        for(int i=nodes_number-1; i>=0; i--){
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



    void chooseNextNode(Random random, boolean debug, int[] prob){
        used[currentPath.getValue()] = true;
        double tot = 0;
        double candidateTot = 0;
        LinkedListNode best = null;
        LinkedListNode node = availableNodes;
        int[] candidate = distanceMatrix.getCandidateList()[currentPath.getValue()];

        // sum candidate
        for (int i = 0; i < candidate.length; i++) {
            if(!used[candidate[i]]) {
                double dist = distanceMatrix.getDistance(currentPath.getValue(), candidate[i]);
                double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), candidate[i]), beta);
                candidateTot += pheromone / dist;
            }
        }

        // sum all the value for each possible next edge
        while(node != null){
            // if a node is used but still in avaiable list remove it
            if(used[node.getValue()]){
                LinkedListNode tmp = node;
                if(node == availableNodes)
                    availableNodes = node.getNext();
                node = node.getNext();
                if(node!=null)
                    node.setPreview(tmp.getPreview());
                if(tmp.getPreview() != null)
                    tmp.getPreview().setNext(node);
                continue;
            }
            double dist = distanceMatrix.getDistance(currentPath.getValue(), node.getValue());
            double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), node.getValue()), beta);
            tot += pheromone/dist;

            if(best == null || distanceMatrix.getDistance(currentPath.getValue(), best.getValue()) > dist){
                best = node;
            }
            node = node.getNext();
        }

        int candidateSelected = -1;
        // if < q0 choose best otherwise choose between others
        if(random.nextFloat() < q0){
            node = best;
        }else{
            // if there are candidate choose between them
            if(candidateTot!=0){
                double select = random.nextDouble() * candidateTot;
                double sum = 0;
                for (int i = 0; i < candidate.length; i++) {
                    if(!used[candidate[i]]){
                        double dist = distanceMatrix.getDistance(currentPath.getValue(), candidate[i]);
                        double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), candidate[i]), beta);
                        sum += pheromone/dist;
                        candidateSelected = candidate[i];
                        if(sum>=select) break;
                    }
                }
            //if no candidate left choose between other
            }else {
                double select = random.nextDouble() * tot;
                double sum = 0;
                node = availableNodes;

                // loop until sum > select
                while (node != null) {
                    double dist = distanceMatrix.getDistance(currentPath.getValue(), node.getValue());
                    double pheromone = Math.pow(pheromoneMap.getPheromone(currentPath.getValue(), node.getValue()), beta);
                    sum += pheromone / dist;
                    if (sum >= select) break;
                    node = node.getNext();
                }
            }
        }

        int selected;
        if(candidateSelected != -1){
            selected = candidateSelected;
        }else{
            selected = node.getValue();
            // remove node from available
            if(node.getPreview() != null) {
                LinkedListNode prev = node.getPreview();
                LinkedListNode next = node.getNext();
                prev.setNext(next);
                if(next!=null)
                    next.setPreview(prev);
            }else {
                availableNodes = availableNodes.getNext();
                if(availableNodes!=null)
                    availableNodes.setPreview(null);
            }
        }

        //mark node like used
        used[selected] = true;


        //update pheromone
        pheromoneMap.updateConnection(currentPath.getValue(), selected);

        //add node to current path;
        currentLeght += distanceMatrix.getDistance(currentPath.getValue(), selected);
        currentPath.setNext(new LinkedListNode(selected, firstNode, currentPath));
        currentPath = currentPath.getNext();
        firstNode.setPreview(currentPath);
    }

    public int getCurrentLength(){
        return currentLeght+distanceMatrix.getDistance(initNode, currentPath.getValue());
    }

    public LinkedListNode getPath(){
        return firstNode;
    }

}
