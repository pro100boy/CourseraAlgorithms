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

        Point[] pointsCopy = Arrays.copyOf(points, points.length);
        List<LineSegment> segmentList = new ArrayList<>();

        for (int a = 0; a < pointsCopy.length - 3; a++) {
            Point p = pointsCopy[a];
            for (int b = a + 1; b < pointsCopy.length - 2; b++) {
                Point q = pointsCopy[b];
                for (int c = b + 1; c < pointsCopy.length - 1; c++) {
                    Point r = pointsCopy[c];
                    for (int d = c + 1; d < pointsCopy.length; d++) {
                        Point s = pointsCopy[d];

                        if (isCollinear(p, q, r, s))
                            segmentList.add(new LineSegment(p, s));

                    }
                }
            }
        }

        segments = segmentList.toArray(new LineSegment[segmentList.size()]);
    }

    // is a-b-c collinear?
    private boolean isCollinear(Point p, Point q, Point r, Point s) {
        return p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(q) == p.slopeTo(s);
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segments, numberOfSegments());
    }

    private void checkDuplicatedEntries(Point[] points) {
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++)
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicated entries in given points.");
    }
}
