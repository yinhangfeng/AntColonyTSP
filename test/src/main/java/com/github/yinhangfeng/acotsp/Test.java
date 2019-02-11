package com.github.yinhangfeng.acotsp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    static List<Point> getRandomPoints(int count) {
        return getRandomPoints(count, -1);
    }

    static List<Point> getRandomPoints(int count, int parent) {
        ArrayList<Point> points = new ArrayList<>();

        float minLng = 120.07f;
        float maxLng = 120.4f;
        float minLat = 30.12f;
        float maxLat = 30.4f;
        for (int i = 0; i < count; ++i) {
            Point point = new Point();

            if (Math.random() > 0.5) {
                point.lng = (float) ((minLng + maxLng) / 2 + (1 - Math.random()) * (maxLng - minLng) * Math.random() * 0.4);
                point.lat = (float) ((minLat + maxLat) / 2 + (1 - Math.random()) * (maxLat - minLat) * Math.random() * 0.4);
            } else {
                point.lng = (float) (minLng + Math.random() * (maxLng - minLng));
                point.lat = (float) (minLat + Math.random() * (maxLat - minLat));
            }
            point.parent = parent;

            points.add(point);
        }
        CoordinateUtils.transformPoints(points, CoordinateUtils.COORDINATE_WGS84);
        return points;
    }

    static List<Point> getPointsFromFile(String path) throws Exception {
        ArrayList<Point> points = new ArrayList<>();
        String testPointsStr = FileUtils.readFileToString(new File(path), "utf8");
        JSONObject testJson = (JSONObject) JSONObject.parse(testPointsStr);
        JSONArray arr = testJson.getJSONArray("tour");
        for (int i = 0; i < arr.size(); ++i) {
            JSONObject jsonObject = arr.getJSONObject(i);
            int parent = jsonObject.getIntValue("parent");
            String id = jsonObject.getString("id");
            Point point = new Point();
            point.lng = jsonObject.getFloat("lng");
            point.lat = jsonObject.getFloat("lat");
            point.parent = parent;
            point.id = id;
            points.add(point);
        }
        CoordinateUtils.transformPoints(points, CoordinateUtils.COORDINATE_WGS84);
        return points;
    }

    static List<List<Point>> getAllPointsFromFile(String path) throws Exception {
        ArrayList<List<Point>> allPoints = new ArrayList<>();
        String testPointsStr = FileUtils.readFileToString(new File(path), "utf8");
        JSONArray testJsonArr = (JSONArray) JSONArray.parse(testPointsStr);

        for (int j = 0; j < testJsonArr.size(); ++j) {
            JSONArray arr = testJsonArr.getJSONArray(j);

            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < arr.size(); ++i) {
                JSONObject jsonObject = arr.getJSONObject(i);
                int parent = jsonObject.getIntValue("parent");
                String id = jsonObject.getString("id");
                Point point = new Point();
                point.lng = jsonObject.getFloat("lng");
                point.lat = jsonObject.getFloat("lat");
                point.parent = parent;
                point.id = id;
                points.add(point);
            }
            CoordinateUtils.transformPoints(points, CoordinateUtils.COORDINATE_WGS84);
            allPoints.add(points);
        }

        return allPoints;
    }

    static void output(Response response) {
        output(response, "acotsp.json");
    }

    static void output(Response response, String fileName) {
        JSONObject jsonObject = new JSONObject();

        List<Point> points = response.originalPoints;
        List<Point> tour = new ArrayList<>(points.size());
        JSONArray tourArray = new JSONArray(points.size());
        Point point;
        Point parent;
        for (int i = 0; i < response.tour.length; ++i) {
            point = points.get(response.tour[i]);
            tour.add(point);
        }
        for (int i = 0; i < tour.size(); ++i) {
            point = tour.get(i);

            // 由于 tour 的顺序与原来不同所以需要修改 parent
            if (point.parent >= 0) {
                point = point.clone();
                parent = points.get(point.parent);
                for (int j = 0; j < tour.size(); ++j) {
                    if (parent == tour.get(j)) {
                        point.parent = j;
                        break;
                    }
                }
            }

            tourArray.add(point);
        }
        jsonObject.put("tour", tourArray);
        jsonObject.put("length", response.length);

        File file = new File("../outputs/" + fileName);
        try {
            FileUtils.writeStringToFile(file, jsonObject.toJSONString(), "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Response run(AntColonyTSP antColonyTSP, List<Point> points, int startPointIndex, int endPointIndex, int maxIterations) {
        long start = System.currentTimeMillis();
        Response response = antColonyTSP.startPointIndex(startPointIndex).endPointIndex(endPointIndex).maxIterations(maxIterations).run(points);
        System.out.println("run time: " + (System.currentTimeMillis() - start));
        System.out.println("run res: " + response);

        return response;
    }

    static Response runAco(AntColonyTSP antColonyTSP, List<Point> points, int startPointIndex, int endPointIndex, int maxIterations) {
        long start = System.currentTimeMillis();
        Response response = antColonyTSP.startPointIndex(startPointIndex).endPointIndex(endPointIndex).runACO(points, maxIterations);
        System.out.println("runAco time: " + (System.currentTimeMillis() - start));
        System.out.println("runAco res: " + response);

        return response;
    }

    static Response runExhaustive(AntColonyTSP antColonyTSP, List<Point> points, int startPointIndex, int endPointIndex) {
        long start = System.currentTimeMillis();
        Response response = antColonyTSP.startPointIndex(startPointIndex).endPointIndex(endPointIndex).runExhaustive(points);
        System.out.println("runExhaustive time: " + (System.currentTimeMillis() - start));
        System.out.println("runExhaustive res: " + response);

        return response;
    }

    static void test() {
        int pointCount = 50;
        List<Point> points = new ArrayList<>(pointCount);
        for (int i = 0; i < pointCount; ++i) {
            points.add(new Point(Math.random() * 100, Math.random() * 100));
        }

        AntColonyTSP antColonyTSP = AntColonyTSP.obtain(pointCount);
        Response response = antColonyTSP.run(points);
        antColonyTSP.recycle();

        System.out.println("response: " + response);
    }

    // 初始信息素对结果的影响
    static void test1() {
        double sumRatio = 0;
        float res1SumIt = 0;
        float res2SumIt = 0;

        int testCount1 = 50;
        int testCount2 = 20;
        int testCount = testCount1 * testCount2;
        int maxIterations = 20;
        int pointCount = 30;

        double bestLength = Integer.MAX_VALUE;
        Response bestResponse = null;

        AntColonyTSP antColony = AntColonyTSP.obtain(pointCount);
        AntColonyTSP antColonyTSP1 = antColony;
        AntColonyTSP antColonyTSP2 = antColony;
        for (int i = 0; i < testCount1; ++i) {
            List<Point> points = getRandomPoints(pointCount);

            for (int j = 0; j < testCount2; ++j) {
//                antColonyTSP1 = AntColonyTSP.obtain(pointCount);
                antColonyTSP1.initialPheromone(1);
                Response response1 = runAco(antColonyTSP1, points, 0, -1, maxIterations);
                res1SumIt += response1.iterationNum;
                if (response1.length < bestLength) {
                    bestResponse = response1;
                }

//                antColonyTSP2 = AntColonyTSP.obtain(pointCount);
                antColonyTSP2.initialPheromone(1 / response1.length);
                Response response2 = runAco(antColonyTSP2, points, 0, -1, maxIterations);
                res2SumIt += response2.iterationNum;
                if (response2.length < bestLength) {
                    bestResponse = response2;
                }

                sumRatio += response1.length / response2.length;

                antColonyTSP1.recycle();
                antColonyTSP2.recycle();
            }
        }

        System.out.println("res1SumIt: " + (res1SumIt / testCount) + " res2SumIt: " + (res2SumIt / testCount) + " " + (sumRatio / testCount)
                + " bestResponse: " + bestResponse);

    }

    static void test2() {
        int maxIterations = 20;
        int pointCount = 10;
        List<Point> points = getRandomPoints(pointCount);
        AntColonyTSP antColonyTSP = AntColonyTSP.obtain(pointCount);
        Response res = run(antColonyTSP, points, 0, -1, maxIterations);
        antColonyTSP.recycle();

        output(res);
    }

    // 大量点测试
    static void test3() {
        int pointCount = 300;
        int maxIterations = Math.min(Math.max(pointCount, 20), 100);
        List<Point> points = getRandomPoints(pointCount);
        points.set(1, points.get(0).clone());
        points.set(4, points.get(0).clone());
        points.set(3, points.get(2).clone());
        AntColonyTSP antColonyTSP = new AntColonyTSP(pointCount);
        Response res = runAco(antColonyTSP, points, 0, -1, maxIterations);
        output(res);
    }

    // 穷举与蚁群的比较
    static void test4() {
        // 穷举 12 个点就要 30s
        int pointCount = 13;
        int maxIterations = Math.min(Math.max(pointCount, 30), 100);
        List<Point> points = getRandomPoints(pointCount);
//        originalPoints.set(1, originalPoints.get(0).clone());
//        originalPoints.set(4, originalPoints.get(0).clone());
//        originalPoints.set(3, originalPoints.get(2).clone());
//        originalPoints.addAll(getRandomPoints(4, 1));
        AntColonyTSP antColonyTSP = new AntColonyTSP(pointCount, false);

        Response res1 = runExhaustive(antColonyTSP, points, 0, -1);

        output(res1);

        Response res2 = runAco(antColonyTSP, points, 0, -1, maxIterations);

        output(res2, "tsp1.json");
    }

    static void test5() throws Exception {
        List<List<Point>> allPoints = getAllPointsFromFile("../outputs/tsp3.json");
        System.out.println("test6 allPoints.size() " + allPoints.size());
        for (int i = 0; i < allPoints.size(); ++i) {
            System.out.println("test6 " + i);
            List<Point> points = allPoints.get(i);
            AntColonyTSP antColonyTSP = AntColonyTSP.obtain(points.size());
            Response response = antColonyTSP.startPointIndex(points.size() - 1)
                    .endPointIndex(AntColonyTSP.RANDOM_POINT_INDEX)
                    .initialPheromone(0.001)
                    .maxIterations(Math.min(Math.max(points.size(), 20), 128))
                    .run(points);
            antColonyTSP.recycle();

            System.out.println("res " + response);
        }


//        output(res);
    }

    static void test6() throws Exception {
        int maxIterations = 50;
        List<Point> points = getRandomPoints(5);
        points.addAll(getRandomPoints(5, 1));
        points.addAll(getRandomPoints(5, 2));
        points.addAll(getRandomPoints(5, 3));
        points.addAll(getRandomPoints(5, 4));

//        List<Point> originalPoints = getPointsFromFile("../outputs/testTsp1.json");

        int pointCount = points.size();
        AntColonyTSP antColonyTSP = AntColonyTSP.obtain(pointCount);
//        antColonyTSP.rightAngleDistance(false);
        Response res = run(antColonyTSP, points, 0, -1, maxIterations);

        Response bestRes = res;

//        for (int i = 0; i < 10; ++i) {
//            antColonyTSP.initialPheromone(1 / bestRes.length);
//            res = runAco(antColonyTSP, originalPoints, -1, -1, maxIterations);
//            if (res.length < bestRes.length) {
//                bestRes = res;
//            }
//        }

        System.out.println("bestRes " + bestRes);


        output(bestRes);
    }

    public static void main(String[] args) throws Exception {

        test();
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();
//        test6();
    }
}
