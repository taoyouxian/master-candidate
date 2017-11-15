package cn.edu.ruc.iir.mc.bbm.domain;

/**
 * @version V1.0
 * @Package: cn.edu.ruc.iir.mc.bbm.domain
 * @ClassName: Point
 * @Description: ID, PointX, PointY
 * @author: taoyouxian
 * @date: Create in 2017-11-14 18:43
 **/
public class Point {

    private int ID;
    private float PointX;
    private float PointY;

    public Point() {
    }

    public Point(int ID, float pointX, float pointY) {
        this.ID = ID;
        PointX = pointX;
        PointY = pointY;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getPointX() {
        return PointX;
    }

    public void setPointX(float pointX) {
        PointX = pointX;
    }

    public float getPointY() {
        return PointY;
    }

    public void setPointY(float pointY) {
        PointY = pointY;
    }
}
