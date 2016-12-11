import java.util.ArrayList;

import processing.core.PApplet;
import toxi.geom.Spline3D;
import toxi.geom.Vec3D;

class Roads implements LineReader{
	String[] ptListRd, listLengthRd;
	Vec3D[] ptsRd;
	ArrayList<Spline3D> sprd = new ArrayList<Spline3D>();// cesty

	void readSplines() {
		ptListRd = lineArray("data/cesty_body.txt");
		listLengthRd = lineArray("data/cesty_zoznam.txt");
		int totRd=0;
		for (int i = 0; i<listLengthRd.length; i++) totRd += Integer.parseInt(listLengthRd[i]); 
		ptsRd = new Vec3D[totRd];
		for (int i=0; i<ptsRd.length; i++) ptsRd[i]=new Vec3D(Float.parseFloat(ptListRd[2*i]), - Float.parseFloat(ptListRd[2*i+1]), 0);

		// CESTY SPLINES //////////////////////////
		int itRd=0;
		int totIndexRd=0;
		for (int n=0; n<listLengthRd.length; n++) {
			Spline3D sprd0=new Spline3D();
			totIndexRd += Integer.parseInt(listLengthRd[n]);
	    for (int i=itRd; i<totIndexRd; i++) {
	    	Vec3D v=new Vec3D(ptsRd[i].copy());
	    	sprd0.add(v);
	    }
	    itRd +=  Integer.parseInt(listLengthRd[n]);
	    sprd.add(sprd0);
	  }
	}

	public void draw(EmCity p) {
		// DRAW ROADS /////////////////////////////////////////////////
		p.stroke(GUI.txtCol, 150);
		p.strokeWeight(1);
		int itRd=0;
		int totIndexRd=0;  
		for (int iter=0; iter<listLengthRd.length; iter++) {
		    totIndexRd += Integer.parseInt(listLengthRd[iter]);
		    for (int i = itRd + 1; i<totIndexRd; i++) {
		    	if (p.DRAW_ROADS) {
		    		p.line(ptsRd[i-1].x, ptsRd[i-1].y, ptsRd[i].x, ptsRd[i].y);
		    	}
		    }
		    itRd += Integer.parseInt(listLengthRd[iter]);
		}
	}

	public void drawPoints(PApplet p) {

		// draw initialization points for agents
		p.ellipseMode(PApplet.CENTER);
		p.fill(255, 132, 0, 90);// Set fill to orange
		p.stroke(255, 100, 0); // Set stroke to deep orange
		p.strokeWeight(3);
		// ellipse(mouseX-width/2, mouseY-height/2, 15, 15);

		/*
		 * ellipse(-407, 37, 15, 15); ellipse(125, 225, 15, 15); ellipse(158,
		 * -282, 15, 15); ellipse(576, 433, 15, 15); ellipse(288, 596, 15, 15);
		 * ellipse(752, 606, 15, 15); ellipse(879, -289, 15, 15); ellipse(-304,
		 * 474, 15, 15);
		 */
		for (Vec3D v: Agent.initLocations){
			p.ellipse(v.x, v.y, 15, 15);
		}
	}
}