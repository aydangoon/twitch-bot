package utils;

public class UniformSquare {

	private int sideLength;
	private int i;
	private int j;
	
	public UniformSquare(int sideLength) {
		this.sideLength = sideLength;
		this.i = -sideLength / 2;
		this.j = this.i;
	}
	
	public Tuple<Integer, Integer> nextPoint() {
		
		Tuple<Integer, Integer> pos = new Tuple<Integer, Integer>(i * Constants.SPACE_BETWEEN_NODES, 12*(i+sideLength/2) + j * Constants.SPACE_BETWEEN_NODES);
		i++;
		if (i == sideLength / 2) {
			j++;
			i = -sideLength / 2;
		}
		return pos;
	}
	
}
