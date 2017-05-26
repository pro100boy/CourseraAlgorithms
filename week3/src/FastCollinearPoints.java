import java.util.*;

public class FastCollinearPoints {
    private LineSegment[] segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        Objects.requireNonNull(points);
        Arrays.stream(points).forEach(p -> Objects.requireNonNull(p));
        checkRepeatedPoints(points);
        int pointsCount = points.length;
        List<LineSegment> segmentList = new ArrayList<>();
        // Go each point p.
        for (int p = 0; p < pointsCount; p++) {
            // Sort the points according to the slopes they makes with p.
            sort(points, points[p].slopeOrder());
            // Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p
            List<Point> collinearPoints = new ArrayList<>(pointsCount);
            for (int q = 0; q < pointsCount - 1; q++) {
                if (p == q) {
                    continue;
                }
                if (collinearPoints.isEmpty()) {
                    collinearPoints.add(points[q]);
                } else if (points[p].slopeTo(points[q - 1]) == points[p].slopeTo(points[q])) {
                    collinearPoints.add(points[q]);
                } else if (collinearPoints.size() > 2) {
                    collinearPoints.add(points[p]);
                    Collections.sort(collinearPoints);
                    segmentList.add(new LineSegment(Collections.min(collinearPoints), Collections.max(collinearPoints)));
                    break;
                } else {
                    collinearPoints.clear();
                    collinearPoints.add(points[q]);
                }
            }
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

    /***********************************************************************
     *  Bottom-Up merge sorting functions
     ***********************************************************************/

    // stably merge a[lo..m] with a[m+1..hi] using aux[lo..hi]
    private static void merge(Point[] a, Point[] aux, int lo, int m, int hi, Comparator<Point> comparator) {
        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }
        // merge back to a[]
        int i = lo, j = m + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > m) a[k] = aux[j++];
            else if (j > hi) a[k] = aux[i++];
            else if (less(comparator, aux[j], aux[i])) a[k] = aux[j++];
            else a[k] = aux[i++];
        }
    }

    // bottom-up mergesort
    public static void sort(Point[] a, Comparator<Point> comparator) {
        int N = a.length;
        Point[] aux = new Point[N];
        for (int n = 1; n < N; n = n + n) {
            for (int i = 0; i < N - n; i += n + n) {
                int lo = i;
                int m = i + n - 1;
                int hi = Math.min(i + n + n - 1, N - 1);
                merge(a, aux, lo, m, hi, comparator);
            }
        }
    }

    /***********************************************************************
     *  Helper sorting functions
     ***********************************************************************/

    // is v < w ?
    private static boolean less(Comparator<Point> comparator, Point v, Point w) {
        return comparator.compare(v, w) < 0;
    }

    private void checkRepeatedPoints(Point[] points) {
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++)
            if (points[i].compareTo(points[i + 1]) == 0)
                throw new IllegalArgumentException("Duplicated entries in given points.");
    }
}
