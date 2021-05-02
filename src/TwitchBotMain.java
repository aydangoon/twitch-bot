import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mc.Digraph;
import mc.MarkovChainBuilder;
import mc.Message;
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
