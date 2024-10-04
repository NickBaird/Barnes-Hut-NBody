package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class Simulation {
    private double width, height;
    private QuadTree tree;
    private List<Body> bodies;
    
    private ForkJoinPool pool;
    
    public long forceTime = 0, positionTime = 0, buildTime = 0;
    
    public Simulation(double width, double height, List<Body> bodies) {
        this.width = width;
        this.height = height;
        this.bodies = bodies;
        this.pool = new ForkJoinPool(); // You can specify parallelism level
    }

    public synchronized QuadTree getQuadTree() {
        return tree;
    }
    
    public synchronized List<Body> getBodies() {
        return bodies;
    }
    
    public synchronized ForkJoinPool getPool() {
    	return pool;
    }

    public synchronized void copyFrom(Simulation other) {
        this.bodies.clear();
        this.bodies = new ArrayList<>(other.getBodies());
    }
    
    public void updateForces() {
    	long startTime = System.nanoTime();
    	pool.invoke(new ForceCalculationTask(bodies, tree, MainController.THETA, 0, bodies.size()));
    	long endTime = System.nanoTime();
    	forceTime = endTime - startTime;
    }
    
    public void updatePositions() {
    	long startTime = System.nanoTime();
    	pool.invoke(new PositionUpdateTask(bodies, 0, bodies.size()));
    	long endTime = System.nanoTime();
    	positionTime = endTime - startTime;
    }
    
    private static class ForceCalculationTask extends RecursiveAction {
        private static final int THRESHOLD = 1000;
        private final List<Body> bodies;
        private final QuadTree tree;
        private final double theta;
        private final int start;
        private final int end;

        public ForceCalculationTask(List<Body> bodies, QuadTree tree, double theta, int start, int end) {
            this.bodies = bodies;
            this.tree = tree;
            this.theta = theta;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    Body body = bodies.get(i);
                    body.resetForce();
                    tree.updateForce(body, theta);
                }
            } else {
                int mid = (start + end) / 2;
                ForceCalculationTask left = new ForceCalculationTask(bodies, tree, theta, start, mid);
                ForceCalculationTask right = new ForceCalculationTask(bodies, tree, theta, mid, end);
                invokeAll(left, right);
            }
        }
    }
    
    private static class PositionUpdateTask extends RecursiveAction {
        private static final int THRESHOLD = 1000;
        private final List<Body> bodies;
        private final int start;
        private final int end;

        public PositionUpdateTask(List<Body> bodies, int start, int end) {
            this.bodies = bodies;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                for (int i = start; i < end; i++) {
                    bodies.get(i).updatePosition(MainController.DT);
                }
            } else {
                int mid = (start + end) / 2;
                PositionUpdateTask left = new PositionUpdateTask(bodies, start, mid);
                PositionUpdateTask right = new PositionUpdateTask(bodies, mid, end);
                invokeAll(left, right);
            }
        }
    }
    
    public void updateForcesStream() {
        bodies.parallelStream().forEach(body -> {
            body.resetForce();
            tree.updateForce(body, MainController.THETA);
        });
    }
    
    public void updatePositionsStream() {
        bodies.parallelStream().forEach(body -> body.updatePosition(MainController.DT));
    }
    
    
    public void buildTreeParallel() {
    	long startTime = System.nanoTime();
        int numThreads = Runtime.getRuntime().availableProcessors();
        double max = 0.0;
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        List<QuadTree> subTrees = pool.invoke(new SubTreeBuilderTask(bodies, width, height, numThreads));
        
        tree = new QuadTree(0, 0, width, height);	// Root tree
        for (QuadTree subTree : subTrees) {
        	
        	if (subTree.getMass() > max)
        		max = subTree.getMass();
        	
            for (Body body : subTree.getAllBodies()) {
                tree.insert(body);
            }
        }
    	long endTime = System.nanoTime();
    	buildTime = endTime - startTime;
    }

    private static class SubTreeBuilderTask extends RecursiveTask<List<QuadTree>> {
        private static final int THRESHOLD = 10000;
        private final List<Body> bodies;
        private final double width;
        private final double height;
        private final int numThreads;

        public SubTreeBuilderTask(List<Body> bodies, double width, double height, int numThreads) {
            this.bodies = bodies;
            this.width = width;
            this.height = height;
            this.numThreads = numThreads;
        }

        @Override
        protected List<QuadTree> compute() {
            if (bodies.size() <= THRESHOLD || numThreads <= 1) {
                QuadTree subTree = new QuadTree(0, 0, width, height);
                for (Body body : bodies) {
                    subTree.insert(body);
                }           
                List<QuadTree> list = new ArrayList<>();
                list.add(subTree);
                return list; // Returns a mutable list
            } else {
                int mid = bodies.size() / 2;
                SubTreeBuilderTask leftTask = new SubTreeBuilderTask(bodies.subList(0, mid), width, height, numThreads / 2);
                SubTreeBuilderTask rightTask = new SubTreeBuilderTask(bodies.subList(mid, bodies.size()), width, height, numThreads / 2);
                invokeAll(leftTask, rightTask);
                List<QuadTree> leftResult = leftTask.join();
                List<QuadTree> rightResult = rightTask.join();
                
                leftResult.addAll(rightResult); // Now leftResult is mutable
                return leftResult;
            }
        }

    }

}