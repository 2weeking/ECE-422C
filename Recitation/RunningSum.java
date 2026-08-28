import java.util.Arrays;

public class RunningSum {
    public static int[] runningSum(int[] nums) {
        int[] result = new int [nums. length];
        int total = 0;

        for (int i=0; i < nums.length; i++) {
            total += nums[i];
            result[i] = total;
        }
        return result;
    }
    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4};
        System.out.println(Arrays.toString(runningSum(nums)));
    }
}