import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

class Spline {
	public static int pointSize = 1941;
	Spline3D sp_path;
	// Spline_definition_for_path_following_test
	
	public boolean load(PVector[] GHpoints) {
		if (GHpoints == null) return false;
		sp_path = new Spline3D();//deklaracia - nazov krivky
		for (int i=0; i<pointSize; i++) {//vytvor body, je ich taky a taky pocet=musi sediet s Grassom
			PVector a = GHpoints[i];
			Vec3D v =new Vec3D(a.x, a.y, 0);//tu sa mi budu nacitavat suradnice bodov (z Grasshoppera GH_Send)
			sp_path.add(v);
		}
		return true;
	}
	
	//Draw spline_for_path_following_test
	void draw(PApplet p) {
		for (int i= 1; i<sp_path.pointList.size (); i++) {
			Vec3D a = sp_path.pointList.get(i);
			Vec3D b = sp_path.pointList.get(i-1);
			p.stroke(0, 90);//farba linky
			p.line(a.x, a.y, a.z, b.x, b.y, b.z); //tu je definovane, ze sa bude kreslit linka na zaklade spojenia serie bodov
		}
	}
}