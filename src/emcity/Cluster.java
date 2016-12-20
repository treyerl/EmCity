package emcity;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;

import processing.core.PApplet;
import processing.core.PVector;

public class Cluster implements Agent.Type, Colonizeable {
	public final static GeometryFactory gf = new GeometryFactory();
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
	int maxHeight = 0;
	int occupation;
	boolean attraction;
	int color = 0;
	int[] center = new int[2];
	private int luciID = 0;

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
	
	public void setPoints(List<int[]> points, Map<Long, Cell> allCells){
		int x = center[0];
		int y = center[1];
		int type = getType();
		cells.clear();
		for (int[] point : points) {
			int capacity = point[2];
			int size = 10;
			if (type == Agent.SQUARE){
				capacity = 1;
			}
			Cell c = Cell.create(type, x + point[0], y + point[1], capacity, size, this); 
			if (allCells.putIfAbsent(c.getLocationKey(), c) == null){
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
			if (c.capacity > maxHeight){
				maxHeight = c.capacity;
			}
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
	
	public List<List<List<double[]>>> getSurfacePolygons(){
		List<List<List<double[]>>> faces = new LinkedList<>();
		List<double[]> footprint = getFootprint(); //.reduceToCorners();
		if (footprint == null) return null;
		List<double[]> roof = footprint.stream()
				.map(p -> new double[]{p[0], p[1], -maxHeight})
				.collect(Collectors.toList()); 
		faces.add(wrap(footprint));
		for (int i = 1; i < footprint.size(); i++){
			List<double[]> side = new LinkedList<>();
			side.add(footprint.get(i - 1));
			side.add(footprint.get(i));
			side.add(roof.get(i));
			side.add(roof.get(i - 1));
			faces.add(wrap(side));
		}
		faces.add(wrap(roof));
		return faces;
	}
	
	public List<double[]> getFootprint(){
		Geometry g = CascadedPolygonUnion.union(cells.stream()
				.map(c -> new Polygon(new LinearRing(new CoordinateArraySequence(
						c.getFootPrint().stream().map(i -> new Coordinate(i[0], i[1])).toArray(Coordinate[]::new)), gf), null, gf))
				.collect(Collectors.toList()));
		if (g.getGeometryType() == "Polygon") {
			Polygon p = (Polygon) g;
			return Arrays.stream(p.getExteriorRing().getCoordinates())
					.map(c -> new double[]{c.x, c.y, 0})
					.collect(Collectors.toList());
		} 
		return null;
	}
	
	private List<List<double[]>> wrap(List<double[]> o){
		List<List<double[]>> l = new LinkedList<>();
		l.add(o);
		return l;
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

	public int getLuciID() {
		return luciID ;
	}
}