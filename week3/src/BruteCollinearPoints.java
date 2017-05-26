import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BruteCollinearPoints {
    private LineSegment[] segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        // Throws NullPointerException if obj is null
        Objects.requireNonNull(points);
        Arrays.stream(points).forEach(p -> Objects.requireNonNull(p));
        checkDuplicatedEntries(points);

        List<LineSegment> segmentList = new ArrayList<>();
        for (int i = 0; i < points.length - 3; i++) {
            Point p = points[i];
            Point q = points[i + 1];
            Point r = points[i + 2];
            Point s = points[i + 3];
            if (p.isCollinear(q, r, s)) segmentList.add(new LineSegment(p, s));
        }
        segments = segmentList.toArray(new LineSegment[segmentList.size()]);
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return segments;
    }

    private void checkDuplicatedEntries(Point[] points) {
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++)
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicated entries in given points.");
    }
}
