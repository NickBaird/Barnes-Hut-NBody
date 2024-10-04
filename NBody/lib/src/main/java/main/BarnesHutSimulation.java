package main;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class BarnesHutSimulation extends Application {
	
//	public int WIDTH = 1920, HEIGHT = 1080;
//	public int SIM_MULT = 2, SIM_WIDTH = SIM_MULT * WIDTH, SIM_HEIGHT = SIM_MULT * HEIGHT; 
//	public static double DT = 1.0;
//	public static double THETA = 0.50;
//	public static int N = 10000;
//	
//	public static AnchorPane root;
//	
//	public static Thread SIMULATION_THREAD;
//	public boolean running = true;
//	public boolean paused = false;
//	
//	public static boolean QUADS = false, TRACERS = false;
//	
//	public SimulationTemplate templates = new SimulationTemplate(SIM_WIDTH, SIM_HEIGHT, N);
//	public Simulation simulation = new Simulation(SIM_WIDTH, SIM_HEIGHT, templates.swirl());
//	public Simulation simulationBuffer = new Simulation(SIM_WIDTH, SIM_HEIGHT, new ArrayList<>());
//	
//	public Canvas canvas = new Canvas(SIM_WIDTH, SIM_HEIGHT);
//	public GraphicsContext gc = canvas.getGraphicsContext2D();
	
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Barnes-Hut Simulation");
        
		try {
			URL url = BarnesHutSimulation.class.getResource("/mainWindow.fxml");
			Parent root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void onLaunch(String[] args) {
		BarnesHutSimulation.launch(args);
	}
}

//class SimulationCanvas {
//    private final Canvas canvas;
//    private GraphicsContext gc;
//    private Simulation simulation;
//    private Simulation simulationBuffer;
//    
//    private Thread simulationThread = null;
//    private boolean simulationRunning = true;
//    
//    final double MIN_SCALE = 0.25, MAX_SCALE = 5.0;
//    double scale = 1.0, scaleX = 1.0, scaleY = 1.0;
//    double offsetX = 0.0, offsetY = 0.0, lastX = 0.0, lastY = 0.0;
//    
//    boolean needsScaling = false, needsOffseting = false;
//
//    public SimulationCanvas(double width, double height, Simulation simulation) {
//        this.canvas = new Canvas(width, height);
//        this.gc = canvas.getGraphicsContext2D();
//        
//        //gc.translate((-width/2) + (width/20), (-height/2) + (height/20));
//        
//        this.simulation = simulation;
//        this.simulationBuffer = simulation;
//        //this.pool = new ForkJoinPool(); // Configure as needed
//        //simulation.initializeBodies(10000);
//
//        
//        canvas.setOnScroll(event -> {
//        	double scalar = (event.getDeltaY() < 0 ? -1 : 1) * 0.05;     	
//        	
//        	if (scale + scalar > MIN_SCALE && scale + scalar < MAX_SCALE) {
//	            scaleX = 1 + scalar;
//	            scaleY = 1 + scalar;
//	            scale += scalar;
//	            
//	            System.out.println(event.getX() * scalar);
//	            
//	            offsetX = -(event.getX() * scalar);
//	            offsetY = -(event.getY() * scalar);
//	            
//	            needsScaling = true;
//	            needsOffseting = true;
//	        	
//	
//	            // Re-render the canvas
//	            render();
//        	}
//        	
//        	System.out.println(scale);
//        });
//        
//        canvas.setOnMousePressed(event -> {
//            lastX = event.getX();
//            lastY = event.getY();
//        });
//
//        canvas.setOnMouseDragged(event -> {
//            double deltaX = event.getX() - lastX;
//            double deltaY = event.getY() - lastY;
//
//            // Update pan offsets
//            offsetX += deltaX;
//            offsetY += deltaY;
//
//            lastX = event.getX();
//            lastY = event.getY();
//            
//            needsOffseting = true;
//
//            // Re-render the canvas
//            render();
//        });
//
//
//
//        // Make the canvas focusable to receive key events
//        canvas.setFocusTraversable(true);
//    }
//    
//
//	public Canvas getCanvas() {
//        return canvas;
//    }
//
//    public void startSimulation() {
//        Thread simulationThread = new Thread(() -> {
//            while (true) {
//            	simulationBuffer.copyFrom(simulation);
//                simulationBuffer.buildTreeParallel(); // Parallel tree construction
//                simulationBuffer.updateForces();      // Parallel force calculation
//                simulationBuffer.updatePositions();    // Parallel position updates
//
//                // Swap buffers
//                Simulation temp = simulation;
//                simulation = simulationBuffer;
//                simulationBuffer = temp;
//                Platform.runLater(this::render);
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    break;
//                }
//               
//            }
//        });
//        
//        this.simulationThread = simulationThread;
//        simulationThread.setDaemon(true);
//        simulationThread.start();
//    }
//    
//    public void stopSimulation() {
//    	if (simulationThread != null)
//    		simulationThread.interrupt();
//    }
//    
//    public void continueSimulation() {
//    	if (simulationThread != null)
//    		startSimulation();
//    }
//
//    private void render() {
//        // Clear the canvas
//    	
//    	//gc = canvas.getGraphicsContext2D();
//    	//gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
//    	//gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//    
//    	
//    	
//        gc.setFill(Color.rgb(0, 0, 0, false ? 0.2 : 1.0)); // RGBA: Black with 10% opacity
//        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        //gc.setFill(Color.BLACK);
//        //gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
//        
//        //System.out.println(QuadTree.MAX_MASS);
//        // Draw quadrants if debug mode is enabled
//        if (false) {
//            
//            gc.setLineWidth(0.5);
//            List<Quad> quadrants = new ArrayList<>(simulation.getQuadTree().getAllQuadrants()); // Create a copy
//            //Collections.reverse(quadrants);
//            
//            
//            for (Quad quad : quadrants) {
//            	if (quad.getWidth() > 1 && quad.getHeight() > 1) {
//            		
////            		if (quad.getMass() > 0.0) {
////            			double normalizedMass = Math.min(1.0, quad.getMass() / QuadTree.MAX_MASS); // Normalize mass to 0-1
////            	        gc.setFill(Color.hsb(240 - (normalizedMass * 240), 1.0, 1.0)); // Color gradient from blue to red
////            			//gc.setFill(Color.hsb(360 - Math.min(120, quad.getMass()/1000.0), 1.0, 1.0)); // Color gradient from blue to red
////                        gc.fillRect(quad.getX(), quad.getY(), quad.getWidth(), quad.getHeight());
////                    }
//            		gc.setStroke(Color.RED);
//            		gc.strokeRect(quad.getX(), quad.getY(), quad.getWidth(), quad.getHeight());
//                    //double normalizedMass = Math.min(1.0, quad.getMass() / (QuadTree.MAX_MASS/10)); // Normalize mass to 0-1
//                    //System.out.println(quad.getMass());
//                    
//                    
//            	}
//            	
//                
//            }
//        }
//        
//        // Apply pan (translation) and zoom (scaling)
//        if (needsScaling) {
//        	gc.scale(scaleX, scaleY);
//        	needsScaling = false;
//        }
//        
//        if (needsOffseting) {
//        	gc.translate(offsetX, offsetY);
//        	offsetX = 0;
//        	offsetY = 0;
//        	needsOffseting = false;
//        }
//        
//
//        // Draw bodies
////        List<Body> bodies = simulation.getBodies();
////        for (Body body : bodies) {
////        	gc.setFill(body.getColor());
////        	double size = Math.sqrt(Math.sqrt(body.getMass()));
////            gc.fillOval((body.getX() - size/2), (body.getY() - size/2), size, size);
////        }
//        
//        
//        List<Body> bodies = simulation.getBodies();
//        
//        for (Body body : bodies) {
//        	gc.setFill(body.getColor());
//            //gc.fillOval((body.getX() - 1 / 2), (body.getY() - 1 / 2), 1, 1);
//    		gc.fillOval(body.getX(), body.getY(), body.getSize(), body.getSize());
//        }
//        
//        //gc.setFill(Color.WHITE);
//
//
//    }
//    
//}










