
public class NodeVisual<T> {
	
	private T value;
	private int x;
	private int y;
	
	public NodeVisual(T value, int x, int y) {
		this.value = value;
		this.x = x;
		this.y = y;
	}
	
	public T getValue() {
		return value;
	}

	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
	
		if (o == this) return true;
		
		if (!(o instanceof NodeVisual)) return false;
		
		return ((NodeVisual<T>) o).getValue().equals(this.value);
		
	}
	
}
