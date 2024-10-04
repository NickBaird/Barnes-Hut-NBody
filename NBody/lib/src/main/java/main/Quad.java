package main;

public class Quad {
    private double x, y, width, height;

    public Quad(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(double px, double py) {
        return (px >= x) && (px < x + width) && (py >= y) && (py < y + height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

}