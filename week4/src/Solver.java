import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Created by Paul Galushkin on 01/06/2017.
 */
public class Solver {
    private Stack<Board> boards = null;
    private boolean solvable = false;
    private int moves = -1;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new NullPointerException("Null board provided.");
        }

        // Initial board
        MinPQ<Node> nodesPQ = new MinPQ<>();
        nodesPQ.insert(new Node(initial));
        Node searchNode = nodesPQ.delMin();

        // Twin board
        MinPQ<Node> twinNodesPQ = new MinPQ<>();
        twinNodesPQ.insert(new Node(initial.twin()));
        Node twinSearchNode = twinNodesPQ.delMin();

        while (!(searchNode.board.isGoal() || twinSearchNode.board.isGoal())) {
            searchNode = searchStep(nodesPQ, searchNode);
            twinSearchNode = searchStep(twinNodesPQ, twinSearchNode);
        }

        if (searchNode.board.isGoal()) {
            solvable = true;
            moves = searchNode.moves;

            boards = new Stack<>();
            while (searchNode.previous != null) {  // Initial node has previous of null
                boards.push(searchNode.board);
                searchNode = searchNode.previous;
            }
            boards.push(initial);
        }
    }

    private static class Node implements Comparable<Node> {
        private final Board board;
        private final int moves;
        private final Node previous;

        public Node(Board board) {
            this.board = board;
            this.moves = 0;
            this.previous = null;
        }

        public Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
        }

        public int priority() {
            return board.manhattan() + moves;
        }

        @Override
        public int compareTo(Node o) {
            return this.priority() - o.priority();
        }
    }

    private Node searchStep(MinPQ<Node> nodesPQ, Node searchNode) {
        for (Board neighbor : searchNode.board.neighbors()) {
            if ((searchNode.previous != null) && neighbor.equals(searchNode.previous.board)) {
                continue;
            }
            nodesPQ.insert(new Node(neighbor, searchNode.moves + 1, searchNode));
        }
        return nodesPQ.delMin();
    }

    // is the initial board solvable?
    public boolean isSolvable() { return solvable; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() { return moves; }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() { return boards; }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}