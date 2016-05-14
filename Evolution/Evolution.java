import java.util.*;

/**
 * Evolution.java. Solves UVA 1713. Given N words, where the
 * longest word has K characters, finds whether or not two
 * evolutionary paths are viable in O(NlogN + NK) time.
 *
 * Compliation: javac Evolution.java
 * Execution: java Evolution < test.txt
 */
public class Evolution {

    /**
     * Runs the main program. See the README for details on how
     * to input data.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            int n = in.nextInt();
            String current = in.next();
            String[] ancestors = new String[n];

            // sorts ancestors by longest to shortest
            for (int i = 0; i < ancestors.length; i++)
                ancestors[i] = in.next();
            Arrays.sort(ancestors, new LongestToShortestString());

            // initializes the two evolutionary paths
            LinkedList<String> pathOne = new LinkedList<String>();
            LinkedList<String> pathTwo = new LinkedList<String>();
            LinkedList<String> commonAncestors = new LinkedList<String>();
            pathOne.push(current);
            pathTwo.push(current);

            // finds the evolutionary paths
            boolean impossible = false;
            for (int i = 0; i < ancestors.length; i++) {
                String ancestor = ancestors[i];
                boolean pathOneAncestor = isPathAncestor(pathOne, ancestor);
                boolean pathTwoAncestor = isPathAncestor(pathTwo, ancestor);

                if (pathOneAncestor && pathTwoAncestor && canHaveCommonAncestor(commonAncestors, ancestor)) {
                    commonAncestors.push(ancestor);
                } else if (pathOneAncestor) {
                    pathOne.push(ancestor);
                    addCommonAncestors(pathTwo, commonAncestors);
                } else if (pathTwoAncestor) {
                    pathTwo.push(ancestor);
                    addCommonAncestors(pathOne, commonAncestors);
                } else {
                    impossible = true;
                    break;
                }
            }

            // prints results
            if (!impossible) {
                addCommonAncestors(pathOne, commonAncestors);
                System.out.println((pathOne.size() - 1) + " " + (pathTwo.size() - 1));
                printPath(pathOne);
                printPath(pathTwo);
            } else {
                System.out.println("impossible");
            }

        }
    }

    /**
     * Custom Comparator which sorts an array of strings
     * from longest to shortest.
     */
    public static class LongestToShortestString implements Comparator<String> {
        public int compare(String a, String b) {
            return b.length() - a.length();
        }
    }

    /**
     * Checks to see if b is a subsequence of a.
     * @param   String a: The larger string.
     * @param   String b: The subsequence to look for.
     * @return  boolean: Whether or not b is a subsequence of a.
     */
    public static boolean isPathAncestor(LinkedList<String> path, String b) {
        String a = path.peek();
        for (int aChar = 0, bChar = 0; bChar < b.length(); bChar++) {
            aChar = a.indexOf(b.charAt(bChar), aChar);
            if (++aChar == 0) return false;
        }

        return true;
    }

    /**
     * Checks to see if the current list of common ancestors can have
     * another one appended to it. This is only when there are no
     * other common ancestors, or if the new is a subsequence of the
     * last ancestor in the list.
     * @param   LinkedList<String> commonAncestors: The list of common ancestors.
     * @param   String ancestor: The new ancestor to try to add.
     * @return  boolean: Whether or not we could have another common ancestor.
     */
    public static boolean canHaveCommonAncestor(LinkedList<String> commonAncestors, String ancestor) {
        String last = commonAncestors.peek();
        return last == null || isPathAncestor(commonAncestors, ancestor);
    }

    /**
     * Adds a list common ancestors to one of the paths.
     * @param  LinkedList<String> path: The path to add to.
     * @param  LinkedList<String> commonAncestors: The ancestors to add.
     */
    public static void addCommonAncestors(LinkedList<String> path, LinkedList<String> commonAncestors) {
        while (commonAncestors.size() > 0) {
            path.push(commonAncestors.removeLast());
        }
    }

    /**
     * Prints out an evolutionary path.
     * @param path LinkedList<String> path: The path to print.
     */
    public static void printPath(LinkedList<String> path) {
        while (path.size() > 1) {
            System.out.println(path.pop());
        }
    }

}
