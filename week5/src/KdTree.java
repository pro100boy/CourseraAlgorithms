import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

/**
 * A mutable data type that uses a 2d-tree to represent a set of points inside a unit square. The unit square has
 * vertices at (0,0), (0,1), (1,0) and (1,1).
 * <p>
 * This API supports operation:
 * - nearest() in time proportional to log(N) in the typical case or N in the worst-case (even if tree is balanced).
 * - range() in time proportional R + log(N) in the typical case or R + Math.sqrt(N) in the worst case (assuming the
 * tree is balanced). Note, R is number of points that match.
 *
 * @author Galushkin Pavel
 */
public class KdTree {
    private Node root;  // Root node of the 2d-tree, which is considered to be at depth 0.
    private int size;   // Number of nodes in the 2d-tree.

    /**
     * Initialise KdTree object.
     */
    public KdTree() {
        root = null;
        size = 0;
    }

    /**
     * Is there zero points in the tree?
     *
     * @return true if zero points, false if there do exist points.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Number of points in the tree.
     *
     * @return total number of points.
     */
    public int size() {
        return size;
    }

    /**
     * Add the point p to the tree (if it is not already in the tree).
     *
     * @param p point to add.
     */
    public void insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        } else if (!inBounds(p)) {
            throw new IllegalArgumentException("Point must be inside the unit square.");
        }
        root = insert(null, root, p, 0);
    }

    /**
     * Does the tree have a point p?
     *
     * @param p point to check for in the tree.
     * @return true if p is in the tree, false otherwise.
     */
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        } else if (!inBounds(p)) {
            throw new IllegalArgumentException("Point must be inside the unit square.");
        }
        return contains(root, p, 0);
    }

    /**
     * Find the closest point to the query point p. Query point p can be outside the unit square.
     * Null is returned if the set is empty.
     *
     * @param p query point.
     * @return closest point to query point p.
     */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new NullPointerException();
        }
        return nearest(root, p, null, 0);
    }

    /**
     * Find all points contained inside the query rectangle. Query rectangle can be outside the unit square.
     *
     * @param rect query rectangle.
     * @return iterable of points inside the query rectangle.
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new NullPointerException();
        }
        ArrayList<Point2D> points = new ArrayList<>();
        range(root, rect, points);
        return points;
    }

    /**
     * Draw all points in the set to standard draw.
     */
    public void draw() {
        draw(root, 0);
    }

    /**
     * Does this depth in the tree correspond to a vertical split? Depth is always >= 0.
     *
     * @param depth in the tree.
     * @return true if this depth relates to a vertical split, otherwise false.
     */
    private boolean isVertical(int depth) {
        return (depth % 2 == 0);
    }

    /**
     * Does this depth in the tree correspond to a horizontal split? Depth is always >= 0.
     *
     * @param depth in the tree.
     * @return true if this depth relates to a horizontal split, otherwise false.
     */
    private boolean isHorizontal(int depth) {
        //Uses an expression like 'x % 2 == 1' to check whether an integer is odd, but this won't work for negative integers. Instead, use an expression like 'x % 2 != 0'
        return (depth % 2 != 0);
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

    /**
     * Node class to be used in the tree.
     * <p>
     * Potential Memory Saving Optimization:
     * Don't need to explicitly store a RectHV in each 2d-tree node.
     */
    private static class Node {
        private final Point2D p;      // the point
        private final RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;              // the left/bottom subtree
        private Node rt;              // the right/top subtree

        /**
         * Node must be instantiated with at least a point and rectangle. lb and rt are usually specified as the tree
         * grows.
         *
         * @param p    point to be held by the node.
         * @param rect rectangle corresponding to new node.
         */
        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
        }
    }

    /**
     * Create a rectangle for point p/new child node that is to be inserted into the tree.
     *
     * @param parent node to insert onto.
     * @param p      point to be inserted.
     * @param depth  depth in the tree of the supposed new child node.
     * @return rectangle corresponding to point p/new child node.
     */
    private RectHV createRectForNewNode(Node parent, Point2D p, int depth) {
        int cmp;
        RectHV rect = new RectHV(0.0, 0.0, 1.0, 1.0); // Default unit square node for root node.
        if (parent != null) {
            if (isVertical(depth)) {  // new child node will be vertically split, so parent must be horizontally split
                cmp = Double.compare(p.y(), parent.p.y());

                if (cmp < 0) {
                    rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.rect.xmax(), parent.p.y());
                } else if (cmp > 0) {
                    rect = new RectHV(parent.rect.xmin(), parent.p.y(), parent.rect.xmax(), parent.rect.ymax());
                }
            } else {  // new child node will be horizontally split, so parent must be vertically split
                cmp = Double.compare(p.x(), parent.p.x());

                if (cmp < 0) {
                    rect = new RectHV(parent.rect.xmin(), parent.rect.ymin(), parent.p.x(), parent.rect.ymax());
                } else if (cmp > 0) {
                    rect = new RectHV(parent.p.x(), parent.rect.ymin(), parent.rect.xmax(), parent.rect.ymax());
                }
            }
        }
        return rect;
    }

    /**
     * A recursive helper method to add the point p to the tree.
     *
     * @param parent node to node n.
     * @param n      current node.
     * @param p      point to add.
     * @param depth  of node n in the tree.
     * @return Node.
     */
    private Node insert(Node parent, Node n, Point2D p, int depth) {
        if (n == null) {
            size++;
            return new Node(p, createRectForNewNode(parent, p, depth));
        }

        int cmpX = Double.compare(p.x(), n.p.x());
        int cmpY = Double.compare(p.y(), n.p.y());
        if ((cmpX == 0) && (cmpY == 0)) {
            return n;
        }

        int cmp;
        if (isVertical(depth)) {
            cmp = cmpX;
        } else {  // n is a horizontal splitting node
            cmp = cmpY;
        }

        if (cmp < 0) {
            n.lb = insert(n, n.lb, p, depth + 1);
        } else {
            n.rt = insert(n, n.rt, p, depth + 1);
        }

        return n;
    }

    /**
     * A recursive helper method to check if the tree has a point p.
     *
     * @param n current node.
     * @param p point to check for in the tree.
     * @return true if p is in the tree, false otherwise.
     */
    private boolean contains(Node n, Point2D p, int depth) {
        if (n == null) {
            return false;
        }  // Haven't found the point.

        int cmpX = Double.compare(p.x(), n.p.x());
        int cmpY = Double.compare(p.y(), n.p.y());
        if ((cmpX == 0) && (cmpY == 0)) {
            return true;
        }  // Found the point.

        int cmp;
        if (isVertical(depth)) {
            cmp = cmpX;
        } else {  // n is a horizontal splitting node
            cmp = cmpY;
        }

        if (cmp < 0) {
            return contains(n.lb, p, depth + 1);
        } else {
            return contains(n.rt, p, depth + 1);
        }
    }

    /**
     * A recursive helper method to draw all points in the set to standard draw. A splitting line is also displayed for
     * every point.
     *
     * @param n     current node.
     * @param depth of current node n in the tree.
     */
    private void draw(Node n, int depth) {
        if (n == null) {
            return;
        }

        draw(n.lb, depth + 1);
        draw(n.rt, depth + 1);

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        n.p.draw();
        if (isVertical(depth)) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();
            StdDraw.line(n.p.x(), n.rect.ymin(), n.p.x(), n.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();
            StdDraw.line(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.p.y());
        }
    }

    /**
     * A recursive helper method to find all points contained inside the query rectangle. Query rectangle can be outside
     * the unit square.
     * <p>
     * Method:
     * If the query rectangle does not intersect the rectangle corresponding to a node, there is no need to explore that
     * node (or its subtrees). A subtree is searched only if it might contain a point contained in the query rectangle.
     * <p>
     * Potential Future Optimization:
     * Instead of checking whether the query rectangle intersects the rectangle corresponding to a node, it suffices to
     * check only whether the query rectangle intersects the splitting line segment: if it does, then recursively search
     * both subtrees; otherwise, recursively search the one subtree where points intersecting the query rectangle could
     * be.
     *
     * @param n      current node.
     * @param query  rectangle.
     * @param points inside the query rectangle.
     */
    private void range(Node n, RectHV query, ArrayList<Point2D> points) {
        if (n == null || !query.intersects(n.rect)) {
            return;
        }
        if (query.contains(n.p)) {
            points.add(n.p);
        }
        range(n.lb, query, points);
        range(n.rt, query, points);
    }

    /**
     * A recursive helper method to find the nearest point to the query point p. Query point p can be outside the unit
     * square.
     * <p>
     * Method:
     * 1. Choose the subtree that is on the same side of the splitting line as the query point. This should
     * prune out most other candidate subtrees needed to be explored.
     * 2. Check out other points that may be closer. If the closest point discovered in 1 so far is closer than the
     * distance between the query point and the rectangle corresponding to a node, there is no need to explore that
     * node (or its subtrees).
     *
     * @param n       current node.
     * @param query   point.
     * @param nearest current nearest found point to query point.
     * @param depth   current depth of node n in the tree.
     * @return current nearest point to query point.
     */
    private Point2D nearest(Node n, Point2D query, Point2D nearest, int depth) {
        if (n == null) {
            return nearest;
        }
        if (nearest == null || (query.distanceSquaredTo(n.p) < query.distanceSquaredTo(nearest))) {
            nearest = n.p;
        }

        int cmp;
        if (isVertical(depth)) {
            cmp = Double.compare(query.x(), n.p.x());
        } else {  // n is a horizontal splitting node
            cmp = Double.compare(query.y(), n.p.y());
        }

        if (cmp < 0) {
            nearest = nearest(n.lb, query, nearest, depth + 1);
            if ((n.rt != null) && n.rt.rect.distanceSquaredTo(query) < query.distanceSquaredTo(nearest)) {
                nearest = nearest(n.rt, query, nearest, depth + 1);
            }
        } else {
            nearest = nearest(n.rt, query, nearest, depth + 1);
            if (n.lb != null && n.lb.rect.distanceSquaredTo(query) < query.distanceSquaredTo(nearest)) {
                nearest = nearest(n.lb, query, nearest, depth + 1);
            }
        }

        return nearest;
    }
}