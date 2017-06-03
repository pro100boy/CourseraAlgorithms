import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Created by Paul Galushkin on 01/06/2017.
 */
public class Board {
    private final int[][] blocks;

    // construct a board from an n-by-n array of blocks
    // (where blocks[i][j] = block in row i, column j)
    // You may assume that the constructor receives an n-by-n array containing the n2 integers between 0 and n2 − 1,
    // where 0 represents the blank square.
    public Board(int[][] blocks) {
        this.blocks = new int[blocks.length][blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            this.blocks[i] = blocks[i].clone();
        }
    }

    private enum Direction {
        LEFT, RIGHT, UP, DOWN;

        public static Direction getDirection(int dirCode) {
            switch (dirCode) {
                case 0:
                    return LEFT;
                case 1:
                    return RIGHT;
                case 2:
                    return UP;
                case 3:
                    return DOWN;
                default:
                    throw new IllegalArgumentException("Direction code needs to be in {0, 1, 2, 3}.");
            }
        }

        private static int[] getNeighboringBlockIndex(int i, int j, Direction direction) {
            int neighborI;
            int neighborJ;
            switch (direction) {
                case LEFT:
                    neighborI = i;
                    neighborJ = j - 1;
                    break;
                case RIGHT:
                    neighborI = i;
                    neighborJ = j + 1;
                    break;
                case UP:
                    neighborI = i - 1;
                    neighborJ = j;
                    break;
                case DOWN:
                    neighborI = i + 1;
                    neighborJ = j;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid direction specified.");
            }

            return new int[] {neighborI, neighborJ};
        }
    }

    private boolean inBounds(int i, int j) {
        return (i >= 0 && i < dimension() && j >= 0 && j < dimension());
    }

    private int[][] copyBlocks() {
        int[][] copy = new int[dimension()][dimension()];
        for (int i = 0; i < dimension(); i++) {
            copy[i] = blocks[i].clone();
        }
        return copy;
    }

    private void swapBlocks(int[][] blocks, int i, int j, int otherI, int otherJ) {
        int otherBlock = blocks[otherI][otherJ];
        blocks[otherI][otherJ] = blocks[i][j];
        blocks[i][j] = otherBlock;
    }

    private int goalBlock(int i, int j) {
        if ((i == (dimension() - 1)) && (j == (dimension() - 1))) {  // Bottom-right block
            return 0;  // Blank block
        } else {  // All other blocks
            return dimension() * i + (j + 1);  // Non-blank block
        }
    }

    private int[] goalBlockIndex(int number) {
        int i;
        int j;
        if (number == 0) {
            i = dimension() - 1;
            j = dimension() - 1;
        } else {
            j = (number - 1) % dimension();
            i = (number - (j + 1)) / dimension();
        }
        return new int[] {i, j};
    }


    // board dimension n
    public int dimension() {
        return blocks.length;
    }

    // number of blocks out of place
    public int hamming() {
        int value = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != 0 && blocks[i][j] != goalBlock(i, j)) {  // Ignore blank block
                    value++;
                }
            }
        }
        return value;  // Ignore empty block.
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int value = 0;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                int[] goalBlock = goalBlockIndex(blocks[i][j]);
                if (blocks[i][j] != 0) {  // Ignore blank block
                    value += Math.abs(goalBlock[0] - i) + Math.abs(goalBlock[1] - j);
                }
            }
        }
        return value;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != goalBlock(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        int randI;
        int randJ;
        int[] randIJNeighbor;
        do {
            randI = StdRandom.uniform(dimension());
            randJ = StdRandom.uniform(dimension());
            Direction dir = Direction.getDirection(StdRandom.uniform(4));
            randIJNeighbor = Direction.getNeighboringBlockIndex(randI, randJ, dir);
        } while (!inBounds(randIJNeighbor[0], randIJNeighbor[1]) ||
                (blocks[randI][randJ] == 0) ||
                (blocks[randIJNeighbor[0]][randIJNeighbor[1]] == 0));

        int[][] twin = copyBlocks();
        swapBlocks(twin, randI, randJ, randIJNeighbor[0], randIJNeighbor[1]);

        return new Board(twin);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) { return true; }  // Same reference.
        if (y == null) { return false; }
        if (y.getClass() != this.getClass()) { return false; }

        Board other = (Board) y;
        if (this.dimension() != other.dimension()) { return false; }

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != other.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Stack<Board> neighbors = new Stack<>();

        int blankBlockI = 0;
        int blankBlockJ = 0;

        outer:
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] == 0) {
                    blankBlockI = i;
                    blankBlockJ = j;
                    break outer;
                }
            }
        }

        int[] neighboringBlockToBlank;
        for (Direction direction: Direction.values()) {
            neighboringBlockToBlank = Direction.getNeighboringBlockIndex(blankBlockI, blankBlockJ, direction);
            if (inBounds(neighboringBlockToBlank[0], neighboringBlockToBlank[1])) {
                int[][] neighbor = copyBlocks();
                swapBlocks(neighbor, blankBlockI, blankBlockJ, neighboringBlockToBlank[0], neighboringBlockToBlank[1]);
                neighbors.push(new Board(neighbor));
            }
        }
        return neighbors;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(dimension()).append("\n");
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                stringBuilder.append(String.format("%2d ", blocks[i][j]));
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}