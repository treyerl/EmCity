import processing.core.PApplet;

class Cell implements Agent.Type, Colonizeable{
	
	static Cell create(int type, int x, int y, int capacity, int size, Cluster c){
		switch(type){
		case Agent.PRIVATE: return new Private(x,y,capacity,size, c);
		case Agent.CULTURE: return new Culture(x,y,capacity,size, c);
		case Agent.SQUARE: return new Square(x,y,capacity,size, c);
		default: return new Cell(x,y,capacity, size, c);
		}
	}
	
	static class Culture extends Cell implements Agent.Culture{
		Culture(int x, int y, int capacity, int size, Cluster c) {
			super(x, y, capacity, size, c);
			activity = 0xffD2E87E;
		}
	}
	static class Square extends Cell implements Agent.Square {
		Square(int x, int y, int capacity, int size, Cluster c) {
			super(x, y, capacity, size, c);
			activity = 0xffA2B93A;
		}
	}
	static class Private extends Cell implements Agent.Private{
		Private(int x, int y, int capacity, int size, Cluster c) {
			super(x, y, capacity, size, c);
			activity = 215;
		}
	}

	int x;
	int y;
	int size;
	int capacity;
	int occupation;
	int activity = 0;
	Cluster cluster;

	Cell(int x, int y, int capacity, int size, Cluster c) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.capacity = capacity;
		this.occupation = 0; // (int)random(capacity);
		this.cluster = c;
	}
	
	void draw(PApplet p) {
		if (capacity > 0) {
			p.pushMatrix();
			p.translate(x, y, capacity / 2);
			p.noFill();
			p.strokeWeight(0.5f);
			p.stroke(p.color(0));
			p.box(size, size, capacity);
			p.popMatrix();
			if (occupation > 0) {
				p.pushMatrix();
				p.translate(x, y, occupation / 2);
				p.noStroke();
				p.fill(activity);
				p.box(size, size, occupation);
				p.popMatrix();
			}
		}
	}

	// @return: remaining agents
	public int colonize(int agent){
		if (isFull())
			return agent; // return all initial agents as remaining agents

		int rest = occupation + agent - capacity;
		// if agents remain
		if (rest >= 0)
			this.occupation = capacity; // full capacity reached
		else
			this.occupation += agent;

		return rest;
	}

	public boolean isFull() {
		return occupation >= capacity;

	}

	public void empty() {
		this.occupation = 0;
	}

	public void fill() {
		this.occupation = capacity;
	}
}