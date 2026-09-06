/* Student Name: Vincent Nguyen, Lab Section: 18695 */
package assignment1;

/*
 * Starter implementation for Assignment #1, Problem 1.
 *
 * IMPORTANT: This implementation is intentionally imperfect.
 * Your job is NOT to replace it immediately. First, develop a strong
 * test suite that exposes its defects and documents the intended
 * behavior described in the assignment.
 */
public class Problem1 {

    public static long largestProduct(String s, int n) {
        long largest = 0;
        int length = s.length();

        for (int i = 0; i <= length; i++) {
            long product = 1;
            int maxLength = Math.min(n, length - i);
            for (int j = 0; j < maxLength; j++) {
                product *= (s.charAt(i + j) - '0');
                if (product > largest) {
                largest = product;
                }
            }
        }

        return largest;
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        String s = scanner.nextLine();
        System.out.println(largestProduct(s, n));
    }
}
