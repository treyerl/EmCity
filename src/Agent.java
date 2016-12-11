import plethora.core.Ple_Agent;
import toxi.geom.Vec3D;

class Agent extends Ple_Agent {

	public static final int PRIVATE = 0;
	public static final int CULTURE = 1;
	public static final int SQUARE = 2;
	
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

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// FIELD
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	int participants;
	boolean is_active;
	int counter;

	float distance;
	boolean add = false;

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
		if (counter >= P.s5.approach || P.key == 't' || P.key == 'T') {
			map.typologies.get((int) (Math.random() * (map.typologies.size() - 1)))
				.createVolume(this.loc.x, this.loc.y, map, type);
		}

	}

	
	/**
	 * @param max_distance
	 * @param max_angle
	 * @param attraction_factor
	 * @param type
	 */
	void attraction(float max_distance, float max_angle, float attraction_factor, EMap map, int type) {
		add = false;
		P.map.clusters.stream().filter(c -> P.map.getClusterClass(type).isAssignableFrom(c.getClass()))
			.forEach(c -> {
			if (c.attraction){
				Vec3D target = new Vec3D(c.attractor.x, c.attractor.y, 0);
				Vec3D direction = target.sub(this.loc);
				float orientation_angle = direction.angleBetween(this.vel, true);
				// println("orientation is: " +orientation_angle);
				distance = target.distanceTo(this.loc);
				if (distance < max_distance && orientation_angle < max_angle) {
					this.seek(target, (max_distance - distance) * attraction_factor);
					if (P.s5.show_distance) {
						P.noStroke();
						P.fill(0xE8C365);
						P.ellipse(this.loc.x, this.loc.y, distance, distance);
					}
				}

				if (P.s5.generate) {
//					// if (counter>=approach) { //approach distance condition
//					switch(type){
//					case Agent.PRIVATE:
//						P.addVolume = true;
//						break;
//					case Agent.CULTURE:
//						P.addVolume_c = true;
//						break;
//					case Agent.SQUARE:
//						P.addVolume_sq = true;
//						break;
//					}
					add = true;
				}
			}
		});
		if (add) {
			addVolume(map, type);
		}
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