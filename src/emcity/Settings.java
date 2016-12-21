package emcity;

public class Settings {
	// general settings
	boolean swarmingOnOff = false, 
			showTrails = true, 
			stigmergy = true, 
			showDir = true, 
			attract = true, 
			generate = false, 
			show_att_distance = false, 
			objexport = false, 
			record = false,
			showPheromone = true; // show pheromone path
	int approach = 100,
		nSamples = 70; //200 - how many samples it takes (precise)
	float 	factor = 0.05f, 
			scatter = 10,  // stigmergy influence raduis
			decay = 1.5f, 
			att_distance = 100f,
			att_angle = 1.4f,
			att_factor = 0.1f, 
			stigmergyStrength = 0.8f, 
			cohesion = 0f, 
			alignment = 1.5f, 
			separation = 1.5f;

	
	// Populations
	int pop_1, pop_2, pop_3, pop_4, pop_5, pop_6, pop_7, pop_8;
	
	public void pop1x(int value){
		Agent.initLocations[0].x = value;
	}
	
	public void pop1y(int value){
		Agent.initLocations[0].y = value;
	}
	
	public void pop2x(int value){
		Agent.initLocations[1].x = value;
	}
	
	public void pop2y(int value){
		Agent.initLocations[1].y = value;
	}
	
	public void pop3x(int value){
		Agent.initLocations[2].x = value;
	}
	
	public void pop3y(int value){
		Agent.initLocations[2].y = value;
	}
	
	public void pop4x(int value){
		Agent.initLocations[3].x = value;
	}
	
	public void pop4y(int value){
		Agent.initLocations[3].y = value;
	}
	
	public void pop5x(int value){
		Agent.initLocations[4].x = value;
	}
	
	public void pop5y(int value){
		Agent.initLocations[4].y = value;
	}
	
	public void pop6x(int value){
		Agent.initLocations[5].x = value;
	}
	
	public void pop6y(int value){
		Agent.initLocations[5].y = value;
	}
	
	public void pop7x(int value){
		Agent.initLocations[6].x = value;
	}
	
	public void pop7y(int value){
		Agent.initLocations[6].y = value;
	}
	
	public void pop8x(int value){
		Agent.initLocations[7].x = value;
	}
	
	public void pop8y(int value){
		Agent.initLocations[7].y = value;
	}
	
	public void pop1amount(int value){
		Agent.populationSizes[0] = value;
	}
	
	public void pop2amount(int value){
		Agent.populationSizes[1] = value;
	}
	
	public void pop3amount(int value){
		Agent.populationSizes[2] = value;
	}
	
	public void pop4amount(int value){
		Agent.populationSizes[3] = value;
	}
	
	public void pop5amount(int value){
		Agent.populationSizes[4] = value;
	}
	
	public void pop6amount(int value){
		Agent.populationSizes[5] = value;
	}
	
	public void pop7amount(int value){
		Agent.populationSizes[6] = value;
	}
	
	public void pop8amount(int value){
		Agent.populationSizes[7] = value;
	}

}
