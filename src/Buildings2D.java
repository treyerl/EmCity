import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

class Buildings2D implements LineReader{
	String[] ptListB, listLengthB;
	ArrayList<Spline3D> spb = new ArrayList<Spline3D>();
	ArrayList<Spline3D> spb0 = new ArrayList<Spline3D>();
	Vec3D[] ptsB;
	int blCol = 100;

	public Buildings2D() {

		ptListB = lineArray("data/budovy_body.txt");
		listLengthB = lineArray("data/budovy_zoznam.txt");
		int totB=0;
		for (int i=0; i<listLengthB.length; i++) {
			totB += Integer.parseInt(listLengthB[i]);
		}
		ptsB=new Vec3D[totB];
		for (int i=0; i<ptsB.length; i++) {
			ptsB[i] = new Vec3D(new Float(ptListB[2*i]), -new Float(ptListB[2*i+1]), 0);
		}
		
		// BUDOVY-SPLINES //////////////////////
		int itB=0;
		int totIndexB=0;
		for (int iter=0; iter<listLengthB.length; iter++) {
		    Spline3D spb0=new Spline3D();
		    totIndexB += new Integer(listLengthB[iter]);
		    for (int i=itB; i<totIndexB-1; i++) {
		    	Vec3D v=new Vec3D(ptsB[i].copy());
		    	spb0.add(v);
		    	if (i==totIndexB-1) {
		    		Vec3D V=new Vec3D(ptsB[i].copy());
		    		spb0.add(V);
		    		spb0.add(spb0.getPointList().get(0));
		    	}
		    }
		    itB += new Integer(listLengthB[iter]);
		    spb.add(spb0);
		}
	}

	void draw(PApplet p, boolean buildings) {
	
		// DRAW BUILDINGS /////////////////////////////////////////////
		
		
		// pushMatrix();
		// translate(-625, 15, 0);
		p.stroke(GUI.txtCol); //added for black bg
		p.strokeWeight(1);
		int itB=0;
		int totIndexB=0;
		for (int iter=0; iter<listLengthB.length; iter++) {
		    totIndexB += Integer.parseInt(listLengthB[iter]);
		    for (int i=itB+1; i<totIndexB; i++){
		    	if (buildings) {
		    		p.line(ptsB[i-1].x, ptsB[i-1].y, ptsB[i].x, ptsB[i].y);
		    	}
		    }
		    itB += Integer.parseInt(listLengthB[iter]);
		}
		p.stroke(p.color(blCol), 100);
		p.fill(p.color(blCol), 100);
		//  popMatrix();
	}
}