import java.util.*;

/**
 * Cheese.java. Solves UVA 1712. Given N holes and M slices, finds
 * the width of each slice so that each slice has the same weight
 * in O(NlogN + MN) time.
 *
 * Compilation: javac Cheese.java
 * Execution: java Cheese < test.txt
 */
public class Cheese {

    private static final double BLOCK_DIMENSION = 100 * 1000;  // 100 mm -> micrometers
    private static final double ERROR = 1000000;               // 10E-6 error

    /**
     * Runs the main program. See the README for details on how
     * to input data.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            int numHoles = in.nextInt();
            int numSlices = in.nextInt();

            // adds holes to the cheese
            BlockOfCheese block = new BlockOfCheese(numHoles);
            for (int i = 0; i < numHoles; i++) {
                double r = in.nextDouble();
                double x = in.nextDouble();
                double y = in.nextDouble();
                double z = in.nextDouble();
                block.addHole(x, y, z, r);
            }
            Arrays.sort(block.holes);

            // initializes the search
            double volume = block.blockVolume;
            double volumePerSlice = volume / (double) numSlices;
            double lastCutEnd = 0.0;

            // searchs for all of the slices
            for (int i = 1; i <= numSlices - 1; i++) {
                double cutHeight = nextSlice(block, lastCutEnd, volumePerSlice);
                double cutWidth = cutHeight - lastCutEnd;
                System.out.printf("%.9f\n", cutWidth / 1000.0);
                lastCutEnd = cutHeight;
            }
            System.out.printf("%.9f\n", (BLOCK_DIMENSION - lastCutEnd) / 1000.0);
        }
    }

    /**
     * Block of cheese class. Represents the current block of cheese.
     */
    private static class BlockOfCheese {
        public double sideLength;               // length of one side of the block
        public int currentHoles;                // current number of holes
        public double blockVolume;              // volume of the block (with holes)
        public Sphere[] holes;                  // list of holes in block

        /**
         * Construct a new block of cheese containing a certain
         * number of holes.
         * @param  int numHoles: The number of holes in the block.
         */
        public BlockOfCheese(int numHoles) {
            this.sideLength = BLOCK_DIMENSION;
            this.blockVolume = Math.pow(sideLength, 3);
            this.holes = new Sphere[numHoles];
            this.currentHoles = 0;
        }

        /**
         * Adds a new hole into the block of cheese.
         * @param  double x: x coordinate of the center of the sphere.
         * @param  double y: y coordinate of the center of the sphere.
         * @param  double z: z coordinate of the center of the sphere.
         * @param  double r: radius of the sphere.
         */
        public void addHole(double x, double y, double z, double r) {
            if (currentHoles == this.holes.length) return;
            this.holes[currentHoles] = new Sphere(x, y, z, r);
            this.blockVolume = this.blockVolume - this.holes[currentHoles].volume();
            currentHoles++;
        }

        /**
         * Finds the volume of a slice starting from one height to another.
         * @param   double start: The z coordinate of the bottom of the slice.
         * @param   double end: The z coordinate of the top of the slice.
         * @return  double: The volume of the slice of cheese.
         */
        public double volumeOfSlice(double start, double end) {
            double volume = sideLength * sideLength * (end - start);
            for (int i = 0; i < holes.length; i++) {
                Sphere hole = holes[i];
                if (hole.z + hole.r < start) continue;
                if (hole.z - hole.r > end) break;
                volume = volume - hole.volumeBetween(start, end);
            }
            return volume;
        }

    }

    /**
     * Sphere class. Represents a sphere. In this problem, represents
     * a hole in the block of cheese.
     */
    private static class Sphere implements Comparable<Sphere> {
        public double x, y, z, r;

        /**
         * Constructs a new sphere, given the coordinates of
         * the center of the sphere and a radius.
         */
        public Sphere(double x, double y, double z, double r) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }

        /**
         * Returns the whole volume of the sphere.
         * @return double: The volume of the sphere.
         */
        public double volume() {
            return (4.0 / 3.0) * Math.PI * Math.pow(r, 3);
        }

        /**
         * Returns the volume of a section of the sphere.
         * @param   double h: The height of the section of the sphere.
         * @return  double: The volume of the section.
         */
        public double capVolume(double h) {
            return (1.0 / 3.0) * Math.PI * Math.pow(h, 2) * (3 * r - h);
        }

        /**
         * Finds the volume of a sphere given the starting coordinate
         * and ending coordinate of a slice.
         * @param   double start: The z coordinate of the bottom of a slice.
         * @param   double end: The z coordinate of the top of a slice.
         * @return  double: The volume of the sphere between the two planes.
         */
        public double volumeBetween(double start, double end) {
            double bottomPoint = z - r;
            double topPoint = z + r;

            if (start <= bottomPoint && topPoint <= end) {          // contain everything
                return volume();
            } else if (start <= bottomPoint && topPoint > end) {    // contain bottom only
                return capVolume(end - bottomPoint);
            } else if (start > bottomPoint && topPoint <= end) {    // contain top only
                return capVolume(topPoint - start);
            } else {                                                // contain middle
                return volume() - capVolume(topPoint - end) - capVolume(start - bottomPoint);
            }
        }

        /**
         * Custom comparator, sorts spheres by their lowest point,
         * where the sphere with a lower lowest point appears before
         * a sphere with a higher one.
         */
        public int compareTo(Sphere that) {
            double thisBottom = this.z - this.r;
            double thatBottom = that.z - that.r;

            if (thisBottom > thatBottom) return 1;
            else if (thisBottom < thatBottom) return -1;
            else return 0;
        }
    }

    /**
     * Searches for the next slice given a block of cheese,
     * a starting z coordinate, and a target volume. Uses
     * a modified binary search.
     * @param   BlockOfCheese block: The block of cheese.
     * @param   double start: The starting z coordinate.
     * @param   double targetVolume: How much volume we want in the slice.
     * @return  double: The height to cut next at.
     */
    private static double nextSlice(BlockOfCheese block, double start, double targetVolume) {
        double lo = start, hi = BLOCK_DIMENSION;
        while (hi > lo) {
            double mid = (hi + lo) / 2.0;
            double sliceVolume = block.volumeOfSlice(start, mid);

            if (Math.abs(targetVolume - sliceVolume) < ERROR) {
                return mid;
            } else if (sliceVolume > targetVolume) {
                hi = mid;
            } else {
                lo = mid;
            }
        }

        return -1;
    }



}
