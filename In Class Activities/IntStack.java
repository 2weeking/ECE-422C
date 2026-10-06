public class IntStack {

    private int[] data;
    private int top;

    public IntStack(int capacity) {
        // TODO
        data = new int[capacity];
        top = 0;
    }

    // Adds value to the top of the stack.
    // Assume the stack is not full.
    public void push(int value) {
        // TODO
        data[top] = value;
        top++;
    }

    // Removes and returns the top value.
    // Assume the stack is not empty.
    public int pop() {
        // TODO
        top--;
        return data[top];
    }

    // Returns the top value without removing it.
    // Assume the stack is not empty.
    public int peek() {
        // TODO
        return data[top - 1];
    }

    public boolean isEmpty() {
        // TODO
        if (top == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        // TODO
        return top;
    }
}