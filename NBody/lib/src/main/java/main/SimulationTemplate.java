package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.paint.Color;

public class SimulationTemplate {
	
	public HashMap<String, Supplier<List<Body>>> map;
	
	public SimulationTemplate() {
		map = new HashMap<>();
		map.put("Circle", () -> randomCircle());
		map.put("Swirl", () -> swirl());
		map.put("Rectangle", () -> randomRect());
		map.put("Two Clusters", () -> twoClustersAgain());
		map.put("Triangle", () -> triangle());
		map.put("3x3", () -> threeByThree());
		map.put("Spiral", () -> spiral());
		map.put("Binary", () -> binaryStarSystem());
		map.put("Test", () -> test());
		map.put("Rings", () -> ringOfSatellites());
		map.put("Uniform Ring", () -> uniformRing());
	}
	
	
	// TODO: Serialize Simulation Templates to save / open starting simulations
	
	
	public List<Body> randomRect() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
        for (int i = 0; i < n; i++) {
            double x = rand.nextDouble() * width;
            double y = rand.nextDouble() * height;
            double vx = 0;
            double vy = 0;
            double mass = 1;
            bodies.add(new Body(x, y, vx, vy, mass));
        }
       return bodies;
	}
	
	public List<Body> randomCircle() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
        for (int i = 0; i < n; i++) {
        	double angle = 2 * Math.PI * rand.nextDouble();
            // Generate a random radius, using square root to increase density near the center
            double randomRadius = Math.sqrt(rand.nextDouble()) * Math.min(width, height)/100;
            // Convert polar to Cartesian coordinates
            double x = width/2 + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
            double vx = 0;
            double vy = 0;
            double mass = 1;
            bodies.add(new Body(x, y, vx, vy, mass));
        }
       return bodies;
	}
	
	public List<Body> twoClusters() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
        for (int i = 0; i < n/1.4; i++) {
        	// Generate a random angle
            double angle = 2 * Math.PI * rand.nextDouble();

            // Generate a random radius, using square root to increase density near the center
            double randomRadius = Math.sqrt(rand.nextDouble()) * 300;

            // Convert polar to Cartesian coordinates
            double x = width/2 + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
        	
            //double x = rand.nextDouble() * (width/2) + width/4;
            //double y = rand.nextDouble() * (height/2) + height/4;
            double vx = 0;
            double vy = 0;
            double mass = 1;
            bodies.add(new Body(x, y, vx, vy, mass));
        }
        for (int i = 0; i < n - (n/1.4); i++) {
        	
        	// Generate a random angle
            double angle = 2 * Math.PI * rand.nextDouble();

            // Generate a random radius, using square root to increase density near the center
            double randomRadius = Math.sqrt(rand.nextDouble()) * 50;

            // Convert polar to Cartesian coordinates
            double x = width/3 + randomRadius * Math.cos(angle);
            double y = height/3 + randomRadius * Math.sin(angle);
        	
            //double x = rand.nextDouble() * (width/4) + width/8;
            //double y = rand.nextDouble() * (height/4) + height/8;
            double vx = 10;
            double vy = 10;
            double mass = 2;
            bodies.add(new Body(x, y, vx, vy, mass));
        }
       return bodies;
	}
	
	public List<Body> swirl() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			double angle = 2 * Math.PI * rand.nextDouble();
			double randomRadius = (rand.nextDouble() * 500);
            double x = width/2 + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
            // set to 100 to get trippy start
            double mag = randomRadius / 500 * 20;
            //double vx = mag * (Math.cos(angle) - Math.sin(angle));
            //double vy = mag * Math.cos(angle) - Math.sin(angle);
            double vx = mag * Math.cos(angle + (Math.PI / 2));
            double vy = mag * Math.sin(angle + (Math.PI / 2));
            double mass = rand.nextDouble() * 10;
            // Color.hsb(320 + (30 * (mass / 5))
            bodies.add(new Body(x, y, vx, vy, Math.sqrt(rand.nextDouble() * 5), Color.hsb(30 + ((mass / 10) * 20), Math.sqrt(mass / 10) / 10, 1.0, 0.35)));
		}
		
		bodies.add(new Body(width/2, height/2, 0, 0, 10000, Color.rgb(255, 255, 255, 0.35)));
		//bodies.add(new Body(width/6, height/2, 10, 0.0, 100000, Color.rgb(255, 0, 0, 0.35)));
		//bodies.add(new Body(width/2 - 10, height/2, 0, 0, 1000, Color.rgb(255, 255, 255)));
			
		return bodies;
	}
	
	public List<Body> oldSwirl() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			double angle = 2 * Math.PI * rand.nextDouble();
			double randomRadius = (rand.nextDouble() * 500);
            double x = width/2 + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
            double mag = 50;
            //double vx = mag * (Math.cos(angle) - Math.sin(angle));
            //double vy = mag * Math.cos(angle) - Math.sin(angle);
            double vx = Math.cos(angle + (Math.PI / 2));
            double vy = Math.sin(angle - (Math.PI / 2));
            double mass = rand.nextDouble() * 5;
            // Color.hsb(320 + (30 * (mass / 5))
            bodies.add(new Body(x, y, vx, vy, Math.sqrt(rand.nextDouble() * 5), Color.hsb(30 + ((mass / 5) * 20), Math.sqrt(mass / 5) / 5, 1.0, 0.35)));
		}
			
		return bodies;
	}
	
	public List<Body> triangle() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			double angle = 2 * Math.PI * rand.nextDouble();
			double randomRadius = (rand.nextDouble() * 500);
            double x = width/2 + (Math.cos(angle) * randomRadius);
            double y = height/2 + randomRadius;
            double vx = Math.cos(angle);
            double vy = Math.sin(angle);
            double mass = rand.nextDouble() * 3;
            
            bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(40, mass / 15, 1.0, 0.35)));
		}
		
		return bodies;
	}
	
	public List<Body> threeByThree() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < n/9; k++) {
					double angle = 2 * Math.PI * rand.nextDouble();
					double randomRadius = (rand.nextDouble() * 50);
		            double x = width/2 + ((i - 1) * width/6) + randomRadius * Math.cos(angle);
		            double y = height/2 + ((j - 1) * height/6) + randomRadius * Math.sin(angle);
		            double vx = 0;
		            double vy = 0;
		            double mass = rand.nextDouble() * 3;
		            
		            bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(40, mass / 15, 1.0, 0.35)));
				}
			}
		}
		
		return bodies;
	}
	
	public List<Body> spiral() {
		int n = MainController.N;
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    int arms = 6;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();
	    double galaxyCenterX = width / 2;
	    double galaxyCenterY = height / 2;
	    
	    double maxRadius = 500.0;  // Max distance from the center of the galaxy
	    double armWidth = 50.0;    // Control how "tight" or "wide" the arms are
	    
	    for (int i = 0; i < n; i++) {
	        // Randomly assign stars to one of the arms, create an angle based on that arm
	        int armIndex = i % arms;
	        double baseAngle = (2 * Math.PI * armIndex) / arms;
	        
	        // Add a small variation to the angle for randomization
	        double randomAngleOffset = rand.nextGaussian() * 0.1; // Small random deviation
	        double angle = baseAngle + randomAngleOffset;
	        
	        // Create the radius for the body with more density in the center
	        double randomRadius = Math.pow(rand.nextDouble(), 2) * maxRadius;  // Squaring to bias towards center
	        
	        // Position the body along the spiral arms
	        double armCurveFactor = randomRadius / armWidth;
	        double x = galaxyCenterX + randomRadius * Math.cos(angle + armCurveFactor);
	        double y = galaxyCenterY + randomRadius * Math.sin(angle + armCurveFactor);
	        
	        // Velocity: bodies orbiting around the center, but slower as they move outward
	        double mag = Math.sqrt(randomRadius) * 1.5;  // Orbital velocity diminishes with distance
	        double vx = mag * Math.sin(angle + armCurveFactor);  // Tangential velocity
	        double vy = -mag * Math.cos(angle + armCurveFactor);
	        
	        // Mass: heavier bodies near the core, lighter bodies toward the edges
	        double mass = Math.max(1, rand.nextDouble() * 10 * (1 - randomRadius / maxRadius));
	        
	        // Add the body to the simulation
	        bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(240 - (mass / 10 * 240), 1.0, 1.0, 0.8)));
	    }
	    
	    // Central black hole or massive body in the middle of the galaxy
	    bodies.add(new Body(galaxyCenterX, galaxyCenterY, 0, 0, 10000, Color.WHITE));
	    
	    return bodies;
	}
	
	
	public List<Body> ellipse() {
		int n = MainController.N;
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();
	    
	    
	    
	    return bodies;
	}
	
	public List<Body> binaryStarSystem() {
	    int n = MainController.N;
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();
	    
	    double dist = 100;
	    
	    // Binary star positions
	    double star1X = width/2 - dist;
	    double star1Y = height/2;
	    double star2X = width/2 + dist;
	    double star2Y = height/2;
	    
	    // Mass of the stars
	    double star1Mass = 5000;
	    double star2Mass = 5000;
	    
	    // Add the two massive stars with opposite velocities (orbiting each other)
	    bodies.add(new Body(star1X, star1Y, 0.05, 0.1, star1Mass, Color.rgb(255, 255, 0, 0.8)));  // Star 1 (yellow)
	    bodies.add(new Body(star2X, star2Y, -0.05, -0.1, star2Mass, Color.rgb(255, 153, 51, 0.8))); // Star 2 (orange)
	    
	    // Add smaller bodies orbiting the binary system
	    for (int i = 0; i < n; i++) {
	        // Pick a random distance from the center of mass between the stars
	        double randomRadius = (rand.nextDouble() * 400) + 50;  // Keep them a bit farther from the stars
	        double angle = 2 * Math.PI * rand.nextDouble();  // Random angle
	        
	        // Position the bodies around the center of the two stars
	        double centerX = (star1X + star2X) / 2;
	        double centerY = (star1Y + star2Y) / 2;
	        double x = centerX + randomRadius * Math.cos(angle);
	        double y = centerY + randomRadius * Math.sin(angle);
	        
	        // Velocity: The bodies orbit around the center of the binary stars
	        double mag = Math.sqrt(randomRadius) * 0.2;  // Control the speed of orbit
	        double vx = -mag * Math.sin(angle);
	        double vy = mag * Math.cos(angle);
	        
	        // Small body mass
	        double mass = rand.nextDouble() * 2 + 0.1;  // Small bodies
	        
	        // Color based on distance from the center of the binary system
	        bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(240 - (randomRadius / 400 * 240), 1.0, 1.0, 0.8)));
	    }
	    
	    return bodies;
	}
	
	public List<Body> twoClustersAgain() {
		int n = MainController.N;
		int width = MainController.SIM_WIDTH;
		int height = MainController.SIM_HEIGHT;
		List<Body> bodies = new ArrayList<>();
		Random rand = new Random();
		
		final double DIST = 150;
		final double VEL_OPP = 5;
		
        for (int i = 0; i < n/2; i++) {
        	
            double angle = 2 * Math.PI * rand.nextDouble();
            double randomRadius = Math.sqrt(rand.nextDouble() * n);
            double x = width/2 - DIST + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
            double vx = VEL_OPP;
            double vy = 0;
            double mass = Math.sqrt(rand.nextDouble() * 10);
            bodies.add(new Body(x, y, vx, vy, mass, Color.rgb(255, 0, 0, 0.25)));
        }
        for (int i = 0; i < n/2; i++) {
        	
        	double angle = 2 * Math.PI * rand.nextDouble();
            double randomRadius = Math.sqrt(rand.nextDouble() * n);
            double x = width/2 + DIST + randomRadius * Math.cos(angle);
            double y = height/2 + randomRadius * Math.sin(angle);
            double vx = -VEL_OPP;
            double vy = 0;
            double mass = Math.sqrt(rand.nextDouble() * 10);
            bodies.add(new Body(x, y, vx, vy, mass, Color.rgb(0, 0, 255, 0.25)));
        }
       return bodies;
	}
	
	public List<Body> ringOfSatellites() {
	    int n = MainController.N; // Total number of bodies
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();

	    // Central massive body (like a star or planet)
	    double centerX = width / 2;
	    double centerY = height / 2;
	    double centralMass = 5000; // Mass of the central body
	    bodies.add(new Body(centerX, centerY, 0, 0, centralMass, Color.WHITE));

	    // Parameters for the orbiting satellites
	    double ringRadius = 1000; // Distance from the center to the satellites
	    double velocityMultiplier = Math.sqrt(centralMass / ringRadius); // Orbital velocity based on mass and radius

	    // Generate the satellites in a stable ring pattern
	    for (int i = 0; i < n; i++) {
	        // Evenly distribute the bodies around the ring
	        double angle = 2 * Math.PI * i / n; 
	        double x = centerX + ringRadius * Math.cos(angle);
	        double y = centerY + ringRadius * Math.sin(angle);

	        // Calculate the tangential velocity for stable orbit
	        double vx = -velocityMultiplier * Math.sin(angle);
	        double vy = velocityMultiplier * Math.cos(angle);

	        // Minimal perturbations to avoid chaotic behavior
	        double satelliteMass = 5; //rand.nextDouble() * 5 + 0.5;
	        Color satelliteColor = Color.hsb(200 + (satelliteMass * 10), 1.0, 1.0, 0.8);

	        // Add body to the simulation
	        bodies.add(new Body(x, y, vx, vy, satelliteMass, satelliteColor));
	    }

	    return bodies;
	}
	
	public List<Body> uniformRing() {
	    int n = MainController.N;
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();
	    
	    double centralMass = 500; // Mass of the central body
	    double ringRadius = 800;    // Radius of the ring
	    double angularSpacing = 2 * Math.PI / n; // Uniform angular spacing

	    // Generate the central massive body
	    bodies.add(new Body(width / 2, height / 2, 0, 0, centralMass, Color.rgb(255, 255, 255, 0.35)));

	    // Generate the satellites uniformly spaced along the ring
	    for (int i = 0; i < n; i++) {
	        double angle = i * angularSpacing;  // Uniformly distributed angle
	        double x = width / 2 + ringRadius * Math.cos(angle);
	        double y = height / 2 + ringRadius * Math.sin(angle);

	        // Calculate the velocity for stable orbit
	        double orbitalSpeed = Math.sqrt(centralMass / ringRadius) * 6.0; // Simplified orbital velocity calculation
	        double vx = -orbitalSpeed * Math.sin(angle);  // Tangential velocity
	        double vy = orbitalSpeed * Math.cos(angle);   // Tangential velocity

	        // Give each satellite a random small mass and color
	        double mass = 5; //rand.nextDouble() * 10 + 1; // Avoiding very small masses
	        bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(30 + ((mass / 10) * 20), Math.sqrt(mass / 10) / 10, 1.0, 0.35)));
	    }

	    return bodies;
	}


	

	
	public List<Body> test() {
	    int n = MainController.N;
	    int width = MainController.SIM_WIDTH;
	    int height = MainController.SIM_HEIGHT;
	    List<Body> bodies = new ArrayList<>();
	    Random rand = new Random();

	    // Parameters for the two vortices
	    double vortex1CenterX = width / 3;
	    double vortex1CenterY = height / 2;
	    double vortex2CenterX = 2 * width / 3;
	    double vortex2CenterY = height / 2;
	    double vortexRadius = 250; // Max radius for the spirals
	    double vortexStrength = 30; // Controls the speed of the rotation around the vortex

	    // Central "black hole" for each vortex
	    double vortexMass = 8000;

	    // Add two central massive bodies for each vortex
	    bodies.add(new Body(vortex1CenterX, vortex1CenterY, 1, 0, vortexMass, Color.WHITE));
	    bodies.add(new Body(vortex2CenterX, vortex2CenterY, -1, 0, vortexMass, Color.WHITE));

	    // Generate bodies swirling around the first vortex
	    generateVortex(bodies, n / 2, vortex1CenterX, vortex1CenterY, vortexRadius, vortexStrength, rand, 1);

	    // Generate bodies swirling around the second vortex
	    generateVortex(bodies, n / 2, vortex2CenterX, vortex2CenterY, vortexRadius, vortexStrength, rand, -1);

	    return bodies;
	}

	// Helper method to generate a vortex of swirling bodies
	private void generateVortex(List<Body> bodies, int numBodies, double centerX, double centerY, double radius, double strength, Random rand, int direction) {
	    for (int i = 0; i < numBodies; i++) {
	        double angle = 2 * Math.PI * rand.nextDouble();
	        double distanceFromCenter = rand.nextDouble() * radius;

	        // Position of the body in the vortex
	        double x = centerX + distanceFromCenter * Math.cos(angle);
	        double y = centerY + distanceFromCenter * Math.sin(angle);

	        // Velocity tangential to create swirling motion
	        double velocityMagnitude = strength / Math.sqrt(distanceFromCenter + 1); // Speed decreases as distance increases
	        double vx = direction * velocityMagnitude * Math.sin(angle);
	        double vy = -direction * velocityMagnitude * Math.cos(angle);

	        // Add slight random perturbations to make it more dynamic
	        vx += 0.5 * rand.nextGaussian();
	        vy += 0.5 * rand.nextGaussian();

	        // Set random mass and assign color based on distance from the vortex center
	        double mass = rand.nextDouble() * 3 + 0.1;
	        double brightness = Math.min(1.0, 0.5 + (1 - distanceFromCenter / radius));
	        bodies.add(new Body(x, y, vx, vy, mass, Color.hsb(180 - (distanceFromCenter / radius * 180), 1.0, 1.0, brightness)));
	    }
	}
	
	
	
	public HashMap<String, Supplier<List<Body>>> getMap() {
		 return map;
	}
	
	public Simulation toSimulation(String string) {
		if (map.containsKey(string)) {
			int width = MainController.SIM_WIDTH;
			int height = MainController.SIM_HEIGHT;
			return new Simulation(width, height, map.get(string).get());
		}
		return null;
	}
	
}
