/**
 * Created by WellDone2044 on 17/09/15.
 */
public class Point {

    private int id;
    private double x;
    private double y;

    public Point(int id, double x, double y){
        this.id = id;
        this.x  = x;
        this.y  = y;
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public int getId() {
        return id;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
