import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Cluster {
	
	public static Cluster create(int type) {
		switch(type){
		case Agent.PRIVATE: return new Private();
		case Agent.CULTURE: return new Culture();
		case Agent.SQUARE: return new Square();
		default: return new Cluster();
		}
		
	}
	
	static public class Culture extends Cluster{
		Culture() {
			color = 0xff00BB00;
		}
		void agentInteraction(Agent agent) {
			super.agentInteraction(agent);
			collision = true;
		}
	}
	static public class Square extends Culture{
		Square() {
			color = 0xffA2B93A;
		}
	}
	
	public static class Private extends Cluster{
		Private() {
			color = 0xffFFC67C;
		}

		void agentInteraction(Agent agent) {
			super.agentInteraction(agent);
//			if (isFull()){
//				// attraction to culture activities
//				agent.attraction(Agent.att_distance, Agent.att_angle, Agent.att_factor, Agent.CULTURE); 
//				// attraction to square-park activities
//				agent.attraction(Agent.att_distance, Agent.att_angle, Agent.att_factor, Agent.SQUARE); 
//			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// FIELDS
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	List<Cell> cells;
	PVector attractor; // centroid - use TOXI Vec3D?

	int capacity;
	int occupation;
	int id; // not necessary (better orientation)
	boolean id_preview = false;
	boolean attraction;
	int color = 0;
	boolean collision = false;

	// TODO cluster_type = existing structure or extension
	// TODO activity_type

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	Cluster() {
		this.occupation = 0;
		cells = new LinkedList<>();
	}
	
	public void addCell(Cell c){
		cells.add(c);
	}
	
	public int cellCount(){
		return cells.size();
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	/** getAttraction and clusterCapacity;
	 * 
	 */
	void init() {
		this.capacity = 0;

		int sum_x = 0;
		int sum_y = 0;

		// loop for each cell in cluster
		for (Cell c: cells){
			sum_x += c.x;
			sum_y += c.y;
			capacity += c.capacity;
		}

		// centroid calculation - attraction point
		this.attractor = new PVector(sum_x / cells.size(), sum_y / cells.size());
		this.attraction = true;

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	void draw(PApplet p) {
		if (attractor == null)
			return;
		// Draw circle as attractor if..
		if (attraction) {
			p.pushMatrix();
			p.translate(0, 0, -0.2f);
			p.fill(color);
			p.ellipse(attractor.x, attractor.y, 7, 7);
			p.popMatrix();

		}
		// Draw cluster ID
		if (id_preview) {
			p.textSize(10);
			p.fill(255);
			p.text("id: " + id, attractor.x, attractor.y, cells.get(0).capacity + 0.5f);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	void agentInteraction(Agent agent) {

		// if cluster is not fully used yet
		if (!isFull()) {
			// colonize cluster's cells
			int rest_participants = colonize(agent.participants);

			// println("cluster_"+ id +": colonization (rest of agents: " + rest
			// +")");

			if (rest_participants > 0) {
				// println("cluster_"+ id +": agent splitting (new agent value
				// is "+rest+")");
				// agent -= rest;
				this.occupation = capacity;
				this.attraction = false;
				agent.participants = rest_participants;

			} else {
				// increase cluster occupation
				this.occupation += agent.participants;
				agent.participants = 0;
				agent.is_active = false;

			}
		} else {
			// println("cluster_"+ id +": obstacle (cells of cluster are
			// obstacles for agents)");
			agent.collision();
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	int colonize(int colonize_by) {
		int rest = colonize_by;
		for (int i = 0; i < cells.size(); i++) {
			rest = cells.get(i).colonize(rest);
			if (rest < 0)
				break;
		}
		return rest;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	boolean isFull() {
		return occupation >= capacity;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	void empty() {
		for (int i = 0; i < cells.size(); i++) {
			cells.get(i).resetCapacity();
		}
		this.occupation = 0;
		this.attraction = true;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	void fill() {
		// fill empty cells gradually (if empty add agent energy until run out
		// of it)
		for (int i = 0; i < cells.size(); i++) {
			// if cell is not full, colonize it
			cells.get(i).fillCapacity();
		}
		this.occupation = capacity;
		this.attraction = false;

		// occupancy ratio (if capcaity is full - ratio 1:1 - remove attraction)
		// if existing structure > and capacity is fulfilled > create new
		// extended cluster
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	
}