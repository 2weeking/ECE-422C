public class RunLengthEncode {

    public static String encode(String word) {
        StringBuilder encodedString = new StringBuilder();
        int count = 1; //each char start at 1
        char prevChar = word.charAt(0); //initial char

        for (int i = 1; i < word.length(); i++) { //start at 2nd char to compare
            char currentChar = word.charAt(i);
            if (currentChar == prevChar) {
                count++;
            } else { //reset
                encodedString.append(count).append(prevChar);
                prevChar = currentChar;
                count = 1;
            }
        }
        encodedString.append(count).append(prevChar);
        return encodedString.toString();
    }

    public static void main(String[] args) {
        String input = "AANNNNNNBBBTDK";
        String output = encode(input);
        System.out.println("original: " + input);
        System.out.println("output: " + output);
    }
}