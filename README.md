## Usage

```java
int pointCount = 50;
List<Point> points = new ArrayList<>(pointCount);
for (int i = 0; i < pointCount; ++i) {
    points.add(new Point(Math.random() * 100, Math.random() * 100));
}

AntColonyTSP antColonyTSP = AntColonyTSP.obtain(pointCount);
Response response = antColonyTSP.run(points);
antColonyTSP.recycle();

System.out.println("response: " + response);
```

## Running Test

```
./gradlew run
```