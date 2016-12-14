import processing.core.PApplet;
import processing.core.PImage;
import toxi.geom.Vec3D;

//CLASS
public class Field {
	// FIELD

	int nx, ny;
	float[] values;

	int X_offset = 1200;
	int Y_offset = 1200;

	private PImage img;
	private PApplet P;

	// CONSTRUCTOR
	Field(PImage _img, PApplet p5) {
		img = _img;
		P = p5;
		nx = img.width;
		ny = img.height;
		values = new float[nx * ny];
	}
	
	public PImage getImage(){
		return img;
	}

	// METHODS
	int id(int x, int y) {
		return (x + X_offset) + (y + Y_offset) * nx;
	}

	void addValue(Vec3D v, float inc) {
		int id = id(PApplet.floor(v.x), PApplet.floor(v.y));
		if (id < values.length) { // TODO catch out of the field
			values[id] += inc;
			// values[id] = constrain(values[id], 0, 5);

			// img.pixels[id] = color(255-values[id]*50, values[id]*255, 255);
			img.pixels[id] = P.color(255, 255, 255, values[id]);
		}
	}

	float readValue(Vec3D v) {
		float val = 0;
		int id = id(PApplet.floor(v.x), PApplet.floor(v.y));
		if (id < 0){
			System.out.println("bug");
			id(PApplet.floor(v.x), PApplet.floor(v.y));
		}
		if (id < values.length)
			val = values[id];
		return val;
	}

	void decay(float mult) {
		for (int i = 0; i < nx * ny; i++) {
			if (values[i] > 0) {
				values[i] *= mult;
				img.pixels[i] = P.color(255, 255, 255, values[i]);
			}
		}
	}

	void reset() {
		for (int i = 0; i < nx * ny; i++) {
			values[i] = 0;
		}
	}

	void drawField() {
//		drawPheromones();
		P.image(img, -X_offset, -Y_offset);
	}
	
	void drawPheromones(){
		img.loadPixels();
		for (int i = 0; i < img.pixels.length; i++) {
			img.pixels[i] = P.color(255-values[i]*50, values[i]*255, 255);
		}
		img.updatePixels();
	}
}
