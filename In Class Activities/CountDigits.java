public class CountDigits {
    public static void main(String[] args) {
        int count = 0;

        for (int i = 0; i <= 100; i++) {
            String number = Integer.toString(i);
            for (char digit : number.toCharArray()) {
                if (digit == '8') {
                    count++;
                }
            }
        }
        System.out.println("8 appears " + count + " times from 0 to 100.");
    }
}