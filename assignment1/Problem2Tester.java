/* Student Name: Vincent Nguyen, Lab Section: 18695 */
package assignment1;

/** Student-written tests for Problem 2. Add your own test cases below. */
public class Problem2Tester {
    public static void main(String[] args) {
        // w23+i9+z26+a1+r18+d4 + `(0) + s19 = 100, also puncatuation doesn't end the word as wizard's is counted as 100
        {
        String input = "The wicked wizard`s wily wraith garnishes his master`s pasta with garlic.";
        String expected = "wizard`s" + System.lineSeparator()
                + "garnishes" + System.lineSeparator();

        TestSupport.checkEquals(expected,
                Problem2.findDollarWords(input),
                "assignment example");
        }

        // TODO: Add tests that exercise distinct behaviors and boundary cases.

        // J10 * 6 (60) + j10 * 4 (40) --> 100, needs to output all the capital J's and lower-case j's properly
        {
        String input = "one JJJJJJjjjj two";
        String expected = "JJJJJJjjjj" + System.lineSeparator();
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "output keeps original casing of words");
        }

        // j10 * 9 (90) + ,(0) + j10 = 100, testing punc in middle of word
        {
        String input = "one jjjjjjjjj,j two";
        String expected = "jjjjjjjjj,j" + System.lineSeparator();
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "output shouldn't fail cuz of punctuation in middle");
        }

        // j10 * 9 (90) + 3(0) + j10 = 100, testing punc in middle of word
        {
        String input = "one jjjjjjjjj3j two";
        String expected = "jjjjjjjjj3j" + System.lineSeparator();
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "output shouldn't fail cuz of number in middle");
        }

        // j10 * 9 (90) + i9 = 99
        {
        String input = "one jjjjjjjjji two";
        String expected = "";
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "value of 99 should fail cuz not 100");
        }

        // j10 * 9 (90) + k11 = 101
        {
        String input = "one jjjjjjjjjk two";
        String expected = "";
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "value of 101 should fail cuz not 100");
        }

        // multiple words are 100
        {
        String input = "one jjjjjjjjjj JJJJJJJJJJ two";
        String expected = "jjjjjjjjjj" + System.lineSeparator() + "JJJJJJJJJJ" + System.lineSeparator();
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "both should be matched and outputted orderly");
        }

        // multiple spaces
        {
        String input = "one  jjjjjjjjjj      two";
        String expected = "jjjjjjjjjj" + System.lineSeparator();
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "should output normally even with the extra spaces");
        }

        // all punctuation
        {
        String input = "...;,'/.',;./,','.;',/, ?!>?#>@*#@";
        String expected = "";
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "nothing should be outputted since value is 0 (punctuation)");
        }

        // all numbers
        {
        String input = "1024910 1204911 999999999991";
        String expected = "";
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "nothing should be outputted since value is 0 (numbers)");
        }

        // empty string
        {
        String input = "";
        String expected = "";
        TestSupport.checkEquals(expected, Problem2.findDollarWords(input),
                "empty input should output empty");
        }

        TestSupport.finish();
    }
}
