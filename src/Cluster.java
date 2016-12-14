import java.util.LinkedList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Cluster implements Agent.Type, Colonizeable {
	
	public static Cluster create(int type) {
		switch(type){
		case Agent.PRIVATE: return new Private();
		case Agent.CULTURE: return new Culture();
		case Agent.SQUARE: return new Square();
		default: return new Cluster();
		}
	}
	
	static public class Culture extends Cluster implements Agent.Culture{
		Culture() {
			color = 0xff00BB00;
		}
	}
	static public class Square extends Cluster implements Agent.Square{
		Square() {
			color = 0xffA2B93A;
		}
	}
	
	public static class Private extends Cluster implements Agent.Private{
		Private() {
			color = 0xffFFC67C;
		}

		void agentInteraction(Agent agent) {
			super.agentInteraction(agent);
		}
	}
	
	List<Cell> cells;
	PVector attractor; // centroid - use TOXI Vec3D?

	int capacity;
	int occupation;
	boolean attraction;
	int color = 0;
	int[] center = new int[2];

	// TODO cluster_type = existing structure or extension
	// TODO activity_type

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
	
	public void setCenter(int x, int y){
		center[0] = x;
		center[1] = y;
	}
	
	public void setPoints(List<int[]> points, EMap map){
		int x = center[0];
		int y = center[1];
		int type = getType();
		for (int[] point : points) {
			
			int capacity = point[2];
			int size = 10;
			if (type == Agent.SQUARE){
				capacity = 1;
			}

			Cell c = Cell.create(type, x + point[0], y + point[1], capacity, size, this); 
			if (map.addCellIfAbsent(c)){
				addCell(c);
			}
		}
	}
	
	/** set Attraction and clusterCapacity;
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
	}

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
			agent.collision();
		}
	}

	public int colonize(int colonize_by) {
		int rest = colonize_by;
		for (int i = 0; i < cells.size(); i++) {
			rest = cells.get(i).colonize(rest);
			if (rest < 0)
				break;
		}
		return rest;
	}

	public boolean isFull() {
		return occupation >= capacity;
	}

	public void empty() {
		for (int i = 0; i < cells.size(); i++) {
			cells.get(i).empty();
		}
		this.occupation = 0;
		this.attraction = true;
	}

	public void fill() {
		// fill empty cells gradually (if empty add agent energy until run out of it)
		for (int i = 0; i < cells.size(); i++) {
			// if cell is not full, colonize it
			cells.get(i).fill();
		}
		this.occupation = capacity;
		this.attraction = false;

		// occupancy ratio (if capcaity is full - ratio 1:1 - remove attraction)
		// if existing structure > and capacity is fulfilled > create new
		// extended cluster
	}
}