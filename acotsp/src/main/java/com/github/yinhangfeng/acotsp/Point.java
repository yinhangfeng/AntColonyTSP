package com.github.yinhangfeng.acotsp;

public class Point implements Cloneable {
    public double x;
    public double y;
    public float lng;
    public float lat;
    public int parent = -1;
    public String id;

    public Point() {

    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    @Override
    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "x:" + x +
                ", y:" + y +
                ", lng:" + lng +
                ", lat:" + lat +
                ", id:'" + id + '\'' +
                '}';
    }
}
