import java.util.*;

public class FastCollinearPoints {
    private LineSegment[] segments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        Objects.requireNonNull(points);
        Arrays.stream(points).forEach(p -> Objects.requireNonNull(p));
        checkRepeatedPoints(points);
        Point[] pointsCopySO = Arrays.copyOf(points, points.length);
        Point[] pointsCopyNO = Arrays.copyOf(points, points.length);
        ArrayList<LineSegment> segmentsList = new ArrayList<LineSegment>();
        Arrays.sort(pointsCopyNO);
        for (int i = 0; i < pointsCopyNO.length; ++i) {
            Point origin = pointsCopyNO[i];
            Arrays.sort(pointsCopySO);
            Arrays.sort(pointsCopySO, origin.slopeOrder());
            int count = 1;
            Point lineBeginning = null;
            for (int j = 0; j < pointsCopySO.length - 1; ++j) {
                if (pointsCopySO[j].slopeTo(origin) == pointsCopySO[j + 1].slopeTo(origin)) {
                    count++;
                    if (count == 2) {
                        lineBeginning = pointsCopySO[j];
                        count++;
                    } else if (count >= 4 && j + 1 == pointsCopySO.length - 1) {
                        if (lineBeginning.compareTo(origin) > 0) {
                            segmentsList.add(new LineSegment(origin, pointsCopySO[j + 1]));
                        }
                        count = 1;
                    }
                } else if (count >= 4) {
                    if (lineBeginning.compareTo(origin) > 0) {
                        segmentsList.add(new LineSegment(origin, pointsCopySO[j]));
                    }
                    count = 1;
                } else {
                    count = 1;
                }
            }
        }
        segments = segmentsList.toArray(new LineSegment[segmentsList.size()]);
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
    private static void sort(Point[] a, Comparator<Point> comparator) {
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