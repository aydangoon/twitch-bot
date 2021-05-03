import javax.swing.JFrame;

import mc.MarkovChainBuilder;
import utils.Constants;
import vis.MarkovChainVisualization;

public class TwitchBotMain {

	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		window.setTitle("Twitch Bot");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		
		MarkovChainBuilder mcb = new MarkovChainBuilder();

		window.getContentPane().add(mcb);
		window.setSize(mcb.getSize());
		window.setVisible(true);
		
		mcb.parseMessagesFromCSV(Constants.FILENAME);
		mcb.buildMarkovChain();
		window.getContentPane().remove(mcb);
		
		MarkovChainVisualization<String> mcv = new MarkovChainVisualization<String>(mcb.getMarkovChain(), mcb.getAverageMessageLength());
		window.getContentPane().add(mcv);
		window.addKeyListener(mcv);
		window.setSize(mcv.getPreferredSize());
		
	}

}
