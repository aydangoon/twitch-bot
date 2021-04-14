import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import java.util.Iterator;

public class TwitchBotMain {

	public static void main(String[] args) {
			
		String[] words = "frog dog log cog frog".split(" ");
		
		Digraph<String> g = new Digraph<String>();
		
		Map<String, Integer> proceeds = new HashMap<String, Integer>();
		
		// add word nodes
		for (int i = 0; i < words.length; i++)
			if (!g.hasNode(words[i])) 
				g.addNode(words[i]);
		
		// calculate total occurances
		for (int i = 0; i < words.length - 1; i++) {
			
			String src = words[i];
			String tgt = words[i + 1];
			
			if (!g.hasEdge(src, tgt))
				g.addEdge(src, tgt, 1);
			else
				g.setWeight(src, tgt, g.getWeight(src, tgt) + 1);
			
			if (!proceeds.containsKey(src))
				proceeds.put(src, 1);
			else
				proceeds.put(src, proceeds.get(src) + 1);	
		}
		
		// normalize
		Iterator<String> iter = proceeds.keySet().iterator();
		while (iter.hasNext()) {
			String src = iter.next();
			for (String tgt : g.childrenOf(src)) {
				g.setWeight(src, tgt, g.getWeight(src, tgt) / proceeds.get(src));
			}
		}
		
//			System.out.println(g);
		
//				String word = "pepeLaugh";
//				int c = 0;
//				StringBuilder sentence = new StringBuilder();
//				List<String> nextWords;
//				do {
//					nextWords = g.childrenOf(word);
//					if (nextWords.size() > 0) {
//						double random = Math.random();
//						int i = 0;
//						while (random > g.getWeight(word, nextWords.get(i))) {
//							random -= g.getWeight(word, nextWords.get(i));
//							i++;
//						}
//						sentence.append(word + " ");
//						word = nextWords.get(i);
//						if (c++ == 10) {
//							c = 0;
//							sentence.append("\n");
//						}
//					}
//				} while (nextWords.size() > 0);
//				
//				System.out.println("Naive sentence built with model:");
//				System.out.println(sentence);
		
		MarkovChainVisualization<String> mcv = new MarkovChainVisualization<String>(g);
		
		JFrame window = new JFrame();
		window.setTitle("TwitchBot Markov Chain Visualization");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		window.setResizable(false);
		window.add(mcv);
		window.setVisible(true);
	}

}
