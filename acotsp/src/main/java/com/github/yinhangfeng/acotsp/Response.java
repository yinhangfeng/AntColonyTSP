package com.github.yinhangfeng.acotsp;

import java.util.Arrays;
import java.util.List;

public class Response {

    public List<Point> originalPoints;
    public double length;
    public int[] tour;
    public boolean endEqStart;
    // 最短距离的迭代次数
    public int iterationNum;

    @Override
    public String toString() {
        return "Response{" +
                "length=" + length +
                ", tour=" + Arrays.toString(tour) +
                ", endEqStart=" + endEqStart +
                ", iterationNum=" + iterationNum +
                '}';
    }
}
