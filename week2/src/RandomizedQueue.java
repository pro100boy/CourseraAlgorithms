import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] q;       // queue elements
    private int n;          // number of elements on queue
    private int first;      // index of first element of queue
    private int last;       // index of next available slot

    // construct an empty randomized queue
    public RandomizedQueue() {
        q = (Item[]) new Object[2];
        n = 0;
        first = 0;
        last = 0;
    }

    // is the queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the queue
    public int size() {
        return n;
    }

    // resize the underlying array
    private void resize(int capacity) {
        assert capacity >= n;
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            temp[i] = q[(first + i) % q.length];
        }
        q = temp;
        first = 0;
        last = n;
    }

    // add the item
    public void enqueue(Item item) {
        // double size of array if necessary and recopy to front of array
        if (item == null) throw new NullPointerException();
        if (n == q.length) resize(2 * q.length);   // double size of array if necessary
        q[last++] = item;                        // add item
        if (last == q.length) last = 0;          // wrap-around
        n++;
    }

    // remove and return a random item >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();
        int randomIndex = StdRandom.uniform(0, n);
        Item item = q[randomIndex];
        q[randomIndex] = q[n - 1];                            // to avoid loitering
        last = --n;
        q[last] = null;
        //n--;

        //first++;
        //if (first == q.length) first = 0;           // wrap-around
        // shrink size of array if necessary
        if (n > 0 && n == q.length / 4) resize(q.length / 2);
        return item;
    }

    // return (but do not remove) a random item
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();
        int randomIndex = StdRandom.uniform(0, n);
        return q[randomIndex];
    }

    /**
     * Returns an iterator that iterates over the items in the queue in arbitrary order.
     * @return an iterator that iterates over the items in the queue in arbitrary order
     */
    public Iterator<Item> iterator() {
        shuffleArray();
        return new ArrayIterator();
    }

    // Implementing Fisher–Yates shuffle
    private void shuffleArray()
    {
        for (int i = n - 1; i > 0; i--) {
            int j = StdRandom.uniform(0, i+1);
            Item tmp = q[j];
            q[j] = q[i];
            q[i] = tmp;
        }
    }
    // an iterator, doesn't implement remove() since it's optional
    private class ArrayIterator implements Iterator<Item> {
        private int i = 0;
        public boolean hasNext()  { return i < n;                               }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return q[i++];
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            switch (item) {
                case "-":
                    if (!queue.isEmpty()) {
                        StdOut.print(queue.dequeue() + " ");
                        StdOut.println();
                    }
                    break;
                case "+":
                    for (Iterator<String> iter = queue.iterator(); iter.hasNext(); )
                        StdOut.print(iter.next() + " ");
                    StdOut.println();
                    break;
                default:
                    queue.enqueue(item);
                    break;
            }

            //StdOut.println("(" + queue.size() + " left on queue)");
        }
    }
}