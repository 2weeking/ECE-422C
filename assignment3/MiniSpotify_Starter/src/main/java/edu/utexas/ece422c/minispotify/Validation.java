package edu.utexas.ece422c.minispotify;

final class Validation {
    private Validation() {
    }

    //reject null consistently | service errors must use IllegalArgumentException
    static <T> T require(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    static void nonnegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("n must be nonnegative");
        }
    }
}
