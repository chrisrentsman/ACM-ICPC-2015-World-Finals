import java.util.*;
import java.io.*;

/**
 * Ship.java. Solves UVA 1717. Given N lanes and
 * M ships per lane, finds the maximum amount of time
 * a ferry has to cross the channel in O() time.
 *
 * Compilation: javac Ship.java
 * Execution: java Ship < input.txt
 */
public class Ship {

    private static final String EAST = "E";             // input string representing east

    /**
     * TimeInterval class. Represents time intervals. In this problem, used
     * to represent times where the ferry cannot cross.
     */
    private static class TimeInterval implements Comparable<TimeInterval> {
        public double start;                    // start of the interval
        public double end;                      // end of the interval

        /**
         * Constructs a new Time Inteval.
         * @param   double start: Starting time of the interval.
         * @param   double end: Ending time of the interval.
         */
        public TimeInterval(double start, double end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Custom comparator. Sorts time intervals by start time,
         * with the earlier starting interval appearing earlier.
         */
        public int compareTo(TimeInterval that) {
            if (this.start > that.start) return 1;
            else if (this.start < that.start) return -1;
            else return 0;
        }
    }

    /**
     * ShipInterval class. Represents a ship in a lane.
     */
    private static class ShipInterval {
        public int length;              // length of the ship
        public int startPosition;       // starting position of the ship

        /**
         * Constructs a new ship interval.
         */
        public ShipInterval(int length, int startPosition) {
            this.length = length;
            this.startPosition = startPosition;
        }
    }

    /**
     * Runs the main program. See the README for details on how
     * to input data.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {

            // initializes the problem
            int numLanes = in.nextInt();
            int laneWidth = in.nextInt();
            int shipSpeed = in.nextInt();
            int ferrySpeed = in.nextInt();
            int earliestStart = in.nextInt();
            int latestStart = in.nextInt();

            // finds which times, per line, per ship are blocked out
            LinkedList<TimeInterval> blockedList = new LinkedList<TimeInterval>();
            for (int currentLane = 1; currentLane <= numLanes; currentLane++) {
                String directionString = in.next();
                int direction = directionString.equals(EAST) ? -1 : 1;
                int numShips = in.nextInt();
                ShipInterval[] laneShips = new ShipInterval[numShips];

                // creates the ships in the lane
                for (int i = 0; i < laneShips.length; i++) {
                    int length = in.nextInt();
                    int startPosition = in.nextInt() * direction;
                    laneShips[i] = new ShipInterval(length, startPosition);
                }

                // checks to see, for each ship, when it blocks the ferry's path
                for (int i = 0; i < laneShips.length; i++) {
                    ShipInterval ship = laneShips[i];

                    double blockStart = getBlockStart(ship, laneWidth, currentLane, ferrySpeed, shipSpeed);
                    double blockEnd = getBlockEnd(blockStart, ship, laneWidth, ferrySpeed, shipSpeed);

                    if (blockStart <= latestStart && blockEnd >= earliestStart) {
                        blockStart = Math.max(blockStart, earliestStart);
                        blockEnd = Math.min(blockEnd, latestStart);
                        blockedList.addFirst(new TimeInterval(blockStart, blockEnd));
                    }
                }
            }

            // initializes the search for the maximum valid time interval
            TimeInterval[] blocked = blockedList.toArray(new TimeInterval[blockedList.size()]);
            Arrays.sort(blocked);
            double maxInterval = 0;

            // finds the maximum valid interval
            if (blocked.length > 0) {
                maxInterval = blocked[0].start - earliestStart;
                double currentTime = blocked[0].end;
                for (int block = 1; block < blocked.length; block++) {
                    TimeInterval currentBlock = blocked[block];
                    if (currentBlock.start > currentTime) {
                        double currentBlockLength = currentBlock.start - currentTime;
                        maxInterval = Math.max(maxInterval, currentBlockLength);
                        currentTime = currentBlock.end;
                    } else {
                        currentTime = Math.max(currentTime, blocked[block].end);
                    }
                }
                maxInterval = Math.max(maxInterval, latestStart - currentTime);
            } else {
                maxInterval = (double) latestStart - earliestStart;
            }

            System.out.printf("%.8f\n", maxInterval);
        }
    }

    private static double getBlockStart(ShipInterval ship, int laneWidth, int currentLane, int ferrySpeed, int shipSpeed) {
        return ((double) ship.startPosition / (double) shipSpeed)
                - ((double) (currentLane * laneWidth) / (double) ferrySpeed);
    }

    private static double getBlockEnd(double blockStart, ShipInterval ship, int laneWidth, int ferrySpeed, int shipSpeed) {
        return blockStart
               + ((double) ship.length / (double) shipSpeed)
               + ((double) laneWidth / (double) ferrySpeed);
    }

}
