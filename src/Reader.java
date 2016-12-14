import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

class Reader{
	private int i = 0;
	private float c1 = 0f;
	/**Parsing String to int
	 * @param s
	 * @return
	 */
	private int i(String s){
		return Integer.parseInt(s);
	}
	
	/**Parses a String to float
	 * @param s
	 * @return
	 */
	private float f(String s){
		return Float.parseFloat(s);
	}
	
	/**Create a Stream of string lines from a file path
	 * @param path Sring
	 * @return String&lt;String&gt;
	 * @throws IOException
	 */
	public Stream<String> lines(String path) throws IOException{
		return Files.lines(Paths.get(path));
	}
	
	/**Creates a String&lt;String&gt; from a ByteBuffer holding the bytes of a "file" 
	 * (or any other byte source)
	 * @param bb ByteBuffer
	 * @return String&lt;String&gt;
	 */
	public Stream<String> lines(ByteBuffer bb){
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bb.array()))).lines();
	}
	
	/**Import existing buildings; one building per line; 
	 * consisting of a point grid that is being transformed into a cell grid.
	 * @param lines Stream&lt;String&gt; 
	 * @param cellSize int - the size of the cells
	 * @param map EMap to which cells and clusters should be added
	 * @param type int - one of the static constants defined in Agent [PRIVATE, CULTURE, SQUARE]
	 */
	public void cluster(Stream<String> lines, int cellSize, EMap map, int type){
		lines.forEach(line -> {
			Cluster cluster = Cluster.create(type);
			String[] n = line.split(" ");
			for (int j = 0; j < n.length - 1; j += 3) {
				Cell c = Cell.create(type, i(n[j]), -i(n[j+1]), i(n[j+2]), cellSize, cluster);
				cluster.addCell(c);
				map.addCell(c);
			}
			map.addCluster(cluster);
			cluster.init();
		});
	}
	
	/**read typologies from a text source
	 * @param lines - Stream&lt;String&gt;
	 * @return List&lt;Typology&gt; containing all Typologies
	 */
	public List<Typology> typologies(Stream<String> lines, EMap map){
		return lines
				.map(line -> new Typology().setPoints(typologyPointsFromString(line), map))
				.collect(Collectors.toList());
	}
	
	/**converts a string with whitespace separated numbers into a list of integer points (int[]{x,y,z})
	 * @param line
	 * @return
	 */
	public List<int[]> typologyPointsFromString(String line){
		String t[] = line.split(" ");
		int baseX = i(t[0]), baseY = i(t[1]);
		List<int[]> p = new LinkedList<>();
		p.add(new int[]{0,0,i(t[2])});
		for (int i = 3; i < t.length; i+=3){
			p.add(new int[]{baseX - i(t[i]), baseY - i(t[i+1]), i(t[i+2])});
		}
		return p;
	}
	
	/**Read all points of a spline_body.txt file
	 * @param pointStream Stream&lt;String&gt;
	 * @return List&lt;Vec3D&gt;
	 */
	public List<Vec3D> points(Stream<String> pointStream){
		List<Vec3D> points = new LinkedList<>();
		i = 0;
		pointStream.forEachOrdered(c -> {
			if (i++ % 2 == 0){
				c1 = f(c);
			} else {
				points.add(new Vec3D(c1, -f(c), 0));
			}
		});
		return points;
	}

	/**Read all indices of a spline_index.txt file and create a List&lt;Spline3D&gt; using the List&lt;Vec3D&gt; 
	 * @param points List&lt;Vec3D&gt; 
	 * @param indexStream Stream&lt;String&gt;
	 * @param close boolean that indicates whether the splines should be closed
	 * @return List&lt;Spline3D&gt;
	 */
	public List<Spline3D> splines(List<Vec3D> points, Stream<String> indexStream, boolean close){
		System.out.println(points.size());
		Iterator<Vec3D> pit = points.iterator();
		i = 0;
		return indexStream.map(idx -> {
			int end = i + i(idx);
			Spline3D spl = new Spline3D();
			while(i++ < end && pit.hasNext()){
				spl.add(pit.next().copy());
				if (i == end - 1 && close){
					spl.add(spl.getPointList().get(0));
				}
			}
			return spl;
		}).collect(Collectors.toList());
	}
	
	/**
	 * @param strstr Stream&lt;String&gt;
	 * @return Spline3D
	 */
	public Spline3D ghSpline(Stream<String> strstr){
		return new Spline3D(strstr
				.map(s -> s.split(" "))
				.map(s -> new Vec3D(Float.parseFloat(s[0]), Float.parseFloat(s[1]), 0))
				.collect(Collectors.toList()));
	}
	
}