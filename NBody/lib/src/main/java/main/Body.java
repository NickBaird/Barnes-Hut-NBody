package main;

import javafx.scene.paint.Color;

public class Body {
    private double x, y;			// positions
    private double vx, vy;			// velocities
    private double fx, fy;			// forces
    
    private final double mass;	
    private final Color color;
    
    private double size;			// size to display body, based on 'mass'
    								// 		established here to reduce computing per render
    
    public Body(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = 1.0;			// Default mass set to 1.0
        this.color = Color.WHITE;	// Default color set to white
        this.size = Math.sqrt(Math.sqrt(mass));
    }
    
    public Body(double x, double y, double vx, double vy, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.color = Color.WHITE;	// Default color set to white;
        this.size = Math.sqrt(Math.sqrt(mass));
    }
    
    public Body(double x, double y, double vx, double vy, double mass, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.color = color;
        this.size = Math.sqrt(Math.sqrt(mass));
    }

    public void resetForce() {
        this.fx = 0;
        this.fy = 0;
    }

    public void addForce(double fx, double fy) {
        this.fx += fx;
        this.fy += fy;
    }

    public void updatePosition(double dt) {
        vx += fx / mass * dt;
        vy += fy / mass * dt;
        x += vx * dt;
        y += vy * dt;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getVX() { return vx; }
    public double getVY() { return vy; }
    public double getFX() { return fx; }
    public double getFY() { return fx; }
    public double getMass() { return mass; }
    public Color getColor() { return color; }
    public double getSize() { return size; }
    
    // Creates a copy of a body
    public Body copy() {
        Body copy = new Body(this.x, this.y, this.vx, this.vy, this.mass, this.color);
        copy.fx = this.fx;
        copy.fy = this.fy;
        return copy;
    }
}
