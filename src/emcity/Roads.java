package emcity;
/**@see DrawingSplines
*
*/
class Roads extends DrawingSplines{
	
	public void draw(EmCity p) {
		p.stroke(GUI.txtCol, 150);
		p.strokeWeight(1);
		drawSplines(p);
	}
}