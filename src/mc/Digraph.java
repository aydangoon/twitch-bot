package mc;
import java.util.*;
import utils.Tuple;

// A directed graph of where nodes are of type T.
public class Digraph<T> {

	// the adajacency list map of value to (value, weight) pairs
	private Map<T, List<Tuple<T, Double>>> li;	
	
	public Digraph() {
		li = new HashMap<T, List<Tuple<T, Double>>>();
	}
	
	public int nodeCount() {
		return li.size();
	}
	
	public boolean hasNode(T value) {
		return li.containsKey(value);
	}
	
	public void addNode(T value) {
		li.put(value, new LinkedList<Tuple<T, Double>>());
	}
	
	public boolean hasEdge(T src, T tgt) {
		for (Tuple<T, Double> tup : li.get(src))
			if (tup._1.equals(tgt))
				return true;
		return false;
	}
	
	public void addEdge(T src, T tgt, double weight) {
		li.get(src).add(new Tuple<T, Double>(tgt, weight));
	}
	
	public void setWeight(T src, T tgt, double weight) {
		for (Tuple<T, Double> tup : li.get(src)) {
			if (tup._1.equals(tgt)) {
				tup._2 = weight;
				return;
			}
		}
	}
	
	public double getWeight(T src, T tgt) {
		for (Tuple<T, Double> tup : li.get(src)) 
			if (tup._1.equals(tgt)) 
				return tup._2;
		return -1;
	}
	
	public List<T> childrenOf(T src) {
		List<T> children = new LinkedList<T>();
		for (Tuple<T, Double> tup : li.get(src))
			children.add(tup._1);
		return children;
	}
	
	public List<T> nodes() {
		List<T> out = new LinkedList<T>();
		Iterator<T> iter = this.li.keySet().iterator();
		while (iter.hasNext())
			out.add(iter.next());
		return out;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		Set<T> keyset = li.keySet();
		
		out.append("Nodes:" + keyset + "\nEdges:\n");
		for (T key : keyset)
			out.append(key + ": " + li.get(key) + "\n");
		
		return out.toString();
	}
}
