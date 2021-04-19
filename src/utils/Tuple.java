package utils;

// Basic tuple object.
public class Tuple<V1, V2> {

	public V1 _1;
	public V2 _2;
	
	public Tuple(V1 _1, V2 _2) {
		this._1 = _1;
		this._2 = _2;
	}
	
	public String toString() {
		return "(" + this._1 + ", " + this._2 + ")";
	}
}
