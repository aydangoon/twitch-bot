
public class NodeVisual<T> {
	
	private T value;
	
	public NodeVisual(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
	
		if (o == this) return true;
		
		if (!(o instanceof NodeVisual)) return false;
		
		return ((NodeVisual<T>) o).getValue().equals(this.value);
		
	}
	
}
