import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Arrays;

public class Catering {
    private static int INFINITY = Integer.MAX_VALUE;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int n = in.nextInt();
            int k = in.nextInt();
            int numNodes = 1 + k + (2 * n);
            int leftStart = 1;
            int rightStart = k + n;
            int[][] g = new int[numNodes][numNodes];

            for (int i = 0; i < g.length; i++) {
                Arrays.fill(g[i], INFINITY);
            }

            int[] teamValues = new int[n];
            for (int i = 0; i < teamValues.length; i++) {
                teamValues[i] = in.nextInt();
            }

            for (int u = leftStart; u <= k ; u++) {
                for (int i = 0; i < n; i++) {
                    g[u][k + n + i] = teamValues[i];
                }
            }

            for (int u = leftStart + k; u < rightStart; u++) {
                for (int v = u + n; v < numNodes - 1; v++) {
                    g[u][v] = in.nextInt();
                }
            }

            for (int i = leftStart; i < rightStart; i++) {
                g[0][i] = 0;
            }

            for (int i = rightStart; i < numNodes - 1; i++) {
                g[i][numNodes - 1] = 0;
            }

            System.out.println(getMinCostMatching(g, n, k));
        }
    }

    public static int getMinCostMatching(int[][] g, int requests, int numTeams) {
        int[][] gMatch = copy2DArray(g);
        int numNodes = gMatch.length;
        int[] p = new int[numNodes];
        int source = 0;
        int sink = g.length - 1;
        int leftStart = 1;
        int rightStart = numTeams + requests;

        for (int numMatches = 0; numMatches < requests; numMatches++) {
            int[] distTo = new int[numNodes];
            int[] parent = new int[numNodes];
            boolean[] visited = new boolean[numNodes];
            PriorityQueue<Node> pq = new PriorityQueue<Node>(numNodes);
            for (int i = 0; i < distTo.length; i++) {
                distTo[i] = INFINITY;
            }

            distTo[source] = 0;
            pq.add(new Node(source, 0));
            while (!pq.isEmpty()) {
                int u = pq.remove().vertex;
                visited[u] = true;
                for (int v = 0; v < numNodes; v++) {
                    if (!visited[v] && gMatch[u][v] != INFINITY) {
                        int currentDist = gMatch[u][v];
                        int newDistance = distTo[u] + currentDist;
                        if (distTo[v] >= newDistance) {
                            distTo[v] = Math.min(distTo[v], newDistance);
                            parent[v] = u;
                        }
                        pq.add(new Node(v, distTo[v]));
                    }
                }
            }

            int current = sink;
            while (current != source) {
                gMatch[current][parent[current]] = gMatch[parent[current]][current];
                gMatch[parent[current]][current] = INFINITY;
                current = parent[current];
            }

            for (int i = 0; i < p.length; i++) {
                p[i] = distTo[i] + p[i];
            }

            for (int u = leftStart; u < rightStart; u++) {
                for (int v = 0; v < numNodes; v++) {
                    if (gMatch[u][v] != INFINITY && v != source) {
                        gMatch[u][v] = p[u] + g[u][v] - p[v];
                    }
                }
            }

            for (int u = rightStart; u < numNodes - 1; u++) {
                for (int v = 0; v < numNodes; v++) {
                    if (gMatch[u][v] != INFINITY && v != sink) {
                        gMatch[u][v] = p[v] + g[v][u] - p[u];
                    }
                }
            }
        }

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

    private static class Node implements Comparable<Node> {
        public int vertex;
        public int cost;

        public Node(int v, int c) {
            this.vertex = v;
            this.cost = c;
        }

        public int compareTo(Node that) {
            return this.cost - that.cost;
        }
    }

    private static int[][] copy2DArray(int[][] a) {
        int[][] b = new int[a.length][a.length];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                b[i][j] = a[i][j];
            }
        }

        return b;
    }

    private static void printArray(int[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    private static void printArray(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            printArray(a[i]);
        }
    }


}
