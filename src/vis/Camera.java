package vis;

// Camera state for a Markov Chain visualization.
public class Camera {

	private int x;
	private int y;
	private double zoom;
	private double velX;
	private double velY;
	
	public Camera() {
		this.x = 0;
		this.y = 0;
		this.zoom = 1;
		this.velX = 0;
		this.velY = 0;
	}
	
	public void update(int hor, int ver, int zoom) {
		
		
		this.x += velX;
		this.y += velY;
		
		switch (hor) {
			case 1:
				velX = 15 / this.zoom;
				break;
			case 0:
				velX = 0;
				break;
			case -1:
				velX = -15 / this.zoom;
				break;
		}
		
		switch (ver) {
			case 1:
				velY = 15 / this.zoom;
				break;
			case 0:
				velY = 0;
				break;
			case -1:
				velY = -15 / this.zoom;
		}
		
		if (zoom == 1 && this.zoom < 2) {
			this.zoom += 0.02;
		} else if (zoom == -1 && this.zoom > 0.1) {
			this.zoom -= 0.02;
		}
		
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
	
	public double getZoom() {
		return this.zoom;
	}
	
}
