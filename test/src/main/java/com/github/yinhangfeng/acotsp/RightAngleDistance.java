package com.github.yinhangfeng.acotsp;

public class RightAngleDistance implements IDistance {

    public static final RightAngleDistance INSTANCE = new RightAngleDistance();

    @Override
    public double getDistance(Point p1, Point p2) {
        double distance = getDistance(p1, p2);
        double ratio = distance / 10000;
        if (ratio > 0.8) {
            ratio = 0.8f;
        }
        double rightAngleDistance = Math.round(Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y));
        double result = ratio * distance + (1 - ratio) * rightAngleDistance;
//        System.out.println("getRightAngleDistance distance: " + distance + " rightAngleDistance: " + rightAngleDistance + " result: " + result + " ratio: " + ratio);
        return result;
    }
}
