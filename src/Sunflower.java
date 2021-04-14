public class Sunflower {
	
	private double points;
	private double iteration;
	private final double GOLDEN_RATIO = 1.618033;
	
	public Sunflower(int points) {
		this.points = points;
		this.iteration = 0;
	}
	
	public Tuple<Integer, Integer> nextPoint() {
		double dst = (this.iteration++) / (this.points - 1);
		double ang = 2 * Math.PI * GOLDEN_RATIO * this.iteration;
		
		double x = Constants.WIDTH * Constants.GRAPH_SCALE_FACTOR * dst * Math.cos(ang);
		double y = Constants.HEIGHT * Constants.GRAPH_SCALE_FACTOR * dst * Math.sin(ang);
		
		return new Tuple<Integer, Integer>((int)x, (int)y);
	}
}
