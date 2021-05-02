package mc;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.*;

import utils.Constants;

public class MarkovChainBuilder extends JPanel {
	
	private List<Message> messages;
	Digraph<String> mc;
	private long avgMsgLen;
	private JLabel actionName;
	private JLabel progressPercent;
	private JProgressBar progress;
	
	public MarkovChainBuilder() {
		this.messages = new ArrayList<Message>();
		this.mc = new Digraph<String>();
		this.progress = new JProgressBar();
		this.progressPercent = new JLabel("0%");
		this.actionName = new JLabel();
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.setSize(400, 100);
		
		this.add(this.actionName);
		this.add(this.progressPercent);
		this.add(this.progress);
	}
	
	public void buildMarkovChain() {
		
		this.progress.setValue(0);
		this.progress.setMaximum(this.messages.size());
		this.progressPercent.setText("0%");
		this.actionName.setText("Building Markov Chain...");
		
		long start = System.currentTimeMillis();
		
		List<String> words = new LinkedList<String>();
		Map<String, Integer> proceeds = new HashMap<String, Integer>();
		
		for (int i = 0; i < this.messages.size(); i++) {
			Message message = this.messages.get(i);
			String[] msgWords = message.getText().split(" ");
			this.avgMsgLen += msgWords.length;
			
			for (String word : msgWords) {
				words.add(word);
			}
			
			// add word nodes
			for (int j = 0; j < words.size(); j++) {
				if (!this.mc.hasNode(words.get(j))) 
					this.mc.addNode(words.get(j));
			}
			// edges
			for (int j = 0; j < words.size() - 1; j++) {
				
				String src = words.get(j);
				String tgt = words.get(j + 1);
				
				if (!this.mc.hasEdge(src, tgt))
					this.mc.addEdge(src, tgt, 1);
				else
					this.mc.setWeight(src, tgt, this.mc.getWeight(src, tgt) + 1);
				
				if (!proceeds.containsKey(src))
					proceeds.put(src, 1);
				else
					proceeds.put(src, proceeds.get(src) + 1);	
			}
			
			words.clear();
			this.updateProgress();
		}
		
		this.progress.setValue(0);
		this.progress.setMaximum(proceeds.keySet().size());
		this.progressPercent.setText("0%");
		this.actionName.setText("Generating Node Iterator...");
		
		// normalize
		Iterator<String> iter = proceeds.keySet().iterator();
		this.actionName.setText("Normalizing Edge Weights...");
		while (iter.hasNext()) {
			String src = iter.next();
			for (String tgt : this.mc.childrenOf(src)) {
				this.mc.setWeight(src, tgt, this.mc.getWeight(src, tgt) / proceeds.get(src));
				
			}
			this.updateProgress();
		}
		
		this.avgMsgLen /= messages.size();
		
		System.out.println("time taken: " + (System.currentTimeMillis() - start));
		
	}
	
	public void parseMessagesFromCSV(String filename) {

		File file = new File(filename);
		Scanner sc;
		try {
			
			this.progress.setValue(0);
			this.progress.setMaximum(Constants.LINES - 2);
			this.actionName.setText("Parsing CSV file...");
			
			sc = new Scanner(file);
			int numHeaders = sc.nextLine().split(", ").length;
			String[] row;
			while (sc.hasNext()) {
				row = sc.nextLine().split(", ", numHeaders);
				this.messages.add(new Message(
						row[0], 
						Integer.parseInt(row[1]) == 1, 
						Integer.parseInt(row[2]) == 1, 
						Integer.parseInt(row[3]) == 1, 
						Integer.parseInt(row[4]), 
						row[5])
				);
				this.updateProgress();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Digraph<String> getMarkovChain() {
		return mc;
	}
	
	public int getAverageMessageLength() {
		return (int)this.avgMsgLen;
	}
	
	private void updateProgress() {
		this.progress.setValue(this.progress.getValue() + 1);
		this.progressPercent.setText((int)(100*this.progress.getPercentComplete()) + "%");
	}
}
