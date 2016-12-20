package emcity;
import processing.core.PApplet;

/**@see DrawingSplines
 *
 */
class Buildings2D extends DrawingSplines{
	
	int blCol = 100;
	
	/**Draw buildings
	 * @param p - PApplet
	 */
	void draw(PApplet p) {
		// pushMatrix();
		// translate(-625, 15, 0);
		p.stroke(GUI.txtCol); //added for black bg
		p.strokeWeight(1);
		drawSplines(p);
		p.stroke(p.color(blCol), 100);
		p.fill(p.color(blCol), 100);
		//  popMatrix();
	}
}