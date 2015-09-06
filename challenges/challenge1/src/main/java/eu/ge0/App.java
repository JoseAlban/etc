package eu.ge0;

public class App {
    private static final int NOT_FOUND = Integer.MIN_VALUE;

    /**
     * Given two arrays, will find the starting position of the second array in the first array.
     */
    public static int findPosition(int[] haystack, int[] needle) {
        if (needle.length == 0) return NOT_FOUND;

        int position = NOT_FOUND;
        for (int i = 0; i < haystack.length; i++) {
            int remaining = haystack.length - i;
            if (needle.length > remaining) break; // needle is currently larger than haystack

            if (needle[0] == haystack[i]) { // found initial hook
                position = i;
                for (int j = 1; j < needle.length; j++) { // can save one iteration
                    int haystackPosition = i+j;
                    if (needle[j] != haystack[haystackPosition]) { // needle has discontinued
                        position = NOT_FOUND;
                        break;
                    }
                }
            }
        }

        return position;
    }
}
