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
		this.setSize(400, 200);
		
		this.add(this.actionName);
		this.add(this.progressPercent);
		this.add(this.progress);
	}
	
	public void buildMarkovChain() {
		
		this.progress.setValue(0);
		this.progress.setMaximum(this.messages.size() / 5);
		this.progressPercent.setText("0%");
		this.actionName.setText("Gathering all unique words...");
		
		List<String> words = new LinkedList<String>();
		for (int i = 0; i < this.messages.size() / 5; i++) {
			Message message = this.messages.get(i);
			for (String word : message.getText().replaceAll("[^ a-zA-Z0-9]", "").toLowerCase().split(" ")) {
				words.add(word);
			}
			this.updateProgress();
		}
		
		Map<String, Integer> proceeds = new HashMap<String, Integer>();
		
		this.actionName.setText("Building nodes...");
		this.progress.setMaximum(words.size());
		this.progress.setValue(0);
		// add word nodes
		for (int i = 0; i < words.size(); i++) {
			if (!this.mc.hasNode(words.get(i))) 
				this.mc.addNode(words.get(i));
			this.updateProgress();
		}
			
		
		// calculate total occurances
		this.actionName.setText("Building edges...");
		this.progress.setMaximum(words.size());
		this.progress.setValue(0);
		for (int i = 0; i < words.size() - 1; i++) {
			
			String src = words.get(i);
			String tgt = words.get(i + 1);
			
			if (!this.mc.hasEdge(src, tgt))
				this.mc.addEdge(src, tgt, 1);
			else
				this.mc.setWeight(src, tgt, this.mc.getWeight(src, tgt) + 1);
			
			if (!proceeds.containsKey(src))
				proceeds.put(src, 1);
			else
				proceeds.put(src, proceeds.get(src) + 1);	
			
			this.updateProgress();
		}
		
		
		this.actionName.setText("Normalizing probabilites...");
		this.progress.setMaximum(proceeds.keySet().size());
		this.progress.setValue(0);
		// normalize
		Iterator<String> iter = proceeds.keySet().iterator();
		while (iter.hasNext()) {
			String src = iter.next();
			for (String tgt : this.mc.childrenOf(src)) {
				this.mc.setWeight(src, tgt, this.mc.getWeight(src, tgt) / proceeds.get(src));
			}
			this.updateProgress();
		}
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
	
	private void updateProgress() {
		this.progress.setValue(this.progress.getValue() + 1);
		this.progressPercent.setText((int)(100*this.progress.getPercentComplete()) + "%");
	}
}
