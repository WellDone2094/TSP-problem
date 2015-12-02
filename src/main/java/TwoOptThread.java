/**
 * Created by WellDone2044 on 02/12/15.
 */
public class TwoOptThread implements Runnable {
    private Ant[] ants;
    private int start;
    private int end;
    private int[] results;
    private DistanceMatrix distanceMatrix;

    public TwoOptThread(Ant[] ants, int start, int end, DistanceMatrix distanceMatrix, int[] results) {
        this.ants = ants;
        this.start = start;
        this.end = end;
        this.distanceMatrix = distanceMatrix;
        this.results = results;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            int len = twoOpt(ants[i].getPath(), distanceMatrix, ants[i].getCurrentLength());
            len  = twoHOpt(ants[i].getPath(), distanceMatrix, len);
//            results[i] = calculateLength(ants[i], distanceMatrix);
            results[i] = len;
        }
    }


    public static int twoOpt(LinkedListNode first, DistanceMatrix distanceMatrix, int len) {
        double gain = 1;
        double bestGain = 1;

        while (bestGain != 0) {
            bestGain = 0;
            LinkedListNode n1, n1prev, n2, n2prev, tmp;
            LinkedListNode bn1 = null, bn1prev = null, bn2 = null, bn2prev = null;
            n1 = first;
            n1prev = n1.getPreview();

            do {
                n2 = n1.getNext(n1prev);
                n2prev = n1;
                do {
                    int c1 = distanceMatrix.getDistance(n1.getValue(), n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue());

                    int c2 = distanceMatrix.getDistance(n1.getValue(), n2.getValue()) +
                            distanceMatrix.getDistance(n1.getNext(n1prev).getValue(), n2.getNext(n2prev).getValue());

                    gain = c1 - c2;
                    if (gain > bestGain) {
                        bestGain = gain;
                        bn1 = n1;
                        bn2 = n2;
                        bn1prev = n1prev;
                        bn2prev = n2prev;
                    }

                    tmp = n2;
                    n2 = n2.getNext(n2prev);
                    n2prev = tmp;
                } while (n2 != first);

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            } while (n1 != first.getPreview());

            if (bestGain > 0) {
                bn1.getNext(bn1prev).substitute(bn1, bn2.getNext(bn2prev));
                bn2.getNext(bn2prev).substitute(bn2, bn1.getNext(bn1prev));
                bn1.setNextFrom(bn1prev, bn2);
                bn2.setPrevFrom(bn2prev, bn1);
                len -= bestGain;
            }
        }

        return len;
    }

    public static int twoHOpt(LinkedListNode first, DistanceMatrix distanceMatrix, int len) {
        double gain = 1;
        double bestGain = 1;

        while (bestGain != 0) {
            gain = 0;
            bestGain = 0;
            LinkedListNode n1, n1prev, n2, n2prev, tmp;
            LinkedListNode bn1 = null, bn1prev = null, bn2 = null, bn2prev = null;
            n1 = first;
            n1prev = n1.getPreview();

            do {
                n2 = n1.getNext(n1prev).getNext(n1);
                n2prev = n1.getNext(n1prev);
                do {
                    int c1 = distanceMatrix.getDistance(n1.getValue(), n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2.getNext(n2prev).getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n2prev.getValue());

                    int c2 = distanceMatrix.getDistance(n1.getValue(), n2.getValue()) +
                            distanceMatrix.getDistance(n2.getValue(), n1.getNext(n1prev).getValue()) +
                            distanceMatrix.getDistance(n2prev.getValue(), n2.getNext(n2prev).getValue());

                    gain = c1 - c2;
                    if (gain > bestGain) {
                        bestGain = gain;
                        bn1 = n1;
                        bn2 = n2;
                        bn1prev = n1prev;
                        bn2prev = n2prev;
                    }

                    tmp = n2;
                    n2 = n2.getNext(n2prev);
                    n2prev = tmp;
                } while (n2 != n1);

                tmp = n1;
                n1 = n1.getNext(n1prev);
                n1prev = tmp;
            } while (n1 != first);

            if (bestGain > 0) {
                LinkedListNode bn1next = bn1.getNext(bn1prev);
                LinkedListNode bn2next = bn2.getNext(bn2prev);

                bn2next.substitute(bn2, bn2prev);
                bn2prev.substitute(bn2, bn2next);
                bn1.substitute(bn1next, bn2);
                bn1next.substitute(bn1, bn2);
                bn2.setPreview(bn1);
                bn2.setNext(bn1next);

                len -= bestGain;
            }
        }
        return len;
    }


    public static int calculateLength(LinkedListNode path, DistanceMatrix distanceMatrix){
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
        currentNode = nextNode;
        int b = currentNode.getValue();
        tot += distanceMatrix.getDistance(a, b);

        return tot;
    }

}

