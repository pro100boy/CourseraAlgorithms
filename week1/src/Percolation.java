import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Percolation {

    private WeightedQuickUnionUF ufUp;
    private WeightedQuickUnionUF ufBoth;
    private int count;
    private boolean[][] open;
    private int[] dx = new int[] {-1, 0, 1, 0};
    private int[] dy = new int[] {0, -1, 0, 1};
    private int n;

    public Percolation(int n) {						// create n-by-n grid, with all sites blocked
        this.n = n;
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        count = 0;
        open = new boolean[n][n];
        ufUp = new WeightedQuickUnionUF(n*n);
        ufBoth = new WeightedQuickUnionUF(n*n);
        for (int i = 1; i < n; i++) {
            ufUp.union(i-1, i);
            ufBoth.union(i-1, i);
            ufBoth.union(n*(n-1)+i-1, n*(n-1)+i);
        }
    }

    public void open(int row, int col) {    		// open site (row, col) if it is not open already
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IndexOutOfBoundsException();
        }
        if (!open[row-1][col-1]) {
            count++;
            open[row-1][col-1] = true;
            for (int i = 0; i < 4; i++) {
                int x = row - 1 + dx[i];
                int y = col - 1 + dy[i];
                if (x >= 0 && x < n && y >= 0 && y < n) {
                    if (open[x][y]) {
                        ufUp.union((row-1)*n+col-1, x*n+y);
                        ufBoth.union((row-1)*n+col-1, x*n+y);
                    }
                }
            }
        }
    }

    public boolean isOpen(int row, int col) {  		// is site (row, col) open?
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IndexOutOfBoundsException();
        }
        return open[row-1][col-1];
    }

    public boolean isFull(int row, int col) {  		// is site (row, col) full?
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IndexOutOfBoundsException();
        }
        if (n < 2) {
            return open[row-1][col-1];
        } else {
            return open[row-1][col-1] && ufUp.connected((row-1)*n+col-1, 0);
        }
    }

    public int numberOfOpenSites() {       			// number of open sites
        return count;
    }

    public boolean percolates() {              		// does the system percolate?
        if (n < 2) {
            return open[0][0];
        } else {
            return ufBoth.connected(0, n*n-1);
        }
    }

    public static void main(String[] args) {  		// test client (optional)
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("percolation/input8.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<int[]> nums = new ArrayList<>();
        if (scanner.hasNextInt()) {
            nums.add(new int[] {scanner.nextInt()});
        }
        while (scanner.hasNextInt()) {
            int num1 = scanner.nextInt();
            if (scanner.hasNextInt()) {
                nums.add(new int[] {num1, scanner.nextInt()});
            }
        }
        int n = nums.get(0)[0];
        Percolation percolation = new Percolation(n);
        for (int i = 1; i < nums.size(); i++) {
            percolation.open(nums.get(i)[0], nums.get(i)[1]);
        }
        StdOut.print(percolation.numberOfOpenSites()+" ");
        StdOut.print(percolation.percolates());
    }
}