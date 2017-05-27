import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * A fast method of examining points and checking whether they lie on the same line segment. Specifically, this is done
 * using stable sorting methods alongside a sliding window approach. Every line segment is collinear with at least four
 * or more points.
 */
public class FastCollinearPoints {

    // The minimum number of "other" collinear points needed for line segment to exist. "Origin" point excluded.
    private static final int MIN_OTHER_COLLINEAR_POINTS = 3;

    // Holds all line segments found
    private LineSegment[] lineSegments;

    /**
     * Instantiate by finding all line segments which are collinear with four or more points from a valid set of points.
     *
     * @param points array to find line segments in.
     */
    public FastCollinearPoints(Point[] points) {
        Objects.requireNonNull(points);
        /**
         * Checks for any null items in points array.
         * Useful for exception handling.
         */
        Arrays.stream(points).forEach(p -> Objects.requireNonNull(p));

        if (hasRepeatedPoints(points)) {
            throw new IllegalArgumentException("Repeated points are contained in the array.");
        }

        lineSegments = findLineSegments(points.clone());  // cloning to prevent mutation of user input.
    }

    /**
     * Finds all line segments which are collinear with four or more points. This is done using stable sorting methods
     * alongside a sliding window approach.
     *
     * @param points array to find line segments in.
     * @return array of all line segments found.
     */
    private LineSegment[] findLineSegments(Point[] points) {
        ArrayList<LineSegment> tempLineSegments = new ArrayList<>();
        Point[] others = points.clone();
        for (Point origin : points) {
            // These sorts are stable. Hence, relative ordering of points by position is maintained even after ordering
            // points by slope with origin.
            Arrays.sort(others);
            Arrays.sort(others, origin.slopeOrder());  // lowest element will always be the origin/degenerate point.

            int first = 1;  // First point considered collinear with origin (based on relative position).
            int end = first;  // End point of potential line segment.
            Comparator<Point> slopeOrder = origin.slopeOrder();

            // Compare two "other" points per iteration (current and previous); sliding window of length two.
            for (int i = first; i < others.length; i++) {
                // Are the slopes of the current and previous "other" points the same?
                // In the first iteration, slopeEqual == false since others[0] is always the origin/degenerate point,
                // which is always unique.
                boolean slopesEqual = (slopeOrder.compare(others[i - 1], others[i]) == 0);

                // Is the origin a start point of a potential line segment? Prevents sub-segments from being added.
                boolean originIsStart = (origin.compareTo(others[first]) < 0);
                if (slopesEqual) {
                    end++;  // Must increment before check.

                    // Valid line segment? This is a special case where the final point creates a valid line segment.
                    if ((i == (others.length - 1)) &&
                            ((1 + end - first) >= MIN_OTHER_COLLINEAR_POINTS) && originIsStart) {
                        tempLineSegments.add(new LineSegment(origin, others[others.length - 1]));
                    }
                } else {  // Different slopes
                    if (((1 + end - first) >= MIN_OTHER_COLLINEAR_POINTS) && originIsStart) {  // Valid line segment?
                        tempLineSegments.add(new LineSegment(origin, others[i - 1]));
                    }

                    // Reset to find next possible line segment.
                    first = i;
                    end = i;
                }
            }
        }
        return tempLineSegments.toArray(new LineSegment[tempLineSegments.size()]);
    }

    /**
     * Number of line segments that were found based on given points.
     *
     * @return number of line segments.
     */
    public int numberOfSegments() {
        return lineSegments.length;
    }

    /**
     * Line segments that were found based on given points.
     *
     * @return array of line segments found.
     */
    public LineSegment[] segments() {
        return lineSegments.clone();
    }

    /**
     * Check for repeated/duplicate points in points array.
     * Useful for exception handling.
     *
     * @param points array to check.
     * @return true if there are repeated points, false if not.
     */
    private boolean hasRepeatedPoints(Point[] points) {
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                // Same reference or same point in terms of (x, y) location?
                if (points[i] == points[j] || points[i].compareTo(points[j]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}