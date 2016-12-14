import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import toxi.geom.Vec3D;

//import java.io.*;
class EMap {
	Map<Long, Cell> cells;
	List<Cluster> clusters;
	ArrayList<Typology> typologies;
	public int sum_area;

	
	public EMap() {
		this.cells = new HashMap<>();
		this.clusters = new LinkedList<>();
		this.typologies = new ArrayList<>();
	}
	
	static long xy2long(float x, float y) {
		return (((long) ((int)x * 0.1)) << 32) + ((int) (y * 0.1));
	}
	
	public Cell addCell(Cell cell) {
		return cells.put(xy2long(cell.x, cell.y), cell); // HACK - hashMap > treeMap
	}

	void addCluster(Cluster clusters) {
		this.clusters.add(clusters);
	}

	void addTypologies(List<Typology> typologies) {
		this.typologies.addAll(typologies);
	}
	
	public int countCells(int type){
		return (int) cells.values().stream().filter(c -> c.is(type)).count();
	}
	
	public int countClusters(int type){
		return (int) clusters.stream().filter(c -> c.is(type)).count();
	}
	
	/**makes agent interact with cells and clusters
	 * @param agent
	 * @param test_coming_loc
	 */
	public void interact(Agent agent, boolean test_coming_loc) {
		
		Vec3D loc = test_coming_loc ? agent.coming_loc : agent.loc;
		long a_key = xy2long(loc.x, loc.y);
		
		Cell cell = cells.get(a_key);
		if (cell != null) {
			// interact with cluster
			cell.cluster.agentInteraction(agent);
		}
	}

	// HACK with addCell method
	boolean addCellIfAbsent(Cell cell){
		return cells.putIfAbsent(xy2long(cell.x, cell.y), cell) == null;
	}

	/**Draws all cells and clusters using the PApplet p
	 * @param p
	 * @param type
	 */
	void draw(PApplet p, int type) {
		cells.values().stream()
			.filter(c -> c.is(type))
			.forEach(c -> c.draw(p));
		clusters.stream()
			.filter(c -> c.is(type))
			.forEach(c -> c.draw(p));
	}

	void reset() {
		this.cells = new HashMap<Long, Cell>();
		this.clusters = new LinkedList<Cluster>();
		this.typologies = new ArrayList<Typology>();
	}

}