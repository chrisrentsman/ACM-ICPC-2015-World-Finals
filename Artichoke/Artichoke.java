import java.util.Scanner;

/**
 * Catering.java. Solves UVA 1709. Given N times
 * to check, finds the maximum decline in prices
 * in O(N) time.
 *
 * Compilation: javac Artichoke.java
 * Execution: java Artichoke < input.txt
 */
public class Artichoke {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int p = in.nextInt();
            int a = in.nextInt();
            int b = in.nextInt();
            int c = in.nextInt();
            int d = in.nextInt();
            int n = in.nextInt();

            double maxPrice = 0;
            double maxDecline = 0;
            for (int k = 1; k <= n; k++) {
                double current = p * (Math.sin((a * k + b) % (2 * Math.PI))
                    + Math.cos((c * k + d) % (2 * Math.PI)) + 2);
                maxPrice = Math.max(maxPrice, current);
                maxDecline = Math.max(maxDecline, maxPrice - current);
            }

            System.out.printf("%.6f\n", maxDecline);
        }
    }

}
