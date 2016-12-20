package emcity;
import java.util.ArrayList;

import controlP5.Chart;
import controlP5.ControlGroup;
import controlP5.ControlP5;
import controlP5.Slider;
import controlP5.Toggle;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.opengl.PGraphics3D;
import toxi.geom.Vec3D;

public class GUI {
	
	public final static int txtCol = 65;
	
	private ControlP5 controlP5;
	private EmCity P;
	private Chart myChart;
	private Chart myChart2;
	private Chart myChart3;
	private Chart Growth;
	
	private PMatrix3D currCameraMatrix;
	private PGraphics3D p3d;
	private PeasyCam campeasy;
	
	public GUI(EmCity p5){
		P = p5;
		p3d = (PGraphics3D) P.g;
		campeasy = new PeasyCam(P, 3200);
	}

	public void initGUI() {
		
		campeasy.lookAt(0, 0, 0);
		campeasy.setResetOnDoubleClick(false);
		campeasy.setMinimumDistance(100);
		campeasy.setMaximumDistance(3000);
		
		controlP5 = new ControlP5(P);
		//controlP5.setColorLabel(color(250, 250, 250));  //Text Color
		ControlGroup<?> ctrl = controlP5.addGroup("menu", 10, 20, 55);  
		ctrl.setColorLabel(P.color(255));
		ctrl.close(); 

		//Chart initialization
		//controlP5.printPublicMethodsFor(Chart.class);

		myChart = controlP5.addChart("Area Ratio")
				.setPosition(P.width-110, P.height-650)
				.setSize(30, 100)
				.setRange(0, 0.5f)
				.setView(Chart.BAR);
		myChart.getColor().setBackground(P.color(0xffA0DB7E, 50));
		myChart.addDataSet("Area Ratio");
		myChart.setColors("Area Ratio", P.color(255, 85), P.color(255));
		myChart.setData("Area Ratio", new float[1]);
		myChart.setStrokeWeight(1);


		myChart2 = controlP5.addChart("Area Ratio C")
				.setPosition(P.width-80, P.height-650)
				.setSize(30, 100)
				.setRange(0, 0.4f)
				.setView(Chart.BAR);
		myChart2.getColor().setBackground(P.color(0xffA0DB7E, 50));
		myChart2.addDataSet("Area Ratio C");
		myChart2.setColors("Area Ratio C", P.color(0xffD2E87E), P.color(0xffD2E87E));
		myChart2.setData("Area Ratio C", new float[1]);
		myChart2.setStrokeWeight(1);

		myChart3 = controlP5.addChart("Area Ratio SQ")
				.setPosition(P.width-50, P.height-650)
				.setSize(30, 100)
				.setRange(0, 0.4f)
				.setView(Chart.BAR);
		myChart3.getColor().setBackground(P.color(0xffA0DB7E, 50));
		myChart3.addDataSet("Area Ratio SQ");
		myChart3.setColors("Area Ratio SQ", P.color(0xffA2B93A), P.color(0xffA2B93A));
		myChart3.setData("Area Ratio SQ", new float[1]);
		myChart3.setStrokeWeight(1);

		Growth = controlP5.addChart("Growth_level")
				.setPosition(P.width-110, P.height-520)
				.setSize(90, 100)
				.setRange(0.17f, 0.6f)
				.setView(Chart.BAR);
		Growth.getColor().setBackground(P.color(0xffA0DB7E, 50));
		Growth.addDataSet("Growth_level");
		Growth.setColors("Growth_level", P.color(255,80));
		Growth.setData("Growth_level", new float[1]);
		Growth.setStrokeWeight(1);


		ArrayList <Slider> sls = new ArrayList<Slider>(); 
		ArrayList <Toggle> tog = new ArrayList<Toggle>(); 

		//buttons
		tog.add(controlP5.addToggle(P.s5, "", "swarmingOnOff", P.s5.swarmingOnOff)
				.setPosition(10, 250).setSize(15, 15).setLabel("Swarming on/off"));
		tog.add(controlP5.addToggle(P.s5, "", "showTrails", P.s5.showTrails)
				.setPosition(10, 400).setSize(15, 15).setLabel("Show Trails"));
		tog.add(controlP5.addToggle(P.s5, "", "stigmergy", P.s5.stigmergy)
				.setPosition(10, 10).setSize(15, 15).setLabel("Stigmergy"));
		tog.add(controlP5.addToggle(P.s5, "", "showDir", P.s5.showDir)
				.setPosition(80, 400).setSize(15, 15).setLabel("Direction"));
		tog.add(controlP5.addToggle(P.s5, "", "attract", P.s5.attract)
				.setPosition(10, 190).setSize(15, 15).setLabel("Attract"));
		tog.add(controlP5.addToggle(P.s5, "", "generate", P.s5.generate)
				.setPosition(10, 350).setSize(15, 15).setLabel("Generate Volumes!"));
		tog.add(controlP5.addToggle(P.s5, "", "show_att_distance", P.s5.show_att_distance)
				.setPosition(190, 100).setSize(15, 15).setLabel("Show"));
		tog.add(controlP5.addToggle(P.s5, "", "objexport", P.s5.objexport)
				.setPosition(80, 440).setSize(15, 15).setLabel("export OBJ"));
		tog.add(controlP5.addToggle(P.s5, "", "record", P.s5.record)
				.setPosition(10, 440).setSize(15, 15).setLabel("export PDF"));


		/////////////////////////////////////////////DYNAMICS CONTROLS/////////////////////////////////////////////////////  

		//sliders
		sls.add(controlP5.addSlider(P.s5, "", "factor")
				.setValue(P.s5.factor)
				.setMin(-4)
				.setMax(4)
				.setPosition(10,220)
				.setSize(100,10)
				.setLabel("Path following factor"));
		
		sls.add(controlP5.addSlider(P.s5, "", "approach")
				.setValue(P.s5.approach)
				.setMax(1000).setMin(100)
				.setPosition(10,380)
				.setSize(100,10)
				.setLabel("accessible distance"));
		
		sls.add(controlP5.addSlider(P.s5, "", "scatter")
				.setValue(P.s5.scatter)
				.setMin(1)
				.setMax(15)
				.setPosition(10,40)
				.setSize(100,10)
				.setLabel("scatter"));
		
		sls.add(controlP5.addSlider(P.s5, "", "stigmergyStrength")
				.setValue(P.s5.stigmergyStrength)
				.setMin(0)
				.setMax(2)
				.setPosition(10,160)
				.setSize(100,10)
				.setLabel("stigmergy strength"));
		
		sls.add(controlP5.addSlider(P.s5, "", "decay")
				.setValue(P.s5.decay)
				.setMin(0.75f)
				.setMax(2)
				.setPosition(10,60)
				.setSize(100,10)
				.setLabel("decay"));
		
		sls.add(controlP5.addSlider(P.s5, "", "nSamples")
				.setValue(P.s5.nSamples)
				.setMin(5)
				.setMax(255)
				.setPosition(10,80)
				.setSize(100,10)
				.setLabel("nSamples"));
		
		sls.add(controlP5.addSlider(P.s5, "", "att_distance")
				.setValue(P.s5.att_distance)
				.setMax(500)
				.setMin(100)
				.setPosition(10,100)
				.setSize(100,10)
				.setLabel("att_distance"));
		
		sls.add(controlP5.addSlider(P.s5, "", "att_angle")
				.setValue(P.s5.att_angle)
				.setMin(1.4f)
				.setMax(3.12f)
				.setPosition(10,120)
				.setSize(100,10)
				.setLabel("att_angle"));
		
		sls.add(controlP5.addSlider(P.s5, "", "att_factor")
				.setValue(P.s5.att_factor)
				.setMin(0.1f)
				.setMax(1)
				.setPosition(10,140)
				.setSize(100,10)
				.setLabel("att_factor to buildings"));
		
		sls.add(controlP5.addSlider(P.s5, "", "cohesion")
				.setValue(P.s5.cohesion)
				.setMin(0)
				.setMax(3)
				.setPosition(10,280)
				.setSize(100,10)
				.setLabel("cohesion"));
		
		sls.add(controlP5.addSlider(P.s5, "", "alignment")
				.setValue(P.s5.alignment)
				.setMin(0)
				.setMax(3)
				.setPosition(10,300)
				.setSize(100,10)
				.setLabel("alignment"));
		
		sls.add(controlP5.addSlider(P.s5, "", "separation")
				.setValue(P.s5.separation)
				.setMin(0)
				.setMax(3)
				.setPosition(10,320)
				.setSize(100,10)
				.setLabel("SEPARATION"));

	
	    //user defined vectros - initial positions for x and y -1645, -1508
		Vec3D[] p = Agent.initLocations;
		sls.add(controlP5.addSlider(P.s5, "", "pop1x", -1645, 1645, 250, 10, 100, 10 )
				.setValue(p[0].x).setLabel("px 1"));
		sls.add(controlP5.addSlider(P.s5, "", "pop2x", -1645, 1645, 250, 30, 100, 10)
				.setValue(p[1].x).setLabel("px 2"));
		sls.add(controlP5.addSlider(P.s5, "", "pop3x", -1645, 1645, 250, 50, 100, 10 )
				.setValue(p[2].x).setLabel("px 3"));
		sls.add(controlP5.addSlider(P.s5, "", "pop4x", -1645, 1645, 250, 70, 100, 10 )
				.setValue(p[3].x).setLabel("px 4"));
		sls.add(controlP5.addSlider(P.s5, "", "pop5x", -1645, 1645, 250, 90, 100, 10)
				.setValue(p[4].x).setLabel("px 5"));
		sls.add(controlP5.addSlider(P.s5, "", "pop6x", -1645, 1645, 250, 110, 100, 10 )
				.setValue(p[5].x).setLabel("px 6"));
		sls.add(controlP5.addSlider(P.s5, "", "pop7x", -1645, 1645, 250, 130, 100, 10)
				.setValue(p[6].x).setLabel("px 7"));
		sls.add(controlP5.addSlider(P.s5, "", "pop8x", -1645, 1645, 250, 150, 100, 10 )
				.setValue(p[7].x).setLabel("px 8"));
		
		sls.add(controlP5.addSlider(P.s5, "", "pop1y", -1508, 1508, 400, 10, 100, 10 )
				.setValue(p[0].y).setLabel("py 1"));
		sls.add(controlP5.addSlider(P.s5, "", "pop2y", -1508, 1508, 400, 30, 100, 10)
				.setValue(p[1].y).setLabel("py 2"));
		sls.add(controlP5.addSlider(P.s5, "", "pop3y", -1508, 1508, 400, 50, 100, 10)
				.setValue(p[2].y).setLabel("py 3"));
		sls.add(controlP5.addSlider(P.s5, "", "pop4y", -1508, 1508, 400, 70, 100, 10)
				.setValue(p[3].y).setLabel("py 4"));
		sls.add(controlP5.addSlider(P.s5, "", "pop5y", -1508, 1508, 400, 90, 100, 10)
				.setValue(p[4].y).setLabel("py 5"));
		sls.add(controlP5.addSlider(P.s5, "", "pop6y", -1508, 1508, 400, 110, 100, 10)
				.setValue(p[5].y).setLabel("py 6"));
		sls.add(controlP5.addSlider(P.s5, "", "pop7y", -1508, 1508, 400, 130, 100, 10)
				.setValue(p[6].y).setLabel("py 7"));
		sls.add(controlP5.addSlider(P.s5, "", "pop8y", -1508, 1508, 400, 150, 100, 10)
				.setValue(p[7].y).setLabel("py 8"));
		    
		    
	    //levels of importance - weights level for each position
	  
		sls.add(controlP5.addSlider(P.s5, "", "pop1amount", 10, 40, 550, 10, 50, 10 )
				.setValue(Agent.populationSizes[0]).setLabel("weights level p1"));
		sls.add(controlP5.addSlider(P.s5, "", "pop2amount", 20, 60, 550, 30, 50, 10 )
				.setValue(Agent.populationSizes[1]).setLabel("weights level p2"));
		sls.add(controlP5.addSlider(P.s5, "", "pop3amount", 20, 70, 550, 50, 50, 10 )
				.setValue(Agent.populationSizes[2]).setLabel("weights level p3"));
		sls.add(controlP5.addSlider(P.s5, "", "pop4amount", 30, 80, 550, 70, 50, 10 )
				.setValue(Agent.populationSizes[3]).setLabel("weights level p4"));
		sls.add(controlP5.addSlider(P.s5, "", "pop5amount", 30, 90, 550, 90, 50, 10 )
				.setValue(Agent.populationSizes[4]).setLabel("weights level p5"));
		sls.add(controlP5.addSlider(P.s5, "", "pop6amount", 40, 100, 550, 110, 50, 10)
				.setValue(Agent.populationSizes[5]).setLabel("weights level p6"));
		sls.add(controlP5.addSlider(P.s5, "", "pop7amount", 40, 100, 550, 130, 50, 10 )
				.setValue(Agent.populationSizes[6]).setLabel("weights level p7"));
		sls.add(controlP5.addSlider(P.s5, "", "pop8amount", 50, 100, 550, 150, 50, 10 )
				.setValue(Agent.populationSizes[7]).setLabel("weights level p8"));
	
		//menu group
	
		for ( int i=0; i< sls.size (); i++) {
			Slider s = sls.get(i);
			s.setGroup(ctrl);
			//s.setId(i);
		}
	
		for ( int i=0; i< tog.size (); i++) {
			Toggle t = tog.get(i);
			t.setGroup(ctrl);
			//t.setId(i);
		}
	}

	// draw gui
	public void gui() {
		campeasy.beginHUD();
		currCameraMatrix = new PMatrix3D(p3d.camera);

		// camera();
		controlP5.draw(); // draw GUI!

		float d = (float) campeasy.getDistance();
		float[] pos = campeasy.getLookAt();
		String camPos = "Camera position: "
				+ "x." + PApplet.round(pos[0]) + " | "
				+ "y." + PApplet.round(pos[1]) + " | "
				+ "z." + PApplet.round(pos[2]);

		P.textSize(11);
		P.fill(230);
		area();
		P.textAlign(PApplet.RIGHT);
		P.text("Frame count | " + P.frameCount + " | @ Frame Rate | " + PApplet.round(P.frameRate) + "|", P.width - 20, P.height - 20);
		P.text("Camera current distance: " + PApplet.round(d), P.width - 20, P.height - 40);
		P.text(camPos, P.width - 20, P.height - 60);
		P.text("Camera Mouse controls:", P.width - 20, P.height - 80);
		P.text("Left - orbit | middle - drag | right/wheel - zoom", P.width - 20, P.height - 100);
		P.text("Display statistics:", P.width - 20, P.height - 120);

		P.textAlign(PApplet.LEFT);
		P.text("Press keys:", 10, P.height - 230);
		P.text("Save Video sequence [V]", 10, P.height - 200);
		P.text("Private activities [1] ", 10, P.height - 180);
		P.text("Public activities  [2] ", 10, P.height - 160);
		P.text("Squares | parks  [3] ", 10, P.height - 140);
		P.text("Buildings 2D  [B] ", 10, P.height - 120);
		P.text("Roads  [W] ", 10, P.height - 100);
		P.text("Add agents!  [A] ", 10, P.height - 80);
		P.text("Reload Typographies![T]", 10, P.height - 60);
		// text("Add Volume by hand [T][C][Q] ", 10, 670);
		P.text("Save Frame JPG [F] ", 10, P.height - 40);
		P.text("Reset simulation [R] ", 10, P.height - 20);

		p3d.camera = currCameraMatrix;

		campeasy.endHUD();
		controlP5.setAutoDraw(false);
	}

	void area() {

		// calculate built area of private activities
		int sum_area = (int) (P.getCells().values().stream().filter(c -> c.is(Agent.PRIVATE)).count() * 100);

		// calculate built area of public activities-culture
		int sum_area_c = (int) (P.getCells().values().stream().filter(c -> c.is(Agent.CULTURE)).count() * 100);

		// calculate built area of squares/parks
		int sum_area_sq = (int) (P.getCells().values().stream().filter(c -> c.is(Agent.SQUARE)).count() * 100);
		float summ = sum_area + sum_area_c + sum_area_sq;
		float whole = 2893599;

		// calculate area ratio
		float area_ratio1 = sum_area / whole;
		float area_ratio2 = sum_area_c / whole;
		float area_ratio3 = sum_area_sq / whole;
		float area_ratio = summ / whole; // sqm of whole lay-out

		String ratio = PApplet.nf(area_ratio, 0, 3);

		P.textSize(11);
		P.fill(230);
		P.textAlign(PApplet.RIGHT, PApplet.TOP);

		P.text("Built private area / sqm : " + sum_area, P.width - 20, P.height - 750);
		P.text("Built public area / sqm : " + sum_area_c, P.width - 20, P.height - 730);
		P.text("Built public parks and squares area / sqm : " + sum_area_sq, P.width - 20, P.height - 710);
		P.text("Total built area / sqm : " + summ, P.width - 20, P.height - 690);
		P.text("Area ratio : " + ratio, P.width - 20, P.height - 670);

		myChart.unshift("Area Ratio", area_ratio1);
		myChart2.unshift("Area Ratio C", area_ratio2);
		myChart3.unshift("Area Ratio SQ", area_ratio3);
		Growth.unshift("Growth_level", area_ratio);

		String ratio1 = PApplet.nf(area_ratio1, 0, 2);
		String ratio2 = PApplet.nf(area_ratio2, 0, 2);
		String ratio3 = PApplet.nf(area_ratio3, 0, 2);

		P.textSize(9.5f);
		P.text(ratio1, P.width - 90, P.height - 550);
		P.text(ratio2, P.width - 60, P.height - 550);
		P.text(ratio3, P.width - 25, P.height - 550);
		P.text(ratio, P.width - 84, P.height - 420);
		P.text("Urban Graininess", P.width - 20, P.height - 540);
		P.text("Density", P.width - 48, P.height - 408);
		P.textSize(11);

	}

	// ________________________________ ControlP5 in GUI FUNCTIONS

	void checkOverlap() { // useful when interface is overlayed on main window -
							// not used with external panel

		// IMPORTANT!!!!///////////////////////////////////////////////
		// avoid rotation by mouse drag when using sliders of ControlP5
		//
		if (controlP5.isMouseOver()) { // if mouse is over controllers
			campeasy.setActive(false); // disable camera mouse controls
		} else { // otherwise....
			campeasy.setActive(true); // ...way to go!
		}
	}

}