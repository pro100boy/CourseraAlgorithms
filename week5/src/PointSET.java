import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A mutable data type that utilises a red-black BST to represent a set of points inside a unit square. The unit square
 * has vertices at (0,0), (0,1), (1,0) and (1,1). Points can lie on the perimeter of the unit square.
 * <p>
 * This API supports nearest() and range() operations in time proportional to the number of points in the set.
 *
 * @author Galushkin Pavel
 */
public class PointSET {

    // The set of all points inside the unit square.
    private final TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        Objects.requireNonNull(p);
        if (!inBounds(p)) throw new IllegalArgumentException("Point must be inside the unit square.");

        points.add(p);
    }

    /**
     * Does the set have a point p?
     *
     * @param p point to check for in the set.
     * @return true if p is in the set, false otherwise.
     */
    public boolean contains(Point2D p) {
        Objects.requireNonNull(p);

        if (!inBounds(p)) {
            throw new IllegalArgumentException("Point must be inside the unit square.");
        }
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points)
            p.draw();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        Objects.requireNonNull(rect);
        return points.stream().filter(point2D -> rect.contains(point2D)).collect(Collectors.toList());
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    /**
     * Find the closest point to the query point p. Query point p can be outside the unit square.
     * Null is returned if the set is empty.
     *
     * @param p query point.
     * @return closest point to query point p.
     */
    public Point2D nearest(Point2D p) {
        Objects.requireNonNull(p);
        if (isEmpty()) return null;

        return points.stream().min(p.distanceToOrder()).get();
    }

    /**
     * Is point p inside the unit square defined with vertices at (0,0), (0,1), (1,0) and (1,1)? Point p can be on the
     * perimeter of the unit square.
     *
     * @param p point to check.
     * @return true if inside unit square, false otherwise.
     */
    private boolean inBounds(Point2D p) {
        return ((p.x() >= 0.0) && (p.x() <= 1.0) && (p.y() >= 0.0) && (p.y() <= 1.0));
    }

    public static void main(String[] args) {
        PointSET pointSET = new PointSET();
        pointSET.insert(new Point2D(0.206107, 0.095492));
        pointSET.insert(new Point2D(0.975528, 0.654508));
        pointSET.insert(new Point2D(0.024472, 0.345492));
        pointSET.insert(new Point2D(0.793893, 0.095492));
        pointSET.insert(new Point2D(0.793893, 0.904508));
        pointSET.insert(new Point2D(0.975528, 0.345492));
        pointSET.insert(new Point2D(0.206107, 0.904508));
        pointSET.insert(new Point2D(0.500000, 0.000000));
        pointSET.insert(new Point2D(0.024472, 0.654508));
        pointSET.insert(new Point2D(0.500000, 1.000000));

        Point2D p = new Point2D(0.900000, 0.300000);
        System.out.println(p);
        System.out.println(pointSET.nearest(p));
        System.out.println(pointSET.range(new RectHV(0,0,0.6, 0.7)));
    }
}
