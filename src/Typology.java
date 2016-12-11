import java.util.ArrayList;

class Typology {
	ArrayList<Integer[]> points;
	
	Typology(ArrayList<Integer[]> points) {
		this.points = points;
	}

	public void createVolume(float x, float y, EMap map, int type) {
		int a_x = (int) Math.floor(x * 0.1);
		int a_y = (int) Math.floor(y * 0.1);

		if (points.size() > 0){
			Cluster cluster = Cluster.create(type);
			for (Integer[] point : points) {
				// Calculate cell position from typology, round agent position (/10 + 5)
				// HACK! cell_size
				Cell c = Cell.create(type, a_x * 10 + 5 + point[0], a_y * 10 + 5 + point[1], point[2], 10, 
						cluster); 
				if (map.addCellIfAbsent(c)) 
					cluster.addCell(c);
			}
			if (cluster.cellCount() > 0){
				map.clusters.add(cluster);
				cluster.init();
			}
		}
	}
}