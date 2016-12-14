
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	private Reader read;
	private Spline spline;
	private Buildings2D buildings;
	private Roads roads;
	private int i = 0;
	Settings s5;
	
	//GLOBAL VARIABLES
	PImage stigIm = createImage(2400,2400,ARGB);
	Field field = new Field(stigIm, this);


	ArrayList<Ple_Agent> agents;
	// int pop = 10; //80 agents = 10*8
	boolean[] addVolume = new boolean[Agent.numCategories];
	boolean[] mapdraw = new boolean[Agent.numCategories];
	static final int cell_size_b = 10;
	static final int cell_size_park = 10;

	boolean drawBuildings = true;
	boolean saveVideo = false;
	boolean reset_button = false;
	boolean DRAW_ROADS = true;

	int res = 0;
	int iterT = 1;
	float distant;

	public void setup() {
		// fullScreen();
		frameRate(50);
		gui = new GUI(this);
		Arrays.fill(mapdraw, true);
		init();
	}
	
	private void init(){
		s5 = new Settings();
		map = new EMap();
		read = new Reader();
		roads = new Roads();
		spline = new Spline();
		buildings = new Buildings2D();
		try {
			read.cluster(read.lines("data/clusters.txt"), cell_size_b, map, Agent.PRIVATE);
			read.cluster(read.lines("data/culture_clusters.txt"), cell_size_b, map, Agent.CULTURE);
			read.cluster(read.lines("data/square_clusters.txt"), cell_size_park, map, Agent.SQUARE);
			map.addTypologies(read.typologies(read.lines("data/typologies.txt"), map));
			spline.setSpline(read.ghSpline(read.lines("data/path_following_line.txt")));
			buildings.setSplines(read.splines(
					read.points(read.lines("data/budovy_body.txt")), read.lines("data/budovy_zoznam.txt"), true));
			roads.setSplines(read.splines(
					read.points(read.lines("data/roads_body.txt")), read.lines("data/roads_index.txt"), false));
			setupAgents();
		} catch (IOException e){
			e.printStackTrace();
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
			// TODO - the velocity can be changed and can contribute to the weights as well
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
		Agent.drawPoints(this);

		if (drawBuildings){
			buildings.draw(this);
		}
		
		for (int i = 1; i < Agent.numCategories ; i++){
			// i = 0 = ALL   i = 1 = PRIVATE   i = 2 = CULTURE   i = 3 = SQUARE
			if (mapdraw[i]) map.draw(this, i);
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
			agent.attraction(s5.att_distance, s5.att_angle, s5.att_factor, map);

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
				Vec3D cns = agent.closestNormalandDirectionToSpline(spline.spline, fLoc, m);
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
			// normalized velocity vector (sum of vel + acc)(no acceleration)
			agent.interacting_update(true); 
			
			for (int i = 1; i < Agent.numCategories; i++){
				if (addVolume[i]) agent.addVolume(map, i);
			}

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
		if (s5.showPheromone) field.drawField();

		// reset all addVolume values to false
		for (int i = 0; i < addVolume.length; ++i){
			addVolume[i] = false;
		}

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

		roads.draw(this);
		//spline.draw(this);// test ok

		if (s5.objexport) {
			endRaw();
			s5.objexport = !s5.objexport;
		}

		if (s5.record) {
			endRaw();
			s5.record = !s5.record;
		}
		gui.gui();
		if (saveVideo)
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
		
		if (key == 't' || key == 'T'){
			i = 0;
			try {
				read.lines("data/typologies.txt").forEachOrdered(s -> {
					map.typologies.get(i++).setPoints(read.typologyPointsFromString(s), map);
				});
				while (map.typologies.size() > i){
					Typology t = map.typologies.remove(i);
					for (Cluster cl: t.usingMe){
						for (Cell c: cl.cells){
							map.cells.remove(EMap.xy2long(c.x, c.y));
						}
						map.clusters.remove(cl);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (key == 'i' || key == 'I') {
			addVolume[Agent.PRIVATE] = !addVolume[Agent.PRIVATE];
		}

		if (key == 'c' || key == 'C') {
			addVolume[Agent.CULTURE] = !addVolume[Agent.CULTURE];
		}

		if (key == 'q' || key == 'Q') {
			addVolume[Agent.SQUARE] = !addVolume[Agent.SQUARE];
		}

		if (key == 'v')
			saveVideo = !saveVideo;
		if (key == 'b')
			drawBuildings = !drawBuildings;
		if (key == '1')
			mapdraw[1] = !mapdraw[1]; // 1 = Agent.PRIVATE
		if (key == '2')
			mapdraw[2] = !mapdraw[2]; // 2 = Agent.CULTURE
		if (key == '3')
			mapdraw[3] = !mapdraw[3]; // 3 = Agent.SQUARE

		if (key == 'p' || key == 'P')
			s5.showPheromone = !s5.showPheromone;

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
				// TODO - the velocity can be changed and can contribute to the  weights as well
				agents.add(agent);
//				agent.update();
//				agent.interacting_update(true);
			});
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