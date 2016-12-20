package emcity;
import java.util.List;

import processing.core.PApplet;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

/**A class holding a list of toxi.geom.Spline3D which it is able to draw to a PApplet canvas
 * by connecting their respective points with a PApplet.line()
 * @author Lukas Treyer
 *
 */
public class DrawingSplines {
	
	List<Spline3D> splines;
	
	/**Set the splines
	 * @param splines List&lt;Spline3D&gt;
	 */
	public void setSplines(List<Spline3D> splines){
		this.splines = splines;
	}
	
	/**Draw the connecting straight lines between spline points.
	 * @param p PApplet - Processing Applet
	 */
	public void drawSplines(PApplet p){
		for (Spline3D spl: splines){
			Vec3D last = spl.getPointList().get(0);
			for (int i = 1; i < spl.getPointList().size(); i++){
				Vec3D P = spl.getPointList().get(i);
				p.line(last.x, last.y, P.x, P.y);
				last = P;
			}
		}
	}
}
