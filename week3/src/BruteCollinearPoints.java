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

        for (int p = 0; p < points.length - 3; p++) {
            Point P = points[p];
            for (int q = p + 1; q < points.length - 2; q++) {
                Point Q = points[q];
                for (int r = q + 1; r < points.length - 1; r++) {
                    Point R = points[r];
                    for (int s = r + 1; s < points.length; s++) {
                        Point S = points[s];

                        if (isCollinear(P, Q, R, S))
                            segmentList.add(new LineSegment(P, S));

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
        return segments;
    }

    private void checkDuplicatedEntries(Point[] points) {
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++)
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicated entries in given points.");
    }
}
