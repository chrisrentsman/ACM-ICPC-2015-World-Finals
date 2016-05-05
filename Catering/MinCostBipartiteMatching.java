import java.util.PriorityQueue;

public class MinCostBipartiteMatching {
    private static int INFINITY = Integer.MAX_VALUE;

    public static void main(String[] args) {
        int[][] g = {
            {INFINITY, 0, 0, INFINITY, INFINITY, INFINITY},
            {INFINITY, INFINITY, INFINITY, 10, 5, INFINITY},
            {INFINITY, INFINITY, INFINITY, 8, 4, INFINITY},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, 0},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, 0},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY}
        };

        int[][] g2 = {
            {INFINITY, 0, 0, 0, INFINITY, INFINITY, INFINITY, INFINITY},
            {INFINITY, INFINITY, INFINITY, INFINITY, 1, 3, 5, INFINITY},
            {INFINITY, INFINITY, INFINITY, INFINITY, 5, 7, 6, INFINITY},
            {INFINITY, INFINITY, INFINITY, INFINITY, 5, 8, 8, INFINITY},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, 0},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, 0},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, 0},
            {INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY, INFINITY}
        };

        System.out.println(getMinCostMatching(g2));
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

        for (int numMatches = 0; numMatches < reqMatches; numMatches++) {
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
