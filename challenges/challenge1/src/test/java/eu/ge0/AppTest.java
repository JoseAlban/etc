package eu.ge0;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AppTest {

    int[] haystack, needle;

    @Test
    public void itReturnsPositionOfSecondArrayWithinFirst() throws Exception {
        haystack = new int[]{2, 3, 4, 5};
        needle = new int[]{4, 5};

        assertEquals(2, App.findPosition(haystack, needle));
    }

    @Test
    public void itReturnsNegativeNumberWhenNeedleNotFoundInHaystack() throws Exception {
        haystack = new int[]{2, 3, 4, 5};
        needle = new int[]{4, 7};

        assertEquals(Integer.MIN_VALUE, App.findPosition(haystack, needle));
    }

    @Test
    public void itReturnsNegativeNumberWhenNeedleIsLargerThanHaystack() throws Exception {
        haystack = new int[]{1, 2, 3};
        needle = new int[]{1, 2, 3, 4};

        assertEquals(Integer.MIN_VALUE, App.findPosition(haystack, needle));
    }

    @Test
    public void itReturnsNegativeNumberWhenNeedleIsEmpty() throws Exception {
        haystack = new int[]{1, 2, 3};
        needle = new int[]{};

        assertEquals(Integer.MIN_VALUE, App.findPosition(haystack, needle));
    }

    @Test
    public void itReturnsNegativeNumberWhenHaystackIsEmpty() throws Exception {
        haystack = new int[]{};
        needle = new int[]{1, 2, 3};

        assertEquals(Integer.MIN_VALUE, App.findPosition(haystack, needle));
    }
}
