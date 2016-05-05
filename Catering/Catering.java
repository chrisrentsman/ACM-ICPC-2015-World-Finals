import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Catering.java. Solves UVA 1711. Given N requests and K
 * teams, returns the minimum cost of servicing each request
 * in O(K(N^2)log(N+K)).
 *
 * Compilation: javac Catering.java
 * Execution: java Catering < input.txt
 */
public class Catering {
    private static int INFINITY = Integer.MAX_VALUE;

    /**
     * Runs the main program. See the README for details on how
     * to input data.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (in.hasNextInt()) {
            int n = in.nextInt();
            int k = in.nextInt();
            int numNodes = 1 + k + (2 * n);
            int leftStart = 1;
            int rightStart = k + n;
            int[][] g = new int[numNodes][numNodes];

            // initializes adjacency matrix
            for (int i = 0; i < g.length; i++) {
                Arrays.fill(g[i], INFINITY);
            }

            // builds up edges from location 1, per team
            int[] teamValues = new int[n];
            for (int i = 0; i < teamValues.length; i++) {
                teamValues[i] = in.nextInt();
            }

            for (int u = leftStart; u <= k ; u++) {
                for (int i = 0; i < n; i++) {
                    g[u][k + n + i] = teamValues[i];
                }
            }

            // builds up costs from location u to location v
            for (int u = leftStart + k; u < rightStart; u++) {
                for (int v = u + n; v < numNodes - 1; v++) {
                    g[u][v] = in.nextInt();
                }
            }

            // connects source and sink to the rest of the graph
            for (int i = leftStart; i < rightStart; i++) {
                g[0][i] = 0;
            }

            for (int i = rightStart; i < numNodes - 1; i++) {
                g[i][numNodes - 1] = 0;
            }

            System.out.println(getMinCostMatching(g, n, k));
        }
    }

    /**
     * Given an adjacency matrix for the Catering problem, finds the
     * minimum cost of servicing each request. Uses a minimum cost
     * bipartite matching algorithm with Dijkstra's single source
     * shortest paths.
     * @param   int[][] g: The adjacency matrix.
     * @param   int requests: The number of requests.
     * @param   int numTeams: The number of available teams.
     * @return  int: The minimum cost of servicing all requests.
     */
    public static int getMinCostMatching(int[][] g, int requests, int numTeams) {
        int[][] gMatch = copy2DArray(g);
        int numNodes = gMatch.length;
        int[] p = new int[numNodes];
        int source = 0;
        int sink = g.length - 1;
        int leftStart = 1;
        int rightStart = numTeams + requests;

        // runs until all requests are matched
        for (int numMatches = 0; numMatches < requests; numMatches++) {

            // dijkstra's single source shortest paths
            int[] distTo = new int[numNodes];
            int[] parent = new int[numNodes];
            IndexMinPQ<Integer> pq = new IndexMinPQ<Integer>(numNodes);
            for (int i = 0; i < distTo.length; i++) {
                distTo[i] = INFINITY;
            }

            distTo[source] = 0;
            pq.insert(source, distTo[source]);
            while (!pq.isEmpty()) {
                int u = pq.delMin();
                for (int v = 0; v < numNodes; v++) {
                    if (gMatch[u][v] != INFINITY) {
                        if (distTo[v] > distTo[u] + gMatch[u][v]) {
                            distTo[v] = distTo[u] + gMatch[u][v];
                            parent[v] = u;
                            if (pq.contains(v)) pq.decreaseKey(v, distTo[v]);
                            else pq.insert(v, distTo[v]);
                        }
                    }
                }
            }

            // reverses all nodes along the shortest path from source to sink
            int current = sink;
            while (current != source) {
                gMatch[current][parent[current]] = gMatch[parent[current]][current];
                gMatch[parent[current]][current] = INFINITY;
                current = parent[current];
            }

            // updates the prices of each vertex
            for (int i = 0; i < p.length; i++) {
                p[i] = distTo[i] + p[i];
            }

            // updates the edge weights of the edges going from left to right
            for (int u = leftStart; u < rightStart; u++) {
                for (int v = 0; v < numNodes; v++) {
                    if (gMatch[u][v] != INFINITY && v != source) {
                        gMatch[u][v] = p[u] + g[u][v] - p[v];
                    }
                }
            }

            // updates the edge weights of the edges going from right to left
            for (int u = rightStart; u < numNodes - 1; u++) {
                for (int v = 0; v < numNodes; v++) {
                    if (gMatch[u][v] != INFINITY && v != sink) {
                        gMatch[u][v] = p[v] + g[v][u] - p[u];
                    }
                }
            }
        }

        // adds up the cost of the edges in the matching
        int minCost = 0;
        for (int u = leftStart; u < rightStart; u++) {
            for (int v = 0; v < numNodes; v++) {
                if (g[u][v] != INFINITY && gMatch[v][u] != INFINITY) {
                    minCost += g[u][v];
                }
            }
        }

        return minCost;
    }

    /**
     * Copies a two dimensional array over.
     * @param    int[][] a: The array to copy over.
     * @request  int[][]: A copy of the array.
     */
    private static int[][] copy2DArray(int[][] a) {
        int[][] b = new int[a.length][a.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                b[i][j] = a[i][j];
            }
        }

        return b;
    }

    /**
     * Indexed Minimum Priority Queue. Courtesy of Princeton's algs4.
     * Stripped down to only functions needed for Dijkstra's SSSP.
     */
    private static class IndexMinPQ<Key extends Comparable<Key>> {
        private int maxN;
        private int N;
        private int[] pq;
        private int[] qp;
        private Key[] keys;

        public IndexMinPQ(int maxN) {
            this.maxN = maxN;
            keys = (Key[]) new Comparable[maxN + 1];
            pq   = new int[maxN + 1];
            qp   = new int[maxN + 1];
            for (int i = 0; i <= maxN; i++)
                qp[i] = -1;
        }

        public boolean isEmpty() {
            return N == 0;
        }

        public boolean contains(int i) {
            return qp[i] != -1;
        }

        public void insert(int i, Key key) {
            N++;
            qp[i] = N;
            pq[N] = i;
            keys[i] = key;
            swim(N);
        }

        public int delMin() {
            int min = pq[1];
            exch(1, N--);
            sink(1);
            qp[min] = -1;
            keys[min] = null;
            pq[N+1] = -1;
            return min;
        }

        public void decreaseKey(int i, Key key) {
            keys[i] = key;
            swim(qp[i]);
        }

        private boolean greater(int i, int j) {
            return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
        }

        private void exch(int i, int j) {
            int swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
            qp[pq[i]] = i;
            qp[pq[j]] = j;
        }

        private void swim(int k) {
            while (k > 1 && greater(k/2, k)) {
                exch(k, k/2);
                k = k/2;
            }
        }

        private void sink(int k) {
            while (2*k <= N) {
                int j = 2*k;
                if (j < N && greater(j, j+1)) j++;
                if (!greater(k, j)) break;
                exch(k, j);
                k = j;
            }
        }
    }

}
