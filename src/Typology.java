import java.util.LinkedList;
import java.util.List;

class Typology {
	List<int[]> points;
	List<Cluster> usingMe = new LinkedList<>();
	
	public Typology setPoints(List<int[]> p, EMap map) {
		this.points = p;
		for (Cluster cl: usingMe){
			for (Cell cell: cl.cells) map.cells.remove(cell);
			cl.cells.clear();
			cl.setPoints(p, map);
		}
		return this;
	}

	public void createVolume(float x, float y, EMap map, int type) {
		// round agent position (/10 + 5)
		int a_x = (int) Math.floor(x * 0.1) * 10 + 5;
		int a_y = (int) Math.floor(y * 0.1) * 10 + 5;
		

		if (points.size() > 0){
			Cluster cluster = Cluster.create(type);
			cluster.setCenter(a_x, a_y);
			cluster.setPoints(points, map);
			if (cluster.cellCount() > 0){
				map.clusters.add(cluster);
				cluster.init();
				usingMe.add(cluster);
			}
		}
	}
}