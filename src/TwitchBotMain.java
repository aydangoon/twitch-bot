import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.swing.JFrame;
import mc.Digraph;
import mc.MarkovChainBuilder;
import mc.Message;
import utils.Constants;
import vis.MarkovChainVisualization;

public class TwitchBotMain {

	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		window.setTitle("TwitchBot");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		
		MarkovChainBuilder mcb = new MarkovChainBuilder();

		window.add(mcb);
		window.setSize(mcb.getSize());
		window.setVisible(true);
		mcb.parseMessagesFromCSV(Constants.FILENAME);
		mcb.buildMarkovChain();
		window.remove(mcb);
		MarkovChainVisualization<String> mcv = new MarkovChainVisualization<String>(mcb.getMarkovChain());
		window.add(mcv);
		window.setSize(mcv.getPreferredSize());
		
		
//			String word = words.get((int)(words.size() * Math.random()));
//			int c = 0;
//			int iters = 0;
//			StringBuilder sentence = new StringBuilder();
//			List<String> nextWords;
//			do {
//				nextWords = g.childrenOf(word);
//				if (nextWords.size() > 0) {
//					double random = Math.random();
//					int i = 0;
//					while (random > g.getWeight(word, nextWords.get(i))) {
//						random -= g.getWeight(word, nextWords.get(i));
//						i++;
//					}
//					sentence.append(word + " ");
//					word = nextWords.get(i);
//					if (c++ == 10) {
//						c = 0;
//						sentence.append("\n");
//					}
//				}
//			} while (nextWords.size() > 0 && iters++ < 10);
			
//			System.out.println("Naive sentence built with model:");
//			System.out.println(sentence);
		
		
	}

}
