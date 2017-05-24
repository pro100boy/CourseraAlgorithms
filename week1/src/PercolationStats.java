import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double[] experiments;
    public PercolationStats(int n, int trials) {    // perform trials independent experiments on an n-by-n grid
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        experiments = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(n);
                int col = StdRandom.uniform(n);
                percolation.open(row+1, col+1);
            }
            experiments[i] = (double) percolation.numberOfOpenSites()/(n*n);
        }
    }

    public double mean() {                          // sample mean of percolation threshold
        double mean = StdStats.mean(experiments);
        return mean;
    }

    public double stddev() {                        // sample standard deviation of percolation threshold
        double dev = StdStats.stddev(experiments);
        return dev;
    }

    public double confidenceLo() {                  // low  endpoint of 95% confidence interval
        double mean = this.mean();
        double dev = this.stddev();
        double t = experiments.length;
        double low = mean - 1.96 * dev / Math.sqrt(t);
        return low;
    }

    public double confidenceHi() {                  // high endpoint of 95% confidence interval
        double mean = this.mean();
        double dev = this.stddev();
        double t = experiments.length;
        double high = mean + 1.96 * dev / Math.sqrt(t);
        return high;
    }

    public static void main(String[] args) {        // test client (described below)
        int input1 = StdIn.readInt();
        int input2 = StdIn.readInt();
        PercolationStats percolationStats = new PercolationStats(input1, input2);
        StdOut.printf("mean\t = %f\n", percolationStats.mean());
        StdOut.printf("stddev\t = %f\n", percolationStats.stddev());
        StdOut.printf("95%% confidence interval\t = [ %f, %f ]",
                percolationStats.confidenceLo(), percolationStats.confidenceHi());
        StdOut.print(percolationStats.confidenceLo()+" ");
        StdOut.print(percolationStats.confidenceLo()+" ");
        StdOut.print(percolationStats.mean()+" ");
        StdOut.print(percolationStats.confidenceLo()+" ");
    }
}