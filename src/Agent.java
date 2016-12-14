import plethora.core.Ple_Agent;
import processing.core.PApplet;
import toxi.geom.Vec3D;

class Agent extends Ple_Agent {

	public static final int PRIVATE = 1;
	public static final int CULTURE = 2;
	public static final int SQUARE = 3;
	
	public static final int numCategories = 4;
	
	public interface Type {
		default boolean is(int type){
			return type == 0;
		}
		
		default int getType(){
			return 0;
		}
	}
	
	public interface Private extends Type{
		default boolean is(int type){
			return type == PRIVATE;
		}
		
		default int getType(){
			return PRIVATE;
		}
	}
	
	public interface Culture extends Type{
		default boolean is(int type){
			return type == CULTURE;
		}
		
		default int getType(){
			return CULTURE;
		}
	}
	
	public interface Square extends Type{
		default boolean is(int type){
			return type == SQUARE;
		}
		
		default int getType(){
			return SQUARE;
		}
	}
	
	public static final int[] populationSizes = new int[]{
		10, // pop 1
		20, // pop 2
		25, // pop 3
		30, // pop 4
		30, // pop 5
		40, // pop 6
		45, // pop 7
		50  // pop 8
	};
	
	public static final Vec3D[] initLocations = new Vec3D[]{
		new Vec3D(-987, -180.95f, 0),	//pop1
		new Vec3D(1381, 0, 0),			//pop2
		new Vec3D(158, 845, 0),			//pop3
		new Vec3D(576, -332, 0),		//pop4
		new Vec3D(-1020, 596, 0),		//pop5
		new Vec3D(460, 392, 0),			//pop6
		new Vec3D(-131, -289, 0),		//pop7
		new Vec3D(1283, -482, 0)		//pop8
	};

	public static void drawPoints(PApplet p){
		// draw initialization points for agents
		p.ellipseMode(PApplet.CENTER);
		p.fill(255, 132, 0, 90);// Set fill to orange
		p.stroke(255, 100, 0); // Set stroke to deep orange
		p.strokeWeight(3);
		// ellipse(mouseX-width/2, mouseY-height/2, 15, 15);

		/*
		 * ellipse(-407, 37, 15, 15); ellipse(125, 225, 15, 15); ellipse(158,
		 * -282, 15, 15); ellipse(576, 433, 15, 15); ellipse(288, 596, 15, 15);
		 * ellipse(752, 606, 15, 15); ellipse(879, -289, 15, 15); ellipse(-304,
		 * 474, 15, 15);
		 */
		for (Vec3D v: Agent.initLocations){
			p.ellipse(v.x, v.y, 15, 15);
		}
	}
	
	int participants;
	boolean is_active;
	int counter;

	float distance;

	EmCity P;

	Vec3D coming_loc;
	Vec3D target_c;

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	Agent(EmCity _p5, Vec3D _loc) {
		super(_p5, _loc);
		this.P = _p5;
		this.participants = 500;
		this.is_active = true;
		this.counter = 0;

		this.coming_loc = null;

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// ADD VOLUME
	void addVolume(EMap map, int type) {
		// create new cluster (cells) on agent position
		map.typologies.get((int) (Math.random() * (map.typologies.size() - 1)))
			.createVolume(this.loc.x, this.loc.y, map, type);
	}

	
	/**
	 * @param max_distance
	 * @param max_angle
	 * @param attraction_factor
	 * @param type
	 */
	void attraction(float max_distance, float max_angle, float attraction_factor, EMap map) {

		P.map.clusters.stream().forEach(c -> {
			if (c.attraction){
				Vec3D target = new Vec3D(c.attractor.x, c.attractor.y, 0);
				Vec3D direction = target.sub(this.loc);
				float orientation_angle = direction.angleBetween(this.vel, true);
				// println("orientation is: " +orientation_angle);
				distance = target.distanceTo(this.loc);
				if (distance < max_distance && orientation_angle < max_angle) {
					this.seek(target, (max_distance - distance) * attraction_factor);
					if (P.s5.show_att_distance) {
	
						P.noStroke();
						P.fill(0xFFE8C365);
						P.ellipse(this.loc.x, this.loc.y, distance, distance);
					}
				}

				if (P.s5.generate && counter >= P.s5.approach) {
					P.addVolume[c.getType()] = true;
					if (c.getType() != Agent.PRIVATE){
						counter = 0;
					}
				}
			}
		});
	}

	// UPDATE NORMALIZED + INTERACT(test coming position)
	void interacting_update(boolean normalized) {
		// Update velocity
		vel.addSelf(acc);
		counter++;
		// println(counter);//only test

		// Limit speed
		vel.limit(maxspeed);

		// Test coming position
		coming_loc = loc.add(vel);
		P.map.interact(this, true);

		if (normalized)
			vel.normalize();

		loc.addSelf(vel);
		loc.addSelf(vel);
		// Reset accelertion to 0 each cycle
		acc.clear();
	}

	// COLLISION - change direction
	void collision() {

		this.vel.x += P.random(-10, 10);
		this.vel.y += P.random(-10, 10);
		counter = 0;

		/// this.vel = this.vel.normalize();
		// TODO test new position until finds empty cell - correct direction
		/// (loc+vel to key -> vel)
	}
}