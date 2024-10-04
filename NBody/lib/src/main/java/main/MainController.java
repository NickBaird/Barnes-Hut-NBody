package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

public class MainController {
	
	@FXML
    private AnchorPane root;
	
	@FXML
    private ScrollPane scroll;
	
	@FXML
    private AnchorPane top;
	
	@FXML
    private Canvas canvas;
	private GraphicsContext gc;
	
	

    @FXML
    private ComboBox<String> dropdown;
    
    @FXML
    private TextField simulationWidth, simulationHeight;

    @FXML
    private Slider dtSlider, nSlider, thetaSlider;
    
    @FXML
    private Label dtLabel, nLabel, thetaLabel, updatingLabel, fpsLabel, performanceLabel;

    @FXML
    private Button loadBtn, startBtn, stopBtn;
    
    @FXML
    private CheckBox fpsCheckbox, quadCheckbox, performanceCheckbox, tracesCheapCheckbox, tracesExpensiveCheckbox, scaleCheckbox, velocitiesCheckbox, forcesCheckbox, smartCheckbox;
    
    
    
    public int WIDTH = 1920, HEIGHT = 1080;
	public static int SIM_WIDTH = 10000, SIM_HEIGHT = 10000;	// Default Simulation Width & Height 
	public static double DT = 0.5;
	public static double THETA = 0.50;
	public static int N = 10000;
	
	public static Thread SIMULATION_THREAD, COOLDOWN_THREAD;
	public String currLoaded = null;
	public boolean loaded = false;
	public boolean paused = true;
	
	public static volatile boolean UPDATING = false, ZOOMING = false;
	public static volatile int COOLDOWN = 500, CURR = 0;
	private ScheduledExecutorService cooldownScheduler, zoomingScheduler;
	
	long lastTime = 0, renderTime = 0, numQuads = 0, numBodies = 0;
	long frames = 0;
	double fps = 0.0;
	
	public final double MIN_SCALE = 0.25, MAX_SCALE = 3.0;
	public double scale = 1.0, scalar = 0.0;
	
	public SimulationTemplate templates;
	public Simulation simulation;
	public Simulation simulationBuffer;
	
    public double lastX = 0.0, lastY = 0.0;
    public boolean dragging = false;
	
	public void initialize() {
		gc = canvas.getGraphicsContext2D();
		templates = new SimulationTemplate();
		updateCanvas();
		
		dropdown.getItems().setAll(templates.map.keySet());
		
		loadBtn.setOnMousePressed(event -> {
			loadSimulation(dropdown.getSelectionModel().getSelectedItem());
		});
		
		startBtn.setOnMousePressed(event -> {
			paused = false;
			startBtn.setDisable(true);
			stopBtn.setDisable(false);
			startSimulation();
		});
		
		stopBtn.setOnMousePressed(event -> {
			paused = true;
			startBtn.setDisable(false);
			stopBtn.setDisable(true);
			stopSimulation();
		});
		
		root.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				WIDTH = newValue.intValue();
				centerCanvas();
			}
		});
		
		root.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				HEIGHT = newValue.intValue();
				centerCanvas();
			}
		});
		
		root.setOnScroll(event -> {
			zoom(event);
		});
		
		root.setOnMouseClicked(event -> {
			
		});
		
		root.setOnMouseDragged(event -> {
			if(!dragging) {
				lastX = event.getPickResult().getIntersectedPoint().getX();
				lastY = event.getPickResult().getIntersectedPoint().getY();
				stopSimulation();
			}
			dragging = true;
		});
		
		root.setOnMouseReleased(event -> {
			if (dragging) {
				double vx = (event.getPickResult().getIntersectedPoint().getX() - lastX)/10;
				double vy = (event.getPickResult().getIntersectedPoint().getY() - lastY)/10;
				Body body = new Body(lastX, lastY, vx, vy, 1000, Color.rgb(255, 0, 0, 0.5));
				simulation.getBodies().add(body);
				simulationBuffer.getBodies().add(body);
				dragging = false;
				if (!paused)
					startSimulation();
			}
		});
		
		velocitiesCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					forcesCheckbox.setSelected(false);
			}
		});
		
		forcesCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					velocitiesCheckbox.setSelected(false);
			}
		});
		
		simulationWidth.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue == false) {
					try {
						int newWidth = Integer.parseInt(simulationWidth.getText());
						if (newWidth != SIM_WIDTH) {
							SIM_WIDTH = newWidth;
							updateCanvas();
						}
					} catch(Exception e) {
						simulationWidth.setText(SIM_WIDTH + "");
					}
				}
			}
		});
		
		simulationWidth.setOnAction(event -> {
			try {
				int newWidth = Integer.parseInt(simulationWidth.getText());
				if (newWidth != SIM_WIDTH) {
					SIM_WIDTH = newWidth;
					updateCanvas();
				}
			} catch(Exception e) {
				simulationWidth.setText(SIM_WIDTH + "");
			}
		});
		
		simulationHeight.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println(newValue);
				if (newValue == false) {
					try {
						int newHeight = Integer.parseInt(simulationHeight.getText());
						if (newHeight != SIM_HEIGHT) {
							SIM_HEIGHT = newHeight;
							updateCanvas();
						}
					} catch(Exception e) {
						simulationHeight.setText(SIM_HEIGHT + "");
					}
				}
			}
		});
		
		simulationHeight.setOnAction(event -> {
			try {
				int newHeight = Integer.parseInt(simulationHeight.getText());
				if (newHeight != SIM_HEIGHT) {
					SIM_HEIGHT = newHeight;
					updateCanvas();
				}
			} catch(Exception e) {
				simulationHeight.setText(SIM_HEIGHT + "");
			}
		});
		
		
		dtSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				DT = newValue.doubleValue();
				dtLabel.setText(newValue.toString());
			}
		});
		
		nSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				N = newValue.intValue();
				nLabel.setText(newValue.intValue() + "");
				update();
			}
		});
		
		thetaSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				THETA = newValue.doubleValue();
				thetaLabel.setText(newValue.toString());
			}
		});
		
		/*		SET DEFAULT VALUES		*/
		dtSlider.setValue(DT);
		nSlider.setValue(N);
		thetaSlider.setValue(THETA);
		
		fpsCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					fpsLabel.setOpacity(1.0);
					fpsLabel.setDisable(false);
				} else {
					fpsLabel.setOpacity(0.0);
					fpsLabel.setDisable(true);
				}
			}
		});
			
		performanceCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					performanceLabel.setOpacity(1.0);
					performanceLabel.setDisable(false);
				} else {
					performanceLabel.setOpacity(0.0);
					performanceLabel.setDisable(true);
				}
			}
		});
		
	}
	
	public void updateCanvas() {
		canvas.setWidth(SIM_WIDTH);
		canvas.setHeight(SIM_HEIGHT);
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, SIM_WIDTH, SIM_HEIGHT);
		centerCanvas();
		update();
	}
	
	public void centerCanvas() {
		canvas.setTranslateX((WIDTH / 2) - (SIM_WIDTH / 2));
		canvas.setTranslateY((HEIGHT / 2) + 37.5 - (SIM_HEIGHT / 2));
		
		System.out.println(canvas.getTranslateX());
		System.out.println(canvas.getTranslateY());
	}
	
	public void loadSimulation(String name) {
		if (name != null) {
			stopSimulation();
			try {
				Thread.sleep(100);
			} catch(Exception e) {}
			loaded = false;
			currLoaded = dropdown.getSelectionModel().getSelectedItem();
			simulation = templates.toSimulation(name);
			simulationBuffer = new Simulation(SIM_WIDTH, SIM_HEIGHT, new ArrayList<>());	// Simulation Buffer MUST be initialized too, so initialize with zero bodies
			loaded = true;
			startSimulation();	// Quickly start to at least render the first frame(s) to display to canvas
			if (paused) {
				startBtn.setDisable(false);
				stopBtn.setDisable(true);
				stopSimulation();
			} else {
				startBtn.setDisable(true);
				stopBtn.setDisable(false);
			}
		}
	}
	
	public void startSimulation() {
        SIMULATION_THREAD = new Thread(() -> {
        	
        	if (lastTime == 0) {
        		lastTime = System.nanoTime();
        	}
        	
            while (true) {
            	long currTime = System.nanoTime();
            	simulationBuffer.copyFrom(simulation);
                simulationBuffer.buildTreeParallel(); // Parallel tree construction
                simulationBuffer.updateForces();      // Parallel force calculation
                simulationBuffer.updatePositions();    // Parallel position updates

                // Swap buffers
                Simulation temp = simulation;
                simulation = simulationBuffer;
                simulationBuffer = temp;
                
                Platform.runLater(this::render);
                frames++;
                double elapsedSeconds = (currTime - lastTime) / 1000000000.0;
                if (elapsedSeconds >= 1.0) {
                	double fps = frames / elapsedSeconds;
                	if (fpsCheckbox.isSelected()) {
                		Platform.runLater(() -> {
                			fpsLabel.setText(String.format("%.2f", fps));
                		});
                	}

                	frames = 0;
                	lastTime = currTime;
                }
                
	            try {
	            	Thread.sleep(0, 100);
	            } catch (InterruptedException e) {
		            break;
		        }

                
            }
        });
        
        SIMULATION_THREAD.setDaemon(true);
        SIMULATION_THREAD.start();
    }
    
    public void stopSimulation() {
    	if (SIMULATION_THREAD != null) {
    		resetFPS();
    		SIMULATION_THREAD.interrupt();
    	}
    }
    
    public void update() {
    	
    	stopSimulation();
    	if (UPDATING) {
    		CURR = COOLDOWN;
    	} else {
    		UPDATING = true;
    		updatingLabel.setOpacity(1.0);
    		updatingLabel.setDisable(false);
    		CURR = COOLDOWN;
    		cooldownScheduler = Executors.newScheduledThreadPool(1);
    		
    		cooldownScheduler.scheduleAtFixedRate(() -> {
    			CURR -= 5;
    			
    			if (CURR <= 0) {
    				CURR = 0;
    				UPDATING = false;
    				updatingLabel.setOpacity(0.0);
    	    		updatingLabel.setDisable(true);
    				cooldownScheduler.shutdown();
    				cooldownScheduler = null;
    				
    				loadSimulation(currLoaded);
    				
    			}
    			
    		}, 0, 5, TimeUnit.MILLISECONDS);  		
    	}
    }
    
    public void zoom(ScrollEvent event) {
    	if (scaleCheckbox.isSelected()) {
	    	if (ZOOMING) {
	    		scalar = event.getDeltaY() < 0 ? -0.1 : 0.1;
	    	} else {
	    		ZOOMING = true;
	    		scalar = event.getDeltaY() < 0 ? -0.1 : 0.1;
	    		
	    		zoomingScheduler = Executors.newScheduledThreadPool(1);
	    		
	    		zoomingScheduler.scheduleAtFixedRate(() -> {
	    			
	    			scalar *= 0.65;
	    			scale += scalar;
	    			Platform.runLater(() -> {
	    				scaleCheckbox.setText(String.format("Scale Mass (%.2f)", scale));
	    			});
	    			
	    			if (Math.abs(scalar) <= 0.00001) {
	    				ZOOMING = false;
	    	    		zoomingScheduler.shutdown();
	    	    		zoomingScheduler = null;
	    				System.out.println("ZOOMING DONE");
	    				//simulationBuffer.getBodies().add(new Body(SIM_WIDTH/2, SIM_HEIGHT/2, 1, 0.0, 100000, Color.rgb(255, 0, 0, 0.35)));
	    				
	    			}
	    			
	    		}, 0, 5, TimeUnit.MILLISECONDS);  		
	    	}
    	}
    }
    
    public void resetFPS() {
    	lastTime = 0;
    	fpsLabel.setText("0.00");
    }
    
    
	private void render() {
		long start = System.nanoTime();
		numBodies = 0;
		//gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.rgb(0, 0, 0, tracesCheapCheckbox.isSelected() ? 0.5 : 1.0)); // RGBA: Black with 10% opacity
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		

		if (quadCheckbox.isSelected()) {

			// List<Quad> quadrants = new
			// ArrayList<>(simulation.getQuadTree().getAllQuadrants()); // Create a copy
			// Collections.reverse(quadrants);

			List<Quad> quadrants = simulation.getQuadTree().getAllQuadrants();
			numQuads = quadrants.size();

			for (Quad quad : quadrants) {
				if (quad.getWidth() > 2 && quad.getHeight() > 2) {
					gc.setStroke(Color.RED);
					gc.strokeRect(quad.getX(), quad.getY(), quad.getWidth(), quad.getHeight());
				}

			}
		}
		
		double viewportX = -canvas.getTranslateX();
		double viewportY = -canvas.getTranslateY();

		List<Body> bodies = simulation.getBodies();
		
		
		// Not pretty, but makes rendering faster
		if (velocitiesCheckbox.isSelected()) {
			for (Body body : bodies) {
				
				if (smartCheckbox.isSelected() && (body.getX() < viewportX || body.getX() > viewportX + WIDTH || body.getY() < viewportY || body.getY() > viewportY + HEIGHT))
					continue;
				else numBodies++;
			
				double velocityMagnitude = Math.sqrt(Math.sqrt(body.getVX() * body.getVX() + body.getVY() * body.getVY()));
				double hue = Math.min(240, velocityMagnitude * 10);  // Higher velocity shifts towards blue
				gc.setFill(Color.hsb(hue, 1.0, 1.0, 0.75));
				if (tracesExpensiveCheckbox.isSelected()) {
					gc.setStroke(Color.hsb(hue, 1.0, 1.0, 0.25));  // Adjust stroke color and opacity
					gc.setLineWidth(1.5);  // Adjust line width for trail thickness
					gc.strokeLine(body.getX(), body.getY(), body.getX() - body.getVX() * velocityMagnitude * 0.1, body.getY() - body.getVY() * velocityMagnitude * 0.1);
				}

				if (scaleCheckbox.isSelected())
					gc.fillOval(body.getX() - body.getSize() * scale / 2, body.getY() - (body.getSize()) * scale / 2, body.getSize() * scale, body.getSize() * scale);
				else
					gc.fillOval(body.getX() - body.getSize() / 2, body.getY() - (body.getSize()) / 2, body.getSize(), body.getSize());
			}
			
			
		} else if (forcesCheckbox.isSelected()) {
			for (Body body : bodies) {
				
				if (smartCheckbox.isSelected() && (body.getX() < viewportX || body.getX() > viewportX + WIDTH || body.getY() < viewportY || body.getY() > viewportY + HEIGHT))
					continue;
				else numBodies++;
				
				double forceMagnitude = Math.sqrt(body.getFX() * body.getFX() + body.getFY() * body.getFY());
				double hue = Math.min(240, forceMagnitude * 100);  // Higher velocity shifts towards blue
				gc.setFill(Color.hsb(hue, 1.0, 1.0, 0.75));
				if (tracesExpensiveCheckbox.isSelected()) {
					gc.setStroke(Color.hsb(hue, 1.0, 1.0, 0.25));  // Adjust stroke color and opacity
					gc.setLineWidth(body.getSize());  // Adjust line width for trail thickness
					gc.strokeLine(body.getX(), body.getY(), body.getX() - body.getVX() * forceMagnitude, body.getY() - body.getVY() * forceMagnitude);
				}
			

				if (scaleCheckbox.isSelected())
					gc.fillOval(body.getX() - body.getSize() * scale / 2, body.getY() - (body.getSize()) * scale / 2, body.getSize() * scale, body.getSize() * scale);
				else
					gc.fillOval(body.getX() - body.getSize() / 2, body.getY() - (body.getSize()) / 2, body.getSize(), body.getSize());
			}
			
		} else {
			for (Body body : bodies) {
				
				if (smartCheckbox.isSelected() && (body.getX() < viewportX || body.getX() > viewportX + WIDTH || body.getY() < viewportY || body.getY() > viewportY + HEIGHT))
					continue;
				else numBodies++;
				
				gc.setFill(body.getColor());
				if (tracesExpensiveCheckbox.isSelected()) {
					double velocityMagnitude = Math.sqrt(body.getVX() * body.getVX() + body.getVY() * body.getVY());
					gc.setStroke(body.getColor().darker());  // Adjust stroke color and opacity
					gc.setLineWidth(1);  // Adjust line width for trail thickness
					gc.strokeLine(body.getX(), body.getY(), body.getX() - body.getVX() * velocityMagnitude, body.getY() - body.getVY() * velocityMagnitude);
				}
			

				if (scaleCheckbox.isSelected())
					gc.fillOval(body.getX() - body.getSize() * scale / 2, body.getY() - (body.getSize()) * scale / 2, body.getSize() * scale, body.getSize() * scale);
				else
					gc.fillOval(body.getX() - body.getSize() / 2, body.getY() - (body.getSize()) / 2, body.getSize(), body.getSize());
			}
		}		
		
		
		
		
		
		

		if (performanceCheckbox.isSelected()) {
			Platform.runLater(() -> {
				String performanceText = "PERFORMANCE:" + "\n Force Time: " + (simulation.forceTime / 1000) + "us"
						+ "\n Position Time: " + (simulation.positionTime / 1000) + "us" + "\n Build Tree Time: "
						+ (simulation.buildTime / 1000) + "us" + "\n Render Time: " + (renderTime / 1000) + "us"
						+ "\n\n Rendered Bodies: " + numBodies;
				
				if (quadCheckbox.isSelected()) {
					performanceText += "\n\n Num of Quads: " + numQuads;
				}
				performanceLabel.setText(performanceText);
			});
		}

		renderTime = System.nanoTime() - start;

	}
}
