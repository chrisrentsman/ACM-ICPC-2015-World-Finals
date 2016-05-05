import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Arrays;

public class Catering {
    private static int INFINITY = Integer.MAX_VALUE;
    private static int M = 1000001;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int n = in.nextInt();
            int k = in.nextInt();
            int numNodes = (2 * (n + k)) + 2;
            int[][] g = new int[numNodes][numNodes];

            for (int i = 0; i < g.length; i++) {
                Arrays.fill(g[i], INFINITY);
            }

            int[] teamValues = new int[n];
            for (int i = 0; i < teamValues.length; i++) {
                teamValues[i] = in.nextInt();
            }

            for (int u = 1; u < k + 1; u++) {
                for (int i = 0; i < n; i++) {
                    g[u][i + n + (2 * k) + 1] = teamValues[i];
                }
            }

            for (int u = 1; u < n; u++) {
                for (int v = (n + 2 * k + u + 1); v < numNodes - 1; v++) {
                    g[u + k][v] = in.nextInt();
                }
            }

            for (int i = 1; i < n + k + 1; i++) {
                g[0][i] = 0;
            }

            for (int i = n + k + 1; i < numNodes - 1; i++) {
                g[i][numNodes - 1] = 0;
            }

            System.out.println(getMinCostMatching(g));
        }
    }

    public static int getMinCostMatching(int[][] g) {
        int[][] gMatch = copy2DArray(g);
        int numNodes = gMatch.length;
        int[] p = new int[numNodes];
        int source = 0;
        int sink = g.length - 1;
        int leftStart = 1;
        int reqMatches = (numNodes - 2) / 2;
        int rightStart = reqMatches + 1;

        while (true) {
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

            if (distTo[sink] == INFINITY) break;

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
        for (int i = leftStart; i < rightStart; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (g[i][j] != INFINITY && gMatch[j][i] != INFINITY) {
                    minCost += g[i][j];
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
            if (this.cost < that.cost) return -1;
            else if (this.cost > that.cost) return 1;
            else return 0;
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
