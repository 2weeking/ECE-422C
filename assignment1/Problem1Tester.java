/* Student Name: Vincent Nguyen, Lab Section: 18695 */
package assignment1;

/** Student-written tests for Problem 1. Add your own test cases below. */
public class Problem1Tester {
    public static void main(String[] args) {
        // Example of the required format only. Replace/add cases based on the specification.
        // "9989" with n=4; --> 5832
        TestSupport.checkEquals(5832L,
                Problem1.largestProduct("9989", 4),
                "example-sized prefix");

        // TODO: Add tests that exercise distinct behaviors and boundary cases.

        // "09" with n=2 --> 9; because 9 by itself is > 0x9 and, 9 has n=1 which is <= n=2
        TestSupport.checkEquals(9L,
                Problem1.largestProduct("09", 2),
                "best answer uses fewer digits than n & have a 0");

        // "909" with n=3 --> 9, same logic as before
        TestSupport.checkEquals(9L,
                Problem1.largestProduct("909", 3),
                "best answer uses fewer digits than n & have a 0 in middle");

        // "9" with n=3 --> 9
        TestSupport.checkEquals(9L,
                Problem1.largestProduct("9", 3),
                "best answer is the entire string as it is less than n");
        
        // "91" with n=5 --> 9
        TestSupport.checkEquals(9L,
                Problem1.largestProduct("91", 5),
                "best answer is found even when n is larger than string length");

        // "1234" with n=2 --> 12
        TestSupport.checkEquals(12L,
                Problem1.largestProduct("1234", 2),
                "best answer is still found when no 0 existing");

        // "00000" with n=3 --> 0
        TestSupport.checkEquals(0L,
                Problem1.largestProduct("00000", 3),
                "all zeros has product 0");

        // "9199209" with n=4 --> 729, 9x1x9x9 = 729, 1x9x9x2 = 162, 9x9x2x0 = 0, 9x2x0x9 = 0
        TestSupport.checkEquals(729L,
                Problem1.largestProduct("9199209", 4),
                "best answer is found even when 0 shortens the window");

        TestSupport.finish();
    }
}
