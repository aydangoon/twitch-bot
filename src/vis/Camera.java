package vis;

// Camera state for a Markov Chain visualization.
public class Camera {

	private int x;
	private int y;
	private int zoom;
	private double velX;
	private double velY;
	
	public Camera() {
		this.x = 0;
		this.y = 0;
		this.zoom = 0;
		this.velX = 0;
		this.velY = 0;
	}
	
	public void update(int hor, int ver, int zoom) {
		
		
		this.x += velX;
		this.y += velY;
		
		switch (hor) {
			case 1:
				velX = 15;
				break;
			case 0:
				velX = 0;
				break;
			case -1:
				velX = -15;
				break;
		}
		
		switch (ver) {
		case 1:
			velY = 15;
			break;
		case 0:
			velY = 0;
			break;
		case -1:
			velY = -15;
		}
		
		this.zoom += zoom;
		
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}
	
	public int getZoom() {
		return this.zoom;
	}
	
}
