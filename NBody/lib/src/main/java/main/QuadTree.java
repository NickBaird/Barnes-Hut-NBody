package main;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private Quad quadrant;
    private Body body;
    private boolean isExternal;
    private QuadTree NW, NE, SW, SE;
    private double mass;
    private double centerX, centerY;

    public QuadTree(double x, double y, double width, double height) {
        this.quadrant = new Quad(x, y, width, height);
        this.isExternal = true;
        this.mass = 0;
        this.centerX = 0;
        this.centerY = 0;
    }

    public void insert(Body b) {
        if (!quadrant.contains(b.getX(), b.getY())) {
            return; // Ignore bodies outside the quadrant
        }

        if (isExternal) {
            if (body == null) {
                body = b;
                mass = b.getMass();
                centerX = b.getX();
                centerY = b.getY();
                return;
            } else {
                subdivide();
                insertBody(body);
                body = null;
                isExternal = false;
            }
        }

        insertBody(b);

        mass += b.getMass();
        centerX = (centerX * (mass - b.getMass()) + b.getX() * b.getMass()) / mass;
        centerY = (centerY * (mass - b.getMass()) + b.getY() * b.getMass()) / mass;
    }

    private void subdivide() {
        double x = quadrant.getX();
        double y = quadrant.getY();
        double w = quadrant.getWidth() / 2;
        double h = quadrant.getHeight() / 2;

        NW = new QuadTree(x, y, w, h);
        NE = new QuadTree(x + w, y, w, h);
        SW = new QuadTree(x, y + h, w, h);
        SE = new QuadTree(x + w, y + h, w, h);
    }

    private void insertBody(Body b) {
        if (NW.quadrant.contains(b.getX(), b.getY())) {
            NW.insert(b);
        } else if (NE.quadrant.contains(b.getX(), b.getY())) {
            NE.insert(b);
        } else if (SW.quadrant.contains(b.getX(), b.getY())) {
            SW.insert(b);
        } else if (SE.quadrant.contains(b.getX(), b.getY())) {
            SE.insert(b);
        }
    }
    
    private static final double DAMPING_FACTOR = 0.98; // Slightly less than 1 to gradually reduce speed
    private static final double SOFTENING_FACTOR = 1e2; // Softening factor to prevent singularities
    private static final double MAX_ACCELERATION = 100; // Set a reasonable maximum acceleration

    public void updateForce(Body b, double theta) {
        if (isExternal) {
            if (body != null && body != b) {
                double dx = centerX - b.getX();
                double dy = centerY - b.getY();
                double dist = Math.sqrt(dx * dx + dy * dy) + SOFTENING_FACTOR;
                double force = (mass * b.getMass()) / (dist * dist);

                // Calculate the acceleration based on force
                double ax = force * dx / (dist * b.getMass());
                double ay = force * dy / (dist * b.getMass());

                // Limit acceleration
                if (Math.abs(ax) > MAX_ACCELERATION) ax = Math.signum(ax) * MAX_ACCELERATION;
                if (Math.abs(ay) > MAX_ACCELERATION) ay = Math.signum(ay) * MAX_ACCELERATION;

                b.addForce(ax, ay);
            }
        } else {
            double dx = centerX - b.getX();
            double dy = centerY - b.getY();
            double dist = Math.sqrt(dx * dx + dy * dy) + SOFTENING_FACTOR;
            double s = quadrant.getWidth();
            if ((s / dist) < theta) {
                double force = (mass * b.getMass()) / (dist * dist);
                double ax = force * dx / (dist * b.getMass());
                double ay = force * dy / (dist * b.getMass());

                // Limit acceleration
                if (Math.abs(ax) > MAX_ACCELERATION) ax = Math.signum(ax) * MAX_ACCELERATION;
                if (Math.abs(ay) > MAX_ACCELERATION) ay = Math.signum(ay) * MAX_ACCELERATION;

                b.addForce(ax, ay);
            } else {
                if (NW != null) NW.updateForce(b, theta);
                if (NE != null) NE.updateForce(b, theta);
                if (SW != null) SW.updateForce(b, theta);
                if (SE != null) SE.updateForce(b, theta);
            }
        }
    }

    
    /**
     * Collects all bodies in the tree.
     * @return A list of all bodies.
     */
    public List<Body> getAllBodies() {
        List<Body> bodies = new ArrayList<>();
        collectBodies(this, bodies);
        return bodies;
    }

    /**
     * Recursively collects bodies from the tree.
     * @param node The current node in the tree.
     * @param bodies The list to accumulate bodies.
     */
    private void collectBodies(QuadTree node, List<Body> bodies) {
        if (node == null) return;

        if (node.isExternal) {
            if (node.body != null) {
                bodies.add(node.body);
            }
        } else {
            collectBodies(node.NW, bodies);
            collectBodies(node.NE, bodies);
            collectBodies(node.SW, bodies);
            collectBodies(node.SE, bodies);
        }
    }
    
    /**
     * Collects all quadrants in the tree.
     * @return A list of all quadrants.
     */
    public List<Quad> getAllQuadrants() {
        List<Quad> quadrants = new ArrayList<>();
        collectQuadrants(this, quadrants);
        return quadrants;
    }
    
    /**
     * Recursively collects quadrants from the tree.
     * @param node The current node in the tree.
     * @param quadrants The list to accumulate quadrants.
     */
    private void collectQuadrants(QuadTree node, List<Quad> quadrants) {
        if (node == null) return;

        quadrants.add(node.quadrant);

        if (!node.isExternal) {
            collectQuadrants(node.NW, quadrants);
            collectQuadrants(node.NE, quadrants);
            collectQuadrants(node.SW, quadrants);
            collectQuadrants(node.SE, quadrants);
        }
    }
    
    public double getMass() {
    	return mass;
    }
}
