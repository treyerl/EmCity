
/*
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 EmCity: case study AYE Singapore: AGENT-BASED SIMULATION MODEL OF COLONIAL GROWTH 12/2016
 Update 2016: Tanjong Pagar simulation model with interactive inputs
 
 Concept of simulation, partial methods programming, GUI, Path following/Attraction, Roads, Buildings2D, editing Agent Class  - (c) Peter Bus, MOLAB, FA CTU Prague, ETH Zurich 
 
 Programming Cells classes, Transcription, Clusters classes, Map and Typology class, editing Agent Class - (c) Lukas Kurilla, MOLAB, FA CTU Prague
 
 Stigmergy algorithm and Field2D class based on Stigmergy2D sketch by (c) Alessandro Zomparelli and (c) Alessio Eriolli, Co-de-iT
 and Pheromonics Processing workshop Prague 2014 organized by VSUP/RecodeNature.org by Martin Gsandtner and tutored by Alessio Eriolli (Co-de-iT), 06/2014
 Editing and adaptation by (c) Dr. Peter Bus, Lukas Kurilla, ETH Zurich, MOLAB, FA CTU Prague
 
 Plethora library by (c) Jose Sanchez (http://www.plethora-project.com/Plethora-0.3.0/index.html)
 
 
 contact: 
 
 Dr. Peter Bus
 Chair of Information Architecture
 DARCH ETH Zurich
 bus@arch.ethz.ch
 http://archa3d.com/

 (c)2011-2016
 
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 */

import java.util.ArrayList;
import java.util.function.BiConsumer;

import plethora.core.Ple_Agent;
import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;

public class EmCity extends PApplet {

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC FIELD
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public EMap map;
	private GUI gui;
	private Reader reader;
	private Spline spline;
	private Buildings2D buildings;
	private Roads roads;
	Settings s5;
	
	//GLOBAL VARIABLES
	PImage stigIm = createImage(2400,2400,ARGB);
	Field field = new Field(stigIm, this);
	
//	float fStrength=0.5f;

	ArrayList<Ple_Agent> agents;
	// int pop = 10; //80 agents = 10*8
	boolean addVolume = false;
	boolean addVolume_c = false;
	boolean addVolume_sq = false;
	static final int cell_size_b = 10;
	static final int cell_size_park = 12;
	boolean showPheromone = true; // show pheromone path


	boolean Buildings = true;
	boolean video = false;
	boolean mapdraw = true;
	boolean mapdrawc = true;
	boolean mapdrawsq = true;
	boolean reset_button = false;
	boolean DRAW_ROADS = true;

	int res = 0;
	int iterT = 1;
	
	float distant;

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setup() {
		// fullScreen();
		frameRate(50);
		// size(720, 760, P3D);
		// size(4096, 2160, P3D); //4K resolution
		// glLoadIdentity
		// colorMode(HSB, 255);
		gui = new GUI(this);
		init();
	}
	
	private void init(){
		s5 = new Settings();
		map = new EMap();
		reader = new Reader();
		spline = new Spline();
		roads = new Roads();
		buildings = new Buildings2D();

		reader.readCluster("data/clusters.txt", cell_size_b, map, Agent.PRIVATE);
		reader.readCluster("data/culture_clusters.txt", cell_size_b, map, Agent.CULTURE);
		reader.readCluster("data/square_clusters.txt", cell_size_park, map, Agent.SQUARE);
		reader.readTypology("data/typologies.txt", map);

		setupAgents();

		if (!spline.load(reader.import_GH("data/path_following_line.txt"))){
			System.out.println("failed on loading spline");
		}

		gui.initGUI();
	}
	
	private void iteratePopulations(BiConsumer<Integer,Integer> f){
		for (int i = 0; i < Agent.populationSizes.length; i++){
			int size = Agent.populationSizes[i];
			for (int j = 0; j < size; j++){
				f.accept(i, size);
			}
		}
	}
	
	private void setupAgents(){
		agents = new ArrayList<>();
		
		iteratePopulations((i,size) -> {
			Agent agent = new Agent(this, new Vec3D(Agent.initLocations[i]));
			agent.setVelocity(new Vec3D(random(-1, 0.5f), random(-1, 0.5f), 0));
			// todo - the velocity can be changed and can contribute to the weights as well
			agents.add(agent);
		});
	}

	public void draw() {
		gui.checkOverlap();

		if (s5.objexport) {
			beginRaw("nervoussystem.obj.OBJExport", "filename#####.obj");
		}

		if (s5.record) {
			beginRaw(PDF, "filename#####.pdf");
		}

		field.getImage().loadPixels();

		background(160);

		strokeWeight(1);
		noFill();
		rect(-1645, -1508, 3291, 3016); // boundaries
		ellipse(0, 0, 50, 50);
		roads.drawPoints(this);

		if (Buildings)
			buildings.draw(this, Buildings);

		if (mapdraw) {
			map.draw(this, Agent.PRIVATE);// draw habitation activity
		}
		if (mapdrawc) {
			map.draw(this, Agent.CULTURE);// draw culture clusters - culture activity
		}
		if (mapdrawsq) {
			map.draw(this, Agent.SQUARE);// draw square clusters - squares and parks
		}

		for (Ple_Agent pAgent : agents) {
			Agent agent = (Agent) pAgent;
			if (!agent.is_active)
				continue;
			if (s5.swarmingOnOff) {
				agent.flock(agents, 80F, 65F, 35F, s5.cohesion, s5.alignment, s5.separation); 
				// zakladne "ozivenie" agentov podla formy prikazu v Plethore.
				// Cisla udavaju miery hodnot viz vyssie.
			} else {
				agent.wander2D(5, 0, PI);
			}

			// wander: inputs: circleSize, distance, variation in radians agent.wander2D(5, 0, PI); separation
			agent.separationCall(agents, 10F, 10F);
			// define the boundries of the sagentce as bounce agent.bounceSpace(970,887.5, 0); 
			// TODO get boundaries from map class (agents don't return)
			agent.bounceSpace(1575, 1710, 0);
			agent.dropTrail(5, 2000);

			// update the tail info every frame (1) agent.updateTail(1);
			// display the tail interpolating 2 sets of values:
			// R,G,B,ALPHA,SIZE - R,G,B,ALPHA,SIZE agent.displayTailPoints(0, 0, 0, 0, 1, 0, 0, 0, 255, 1);
			// //set the max speed of movement:
			// //agent.setMaxspeed(2);
			// //agent.setMaxforce(0.05);

			// attraction to clusters
			agent.attraction(s5.att_distance, s5.att_angle, s5.att_factor, map, 0);
			// agent.attraction_c(att_distance, att_angle, att_factor);
			// agent.attraction_sq(att_distance, att_angle, att_factor);

			// STIGMERGY
			if (s5.stigmergy) {

				// Vec3D fVec = new Vec3D(cos(ang)*fStrength,
				// sin(ang)*fStrength, 0);
				// unify actual velocity vector for other calculation
				agent.vel.normalize(); 
				// scatter as distance of future localization
				Vec3D futLoc = agent.futureLoc(s5.scatter); 
				Vec3D bestLoc = new Vec3D(); // default position

				// FIND MAX VALUE AROUND
				float val = -1; // default min value
				for (int i = 0; i < s5.nSamples; i++) {
					// scatter = distance radius
					Vec3D v = futLoc.add(new Vec3D(random(-s5.scatter, s5.scatter), random(-s5.scatter, s5.scatter), 0));
					float sampleVal = field.readValue(v);
					// float sampleVal = 10000000;
					if (sampleVal > val) {
						val = sampleVal;
						bestLoc = v;
					}
				}
				// stigmergic vector with scale
				Vec3D stigVec = bestLoc.sub(agent.loc).normalize().scale(s5.stigmergyStrength);
				agent.vel.addSelf(stigVec);// orient agent by stigmegic vector
											// (add velocity)
				agent.vel.normalize();// to do a constant speed

				agent.setMaxspeed(3);

				// spread
				field.addValue(agent.loc, 80);// spread pheromone

				// agent.update(); //move agent to new position

				// update agents location based on agentst calculations
				// normalized velocity vector (sum of vel + acc)(no acceleration)
				agent.interacting_update(true); 
			}

			if (s5.showDir) {

				// calculate future location of the agent

				Vec3D fLoc = agent.futureLoc(15); // future location
				stroke(255, 0, 0, 90); // r,g,b, alpha
				strokeWeight(1);

				agent.vLine(fLoc, agent.loc);
			}

			if (s5.attract) {// path following attraction
				// atraction to path spline
				Vec3D fLoc = agent.futureLoc(6);
				// agenti behaju sem a tam po spline
				float m = map(width, 0, width, -5, 0); 
				Vec3D cns = agent.closestNormalandDirectionToSpline(spline.sp_path, fLoc, m);
				agent.arrive(cns);
				agent.seek(cns, s5.factor);
			}

			if (s5.showTrails) {

				// vykreslenie chvosta ako linka - trajektoria

				stroke(random(255, 0), 0, 0, 90);
				strokeWeight(1);
				agent.drawTrail(200);
			}

			// update agents location based on agentst calculations
			agent.interacting_update(true); // normalized velocity vector (sum
											// of vel + acc)(no acceleration)

			// Add new volume from stored typologies
			if (addVolume) {
				agent.addVolume(map, Agent.PRIVATE);
				// addVolume = false;
			}

			if (addVolume_c) {
				agent.addVolume(map, Agent.CULTURE);

				// addVolume = false;
			}

			if (addVolume_sq) {
				agent.addVolume(map, Agent.SQUARE);

				// addVolume = false;
			}
			// agent.update();

			/*
			 * String locList = ""; //data stored in the list - as a string
			 * 
			 * 
			 * for (int i=0; i<pop;i++){ Agent n = agents.get(i); locList=
			 * locList+String.valueOf(-n.loc.x)+","+String.valueOf(n.loc.y)+";";
			 * 
			 * } //String message = agent.loc.x + "," + agent.loc.y; // the
			 * message to send String message = locList;
			 * 
			 * String ip = "127.0.0.1"; // the remote IP address int port =
			 * 7001; // the destination port print(message); udps.send( message,
			 * ip, port );
			 * 
			 * 
			 * 
			 * // print the result //println(
			 * "receive: \""+message+"\" from "+ip+" on port "+port );
			 */

			// Display the location of the agent with a point
			strokeWeight(3);// agent.participants*0.1);
			stroke(255);

			agent.displayPoint();
			// Display the direction of the agent with a line
			strokeWeight(1);
			stroke(100, 90);
			// //Display the direction of the agent with a line
			// strokeWeight(1);
			// stroke(100, 90);
			// agent.displayDir(agent.vel.magnitude()*3);
		}
		if (s5.decay < 1) field.decay(s5.decay);
		field.getImage().updatePixels();
		if (showPheromone) field.drawField();

		addVolume = false;
		addVolume_c = false;
		addVolume_sq = false;

		/*
		 * if(generate){
		 * 
		 * if (frameCount==res+iterT*approach||frameCount>res+iterT*approach) {
		 * //iteration approaching distance = based on frame_number addVolume =
		 * true; iterT+=1; }
		 * 
		 * 
		 * /* Vec3D fLoc1 = agent.futureLoc(5); distant =
		 * fLoc.distanceTo(agent.loc);
		 * 
		 * if (distant>180){ //definition of real approach distance addVolume =
		 * true; }
		 * 
		 * }
		 */

		// roads.draw();
		spline.draw(this);// test ok

		if (s5.objexport) {
			endRaw();
			s5.objexport = !s5.objexport;
		}

		if (s5.record) {
			endRaw();
			s5.record = !s5.record;
		}
		gui.gui();
		if (video)
			saveVideoFrame();

		/*
		 * if (frameCount==4000){ reset(); }
		 */

	}
	
	void saveVideoFrame() {
		saveFrame(dataPath(this.getClass().getName() + "_video10/" + this.getClass().getName() + "_#####.jpg"));
//		System.out.println("video recording");
	}

	void reset() {
		frameCount = 0;
		map.reset();
		init();
	}

	public void keyPressed() {

		if (key == 'r' || key == 'R') {
			reset();
		}

		if (key == 't' || key == 'T') {
			addVolume = true;
		}

		if (key == 'c' || key == 'C') {
			addVolume_c = true;
		}

		if (key == 'q' || key == 'Q') {
			addVolume_sq = true;
		}

		if (key == 'v')
			video = !video;
		if (key == 'b')
			Buildings = !Buildings;
		if (key == '1')
			mapdraw = !mapdraw;
		if (key == '2')
			mapdrawc = !mapdrawc;
		if (key == '3')
			mapdrawsq = !mapdrawsq;

		if (key == 'p' || key == 'P')
			showPheromone = !showPheromone;

		if (key == 'f' || key == 'F') {
			saveFrame("data/simulation_model-####.jpg");
			println("JPG generated succsessfully");
			s5.record = true;
		}
		if (key == 'w' || key == 'W')
			DRAW_ROADS = !DRAW_ROADS;

		if (key == 'a' || key == 'A') {
			iteratePopulations((i,size)->{
				Agent agent = new Agent(this, new Vec3D(Agent.initLocations[i]));
				agent.setVelocity(new Vec3D(random(-1, 0.5f), random(-1, 0.5f), 0));
				// todo - the velocity can be changed and can contribute to the  weights as well
				agents.add(agent);
				agent.update();
				agent.interacting_update(true);
			});

			// agent.interacting_update(true);
			// }
		}
	}
	
	public void settings() {
		smooth();
		size(1200, 800, P3D);
	}
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { EmCity.class.getName() });
	}

}