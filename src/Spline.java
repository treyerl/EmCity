import processing.core.PApplet;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

class Spline {
	Spline3D spline;
	
	public void setSpline(Spline3D s){
		spline = s;
	}

	void draw(PApplet p) {
		p.stroke(0, 90);
		for (int i= 1; i<spline.pointList.size(); i++) {
			Vec3D a = spline.pointList.get(i);
			Vec3D b = spline.pointList.get(i-1);
			p.line(a.x, a.y, a.z, b.x, b.y, b.z);
		}
	}
}