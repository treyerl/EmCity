import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import processing.core.PApplet;
import toxi.geom.Vec3D;

//import java.io.*;
class EMap {
	TreeMap<Long, Cell> cell_grid;
	List<Cluster> clusters;
	ArrayList<Typology> typologies;
	public int sum_area;

	
	public EMap() {
		this.cell_grid = new TreeMap<>();
		this.clusters = new LinkedList<>();
		this.typologies = new ArrayList<>();
	}
	
	static long xy2long(float x, float y) {
		return (((long) ((int)x * 0.1)) << 32) + ((int) (y * 0.1));
	}
	
	public Cell addCell(Cell cell) {
		return cell_grid.put(xy2long(cell.x, cell.y), cell); // HACK - hashMap > treeMap
	}

	void addCluster(Cluster clusters) {
		this.clusters.add(clusters);
	}

	void addTypologies(List<Typology> typologies) {
		this.typologies.addAll(typologies);
	}
	
	public int countCells(int type){
		return (int) cell_grid.values().stream().filter(c -> getCellClass(type).isAssignableFrom(c.getClass())).count();
	}
	
	public int countClusters(int type){
		return (int) clusters.stream().filter(c -> getClusterClass(type).isAssignableFrom(c.getClass())).count();
	}
	
	/**makes agent interact with cells and clusters
	 * @param agent
	 * @param test_coming_loc
	 */
	public void interact(Agent agent, boolean test_coming_loc) {
		
		Vec3D loc = test_coming_loc ? agent.coming_loc : agent.loc;
		long a_key = xy2long(loc.x, loc.y);
		
		Cell cell = cell_grid.get(a_key);
		if (cell != null) {
			// interact with cluster
			cell.cluster.agentInteraction(agent);
		}
	}

	// HACK with addCell method
	boolean addCellIfAbsent(Cell cell){
		return cell_grid.putIfAbsent(xy2long(cell.x, cell.y), cell) == null;
	}

	/**Draws all cells and clusters using the PApplet p
	 * @param p
	 * @param type
	 */
	void draw(PApplet p, int type) {
		cell_grid.values().stream()
			.filter(c -> getCellClass(type).isAssignableFrom(c.getClass()))
			.forEach(c -> c.draw(p));
		clusters.stream()
			.filter(c -> getClusterClass(type).isAssignableFrom(c.getClass()))
			.forEach(c -> c.draw(p));
	}

	public Class<? extends Cell> getCellClass(int type){
		switch(type){
		case Agent.PRIVATE: return Cell.Private.class;
		case Agent.CULTURE: return Cell.Culture.class;
		case Agent.SQUARE: return Cell.Square.class;
		default: return Cell.class;
		}
	}
	
	public Class<? extends Cluster> getClusterClass(int type){
		switch(type){
		case Agent.PRIVATE: return Cluster.Private.class;
		case Agent.CULTURE: return Cluster.Culture.class;
		case Agent.SQUARE: return Cluster.Square.class;
		default: return Cluster.class;
		}
	}

	void reset() {
		this.cell_grid = new TreeMap<Long, Cell>();
		this.clusters = new ArrayList<Cluster>();
		this.typologies = new ArrayList<Typology>();
	}

}