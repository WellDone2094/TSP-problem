import sun.awt.image.ImageWatched;

/**
 * Created by WellDone2044 on 24/09/15.
 */
public class LinkedListNode {
    private LinkedListNode next;
    private int value;

    public LinkedListNode(int value, LinkedListNode next){
        this.value = value;
        this.next = next;
    }

    public LinkedListNode getNext() {
        return next;
    }

    public int getValue() {
        return value;
    }

    public void setNext(LinkedListNode next){
        this.next = next;

    }

    public void setValue(int value){
        this.value = value;
   }
}
