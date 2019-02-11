package com.github.yinhangfeng.acotsp;

public class EuclideanDistance implements IDistance {

    public static final EuclideanDistance INSTANCE = new EuclideanDistance();

    @Override
    public double getDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
