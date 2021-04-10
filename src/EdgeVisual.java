
public class EdgeVisual<T> {

	private final NodeVisual<T> src;
	private final NodeVisual<T> tgt;
	
	public EdgeVisual(NodeVisual<T> src, NodeVisual<T> tgt) {
		this.src = src;
		this.tgt = tgt;
	}

}
