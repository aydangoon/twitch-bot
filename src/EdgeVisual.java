
public class EdgeVisual<T> {

	private final NodeVisual<T> src;
	private final NodeVisual<T> tgt;
	private final double weight;
	
	public EdgeVisual(NodeVisual<T> src, NodeVisual<T> tgt, double weight) {
		this.src = src;
		this.tgt = tgt;
		this.weight = weight;
	}
	
	public NodeVisual<T> getSrc() {
		return src;
	}
	
	public NodeVisual<T> getTgt() {
		return tgt;
	}
	
	public double getWeight() {
		return weight;
	}

}
