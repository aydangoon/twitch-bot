package vis;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utils.BadWordManager;
import utils.Constants;
import utils.Sunflower;
import utils.UniformSquare;
import utils.Tuple;

import mc.Digraph;

// A visualizer for a Markov Chain.
@SuppressWarnings("serial")
public class MarkovChainVisualization<T> extends JPanel implements KeyListener {

	private List<NodeVisual<T>> nodes;
	private List<EdgeVisual<T>> edges;
	private Digraph<T> mc;
	private int avgMsgLen;
	private double msgProbability;
	private boolean customLen;
	private int msgLen;
	private int xInput;
	private int yInput;
	private int zoomInput;
	private Camera cam;
	private Timer t;
	
	private Set<T> sentenceWords;
	
	// jcomponents
	private JPanel infoPanel;
	private JLabel camInfo;
	private JLabel graphInfo;
	private JPanel senGen;
	private JLabel sentence;
	private Map<String, String> emotes;
	
	public MarkovChainVisualization(Digraph<T> g, int avgMsgLen) {
		
		this.emotes = EmoteFactory.getEmoteMap();
		
		this.mc = g;
		this.avgMsgLen = avgMsgLen;
		this.msgProbability = 0.0;
		this.customLen = true;
		this.msgLen = avgMsgLen;
		
		this.buildVisuals(g);
		
		cam = new Camera();
		zoomInput = 0;
		
		t = new Timer(Constants.MILLISECONDS_PER_FRAME, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		t.start();
		
		this.sentenceWords = new HashSet<T>();
		
		this.infoPanel = new JPanel();
		//this.infoPanel.setPreferredSize(new Dimension(Constants.WIDTH - 10, 200));
		this.infoPanel.setFont(Constants.FONT);
		this.infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.infoPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#82C09A")));
		
		this.camInfo = new JLabel();
		this.camInfo.setPreferredSize(new Dimension(100, 100));
		this.camInfo.setBorder(BorderFactory.createLineBorder(Color.decode("#82c09a")));
		
		this.graphInfo = new JLabel("<html><b>Graph Info</b><div>Nodes: "+this.nodes.size()+ "</div><div>Edges:"+this.edges.size()+"</div></html>");
		this.graphInfo.setPreferredSize(new Dimension(100, 100));
		this.graphInfo.setBorder(BorderFactory.createLineBorder(Color.decode("#82c09a")));
		
		this.sentence = new JLabel();
		this.sentence.setPreferredSize(new Dimension(Constants.WIDTH / 2, 200));
		this.senGen = new JPanel();
		this.senGen.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.senGen.setBorder(BorderFactory.createLineBorder(Color.decode("#82c09a")));
		
		JButton genSen = new JButton("Generate Sentence");
		genSen.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        generateSentence();
		    }
		});
		JButton clearSen = new JButton("Clear Sentence");
		clearSen.addActionListener(new ActionListener() {
			@Override
		    public void actionPerformed(ActionEvent e) {
		        clearSentence();
		    }
		});
		genSen.setFocusable(false);
		clearSen.setFocusable(false);
		
		JSlider msgLenSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, this.msgLen);
		msgLenSlider.setBackground(Color.BLACK);
		msgLenSlider.setForeground(Color.BLACK);
		msgLenSlider.setFocusable(false);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(1, new JLabel("1") );
		labelTable.put(50, new JLabel("50") );
		msgLenSlider.setLabelTable(labelTable);
		msgLenSlider.setMajorTickSpacing(10);
		msgLenSlider.setPaintTicks(true);
		msgLenSlider.setPaintLabels(true);
		
		msgLenSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				msgLen = msgLenSlider.getValue();
			}
		});
		
		JCheckBox custom = new JCheckBox("Custom", true);
		custom.setFocusable(false);
		custom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msgLenSlider.setVisible(custom.isSelected());
				customLen = custom.isSelected();
			}
		});
		
		JPanel opts = new JPanel();
		opts.setLayout(new BoxLayout(opts, BoxLayout.Y_AXIS));
		opts.add(new JLabel("<html><b>Sentence Generator</b><div>Message Length:</div></html>"));
		opts.add(custom);
		opts.add(msgLenSlider);
		opts.add(genSen);
		opts.add(clearSen);
		this.senGen.add(opts);
		this.senGen.add(this.sentence);
		
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		this.setFocusable(true);
		this.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		this.add(this.infoPanel);
		this.infoPanel.add(this.camInfo);
		this.infoPanel.add(this.graphInfo);
		this.infoPanel.add(this.senGen);
		
		this.infoPanel.setPreferredSize(this.infoPanel.getPreferredSize());
		//this.addKeyListener(this);
		

		
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Set<NodeVisual<T>> offscreenNodes = new HashSet<NodeVisual<T>>();
		Set<NodeVisual<T>> hiddenNodes = new HashSet<NodeVisual<T>>();
		
		Font font = Constants.FONT.deriveFont((float)(14 * cam.getZoom() + 7));
		FontMetrics metrics = g.getFontMetrics(font);
		g.setFont(font);
		
		int xOffset = (int)(this.getWidth() / (2 * cam.getZoom()));
		int yOffset = (int)(this.getHeight() / (2 * cam.getZoom()));
		int nr = Constants.NODE_RADIUS;
		
		double forgivenessFactor = 1.5;
		int hiddenXOffset = (int)(forgivenessFactor * xOffset);
		int hiddenYOffset = (int)(forgivenessFactor * yOffset);
		for (NodeVisual<T> node : nodes) {
			if (node.getX() < cam.getX() - hiddenXOffset || node.getX() > cam.getX() + hiddenXOffset
					|| node.getY() < cam.getY() - hiddenYOffset || node.getY() > cam.getY() + hiddenYOffset) {
				offscreenNodes.add(node);
			} 
			if (!this.sentenceWords.isEmpty() && !this.sentenceWords.contains(node.getValue())) {
				hiddenNodes.add(node);
			}
		}
		
		int x, y, x2, y2, sx, sy;
		NodeVisual<T> src;
		NodeVisual<T> tgt;
		for (EdgeVisual<T> edge : edges) {
			src = edge.getSrc();
			tgt = edge.getTgt();
			x = (int)(cam.getZoom() * (src.getX() + xOffset - cam.getX()));
			y = (int)(cam.getZoom() * (src.getY() + yOffset - cam.getY()));
			x2 = (int)(cam.getZoom() * (tgt.getX() + xOffset - cam.getX()));
			y2 = (int)(cam.getZoom() * (tgt.getY() + yOffset - cam.getY()));
			
			if (!src.equals(tgt) && 
			    !hiddenNodes.contains(src) && !hiddenNodes.contains(tgt) && 
			    (!offscreenNodes.contains(src) || !offscreenNodes.contains(tgt))) {
				
				sx = x2 - x;
				sy = y2 - y;
				int len = (int) Math.sqrt(sx*sx + sy*sy);
				if (len == 0) {
					len = 1;
				}
				sx = (int)(cam.getZoom() * nr) * sx / len;
				sy = (int)(cam.getZoom() * nr) * sy / len;
				x2 -= sx;
				y2 -= sy;
				int px = (int)(0.6 * sy);
				int py = (int)(-0.6 * sx);
				
				int p1x = x2 - sx + px;
				int p1y = y2 - sy + py;
				
				int p2x = x2 - sx - px;
				int p2y = y2 - sy - py;
				
				int[] poiX = { p1x, p2x, x2 };
				int[] poiY = { p1y, p2y, y2 };
				
				// color is a function of probability
				g.setColor(new Color((float)(0.25*(1-edge.getWeight())) + 0.5f, 0.75f, 0.75f));
				g.drawLine(x, y, x2, y2);
				g.fillPolygon(poiX, poiY, 3);
				
				// edge labels really clutter the graph. Maybe don't include?
				//g.setColor(Constants.TEXT_COLOR);
				//g.drawString(String.format("%.2f", edge.getWeight()), (x + x2) / 2, (y + y2) / 2);
			}
			
		}
		
		String text;
		for (NodeVisual<T> node : nodes) {
			if (!hiddenNodes.contains(node) && !offscreenNodes.contains(node)) {
				x = (int)(cam.getZoom() * (node.getX() - cam.getX() + xOffset - nr));
				y = (int)(cam.getZoom() * (node.getY() - cam.getY() + yOffset - nr));
				g.setColor(Constants.NODE_COLOR);
				g.fillOval(x, y, (int)(2 * nr * cam.getZoom()), (int)(2 * nr * cam.getZoom()));
				g.setColor(Constants.TEXT_COLOR);
				text = node.getValue().toString();
				g.drawString(text, x + (int)(cam.getZoom() * nr) - (metrics.stringWidth(text) / 2), y);
			}	
		}
	}
	
	public void tick() {

		cam.update(this.xInput, this.yInput, this.zoomInput);
		
		this.camInfo.setText("<html><b>Camera Info</b><div>Zoom: " + String.format("%1.2f", this.cam.getZoom()) 
			+ "</div><div>x: " + this.cam.getX()
			+ "</div>y: " + this.cam.getY());
		
		this.repaint();
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				this.yInput = -1;
				break;
			case KeyEvent.VK_DOWN:
				this.yInput = 1;
				break;
			
		}
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_Z:
				this.zoomInput = 1;
				break;
			case KeyEvent.VK_X:
				this.zoomInput = -1;
				break;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				this.xInput = -1;
				break;
			case KeyEvent.VK_RIGHT:
				this.xInput = 1;
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				this.yInput = 0;
				break;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				this.xInput = 0;
				break;
			case KeyEvent.VK_Z:
			case KeyEvent.VK_X:
				this.zoomInput = 0;
		}
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}
	
	private void generateSentence() {
		
		int msgLen = this.customLen ? this.msgLen : (int)(this.avgMsgLen + ((new Random()).nextGaussian() * 2));
		this.sentenceWords.clear();
		this.msgProbability = 1.0;
		
		T word;
		do {
			word = mc.nodes().get((int)(Math.random() * mc.nodeCount()));
		} while (mc.childCountOf(word) == 0);
		
		System.out.println(msgLen + " " + word);
		int iters = 0;
		StringBuilder sentence = new StringBuilder();
		List<T> nextWords;
		do {
			nextWords = this.mc.childrenOf((T) word);
			if (nextWords.size() > 0) {
				double random = Math.random();
				int i = 0;
				while (random > this.mc.getWeight((T) word, nextWords.get(i))) {
					random -= this.mc.getWeight((T) word, nextWords.get(i));
					i++;
				}
				
				this.msgProbability *= this.mc.getWeight((T) word, nextWords.get(i));
				
				if (BadWordManager.shouldCensor((String)word)) {
					word = (T) BadWordManager.censor((String)word);
				}
				
				this.sentenceWords.add(word);
				
				if (emotes.containsKey(word)) {
					sentence.append("<img height=\"30\" src=\""+emotes.get(word)+"\"></img> ");
				} else {
					sentence.append(word + " ");
				}
				
				word = nextWords.get(i);
			}
		} while (nextWords.size() > 0 && iters++ < msgLen);
		
		this.sentence.setText("<html><div><b>Message:</b> " + sentence.toString() + 
				"</div><div><b>Message Probability:</b> "+this.msgProbability+"</html>");
		this.senGen.setPreferredSize(this.senGen.getPreferredSize());
		this.infoPanel.setPreferredSize(this.infoPanel.getPreferredSize());
	}
	
	private void clearSentence() {
		this.sentence.setText("");
		this.sentenceWords.clear();
	}
	
	private void buildVisuals(Digraph<T> g) {
		this.nodes = new LinkedList<NodeVisual<T>>();
		this.edges = new LinkedList<EdgeVisual<T>>();
		
		Map<T, NodeVisual<T>> lookup = new HashMap<T, NodeVisual<T>>();
		
		List<T> rawNodes = g.nodes();
		//Sunflower s = new Sunflower(g.nodeCount());
		UniformSquare us = new UniformSquare((int)Math.sqrt(g.nodeCount()) + 1);
		Tuple<Integer, Integer> pos;
		
		// generate node visuals and add to lookup table
		// for quick generation of edge visuals
		
		for (T node : rawNodes) {
			pos = us.nextPoint();
			lookup.put(node, new NodeVisual<T>(node, pos._1, pos._2));
		}
		
		// generate and store edge visuals, store node visuals
		for (T src : rawNodes) {
			for (T tgt : g.childrenOf(src)) {
				this.edges.add(new EdgeVisual<T>(lookup.get(src), lookup.get(tgt), g.getWeight(src, tgt)));
			}
			this.nodes.add(lookup.get(src));
		}
	}

}
