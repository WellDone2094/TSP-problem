/**
 * Created by WellDone2044 on 24/09/15.
 */
public class LinkedListNode {
    private LinkedListNode next;
    private LinkedListNode preview;
    private int value;

    public LinkedListNode(int value, LinkedListNode next){
        this.value = value;
        this.next = next;
        this.preview = null;
    }

    public LinkedListNode(int value, LinkedListNode next, LinkedListNode preview) {
        this.value = value;
        this.next = next;
        this.preview = preview;

    }

    public LinkedListNode getPreview() {
        return preview;
    }

    public void setPreview(LinkedListNode preview){
        this.preview = preview;
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

    public LinkedListNode getNext(LinkedListNode node){
        if(node == next)
            return preview;
        return next;
    }

    public LinkedListNode getPreview(LinkedListNode node){

        if(node == preview)
            return next;
        return preview;
    }

    public void setNextFrom(LinkedListNode prev, LinkedListNode newNext){
        if(prev == next){
            preview = newNext;
            return;
        }
        next = newNext;
    }

    public void setPrevFrom(LinkedListNode next, LinkedListNode newPrev){
        if(next == preview){
            this.next = newPrev;
            return;
        }
        preview = newPrev;
    }

    public void substitute(LinkedListNode node, LinkedListNode newNode){
        if(next == node)
            next = newNode;
        if(preview == node)
            preview = newNode;

    }
}
