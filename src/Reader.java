import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import processing.core.PVector;

class Reader implements LineReader {

//	public void readClusters(String path, int cell_size, EMap map) {
//		String txt_clusters[] = lineArray(path);
//		if (txt_clusters == null) {
//			System.out.println(path + " file is missing!");
//			return;
//		}
//		// Get clusters from file
//		ArrayList<Cluster> cluster_list = new ArrayList<Cluster>(txt_clusters.length);
//		for (int i = 0; i < txt_clusters.length; i++) {
//			String txt_cells[] = txt_clusters[i].split(" ");
//			// Get cluster cells
//			ArrayList<Cell> cluster_cells = new ArrayList<Cell>(txt_cells.length / 3);
//			for (int j = 0; j < txt_cells.length - 1; j += 3) {
//				int x = Integer.parseInt(txt_cells[j]);
//				int y = -(Integer.parseInt(txt_cells[j + 1]));
//				int z = Integer.parseInt(txt_cells[j + 2]);
//				Cell cell = new Cell.Private(x, y, z, cell_size, i);
//				cluster_cells.add(cell);
//				map.addCell(cell);
//
//			}
//			// Create cluster and store it into a list
//			Cluster cluster = new Cluster.Private(cluster_cells, i);
//			cluster_list.add(cluster);
//		}
//
//		map.addAllClusters(cluster_list);
//	}
//
//	public void readCulture_Clusters(String path, int cell_c_size, EMap map) {
//		String txt_clusters_c[] = lineArray(path);
//		if (txt_clusters_c == null) {
//			System.out.println(path + " file is missing!");
//			return;
//		}
//		// Get clusters from file
//		ArrayList<Cluster> cluster_list_c = new ArrayList<>(txt_clusters_c.length);
//		for (int i = 0; i < txt_clusters_c.length; i++) {
//			String txt_cells_c[] = txt_clusters_c[i].split(" ");
//
//			// Get cluster cells
//			ArrayList<Cell> cluster_cells_c = new ArrayList<>(txt_cells_c.length / 3);
//			for (int j = 0; j < txt_cells_c.length - 1; j += 3) {
//				int x = Integer.parseInt(txt_cells_c[j]);
//				int y = -(Integer.parseInt(txt_cells_c[j + 1]));
//				int z = Integer.parseInt(txt_cells_c[j + 2]);
//
//				Cell.Culture cell_c = new Cell.Culture(x, y, z, cell_c_size, i);
//				cluster_cells_c.add(cell_c);
//				// Register cell in map
//				map.addCell(cell_c);
//			}
//			// Create cluster and store it into a list
//			Cluster.Culture cluster_c = new Cluster.Culture(cluster_cells_c, i);
//			cluster_list_c.add(cluster_c);
//		}
//		map.addAllClusters(cluster_list_c);
//	}
	
	private int i(String s){
		return Integer.parseInt(s);
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public void readCluster(String path, int cell_size, EMap map, int type) {
		try {
			eachLine(path, (i,line)->{
				Cluster cluster = Cluster.create(type);
				String[] n = line.split(" ");
				for (int j = 0; j < n.length - 1; j += 3) {
					Cell c = Cell.create(type, i(n[j]), -i(n[j+1]), i(n[j+2]), cell_size, cluster);
					cluster.addCell(c);
					map.addCell(c);
				}
				map.addCluster(cluster);
				cluster.init();
			});
		} catch (IOException e) {
			System.out.printf("file %s is missing!%n", path);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////

	public void readTypology(String path, EMap map) {
		String txt_typology[] = lineArray(path);
		if (txt_typology == null) {
			System.out.println(path + " file is missing!");
			return;
		}
		// Get list of typology from file
		ArrayList<Typology> typology_list = new ArrayList<Typology>(txt_typology.length);
		for (int i = 0; i < txt_typology.length; i++) {
			String txt_cells[] = txt_typology[i].split(" ");

			// Get one typology cells
			ArrayList<Integer[]> points = new ArrayList<Integer[]>(txt_cells.length / 3);
			int base_x = 0;
			int base_y = 0;
			for (int j = 0; j < txt_cells.length - 1; j += 3) {
				int x = Integer.parseInt(txt_cells[j]);
				int y = -(Integer.parseInt(txt_cells[j + 1]));
				int z = Integer.parseInt(txt_cells[j + 2]);

				if (j == 0) {
					base_x = x;
					base_y = y;

					points.add(new Integer[] { 0, 0, z });
				} else {
					points.add(new Integer[] { base_x - x, base_y - y, z });
				}
			}
			Typology typology = new Typology(points);
			typology_list.add(typology);
		}
		map.addTypologies(typology_list);
	}

	public PVector[] import_GH(String path) { // String_for_import_points_for_path_following_splie
		String GH_PT[] = lineArray(path);
		if (GH_PT != null) {
			return Arrays.stream(GH_PT).map((s) -> s.split(" "))
					.map(s -> new PVector(Float.parseFloat(s[0]), Float.parseFloat(s[1]))).toArray(PVector[]::new);
		}
		return null;
	}

}