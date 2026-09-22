interface Printable {
    void print();
}

abstract class Shape implements Printable {
    protected String color;

    public Shape(String color) {
        this.color = color;
    }

    public abstract double area();

    public String getColor() {
        return color;
    }
}

class Rectangle extends Shape {

    private double width;
    private double height;

    public Rectangle(String color, double width, double height) {
        // YOUR CODE
        super (color);
        this.width = width;
        this.height = height;
    }
    // YOUR CODE
    @Override 
    public double area() {
        return width * height;
    }
    @Override 
    public void print() {
        System.out.println ("The Rectangle's color is " + color + ", and the Rectangle's area is " + area());
    }
}