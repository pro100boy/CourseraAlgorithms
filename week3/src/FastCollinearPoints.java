import java.util.*;

public class FastCollinearPoints {
    private LineSegment[] segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        Objects.requireNonNull(points);
        Arrays.stream(points).forEach(p -> Objects.requireNonNull(p));
        checkRepeatedPoints(points);

        List<LineSegment> segmentsList = new LinkedList<>();
        Point[] pointsCopy = Arrays.copyOf(points, points.length);

        for (Point startPoint : points) {
            // origin point
            Arrays.sort(pointsCopy, startPoint.slopeOrder());
            Map<Double, LinkedList<Point>> map = new HashMap<>();

            for (int j = 1; j < pointsCopy.length; j++) {
                Point q = pointsCopy[j];
                //if (q.compareTo(startPoint) == 0) continue;

                // adding points into map according to their slopes
                double d = q.slopeTo(startPoint);
                LinkedList<Point> tmpList = map.getOrDefault(d, new LinkedList<>());
                tmpList.add(q);
                map.put(d, tmpList);
            }

            // determine list with 4+ points with same slopes
            for (Map.Entry<Double, LinkedList<Point>> m : map.entrySet()) {
                LinkedList<Point> tmpList = m.getValue();
                if (tmpList.size() >= 3) {
                    tmpList.addLast(startPoint);
                    Collections.sort(tmpList);
                    LineSegment lineSegment = new LineSegment(tmpList.getFirst(), tmpList.getLast());
                    if (!containSegment(segmentsList, lineSegment)) segmentsList.add(lineSegment);
                }
            }
        }

        segments = segmentsList.toArray(new LineSegment[segmentsList.size()]);
    }

    private boolean containSegment(List<LineSegment> segmentsList, LineSegment lineSegment) {
        boolean res = false;
        for (LineSegment l : segmentsList)
            if (l.toString().equals(lineSegment.toString())) {
                res = true;
                break;
            }
        return res;
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segments, numberOfSegments());
    }

    private void checkRepeatedPoints(Point[] points) {
        //Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0) {
                    throw new IllegalArgumentException("Duplicated entries in given points.");
                }
            }
        }
    }
}
