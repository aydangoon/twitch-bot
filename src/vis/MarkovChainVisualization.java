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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

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
	private int xInput;
	private int yInput;
	private int zoomInput;
	private Camera cam;
	private Timer t;
	
	// jcomponents
	private JPanel infoPanel;
	private JLabel camInfo;
	private JPanel senGen;
	private JLabel sentence;
	private Map<String, String> emotes;
	
	public MarkovChainVisualization(Digraph<T> g) {
		
		this.emotes = EmoteFactory.getEmoteMap();
		
		this.mc = g;
		this.buildVisuals(g);
		
		cam = new Camera();
		zoomInput = 0;
		
		t = new Timer(Constants.MILLISECONDS_PER_FRAME, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		t.start();
		
		this.infoPanel = new JPanel();
		this.infoPanel.setPreferredSize(new Dimension(Constants.WIDTH - 10, 175));
		this.infoPanel.setFont(Constants.FONT);
		this.infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.infoPanel.setBorder(BorderFactory.createLineBorder(Color.decode("#82C09A")));
		
		this.camInfo = new JLabel();
		this.sentence = new JLabel();
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
		genSen.setFocusable(false);
		JPanel opts = new JPanel();
		opts.setLayout(new BoxLayout(opts, BoxLayout.Y_AXIS));
		opts.add(new JLabel("Message Type:"));
		opts.add(new JCheckBox("subscriber", true));
		opts.add(new JCheckBox("moderator", true));
		opts.add(new JCheckBox("bot", true));
		opts.add(new JCheckBox("pleb", true));
		opts.add(genSen);
		this.senGen.add(opts);
		this.senGen.add(this.sentence);
		
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.setFocusable(true);
		this.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		this.add(this.infoPanel);
		this.infoPanel.add(this.camInfo);
		this.infoPanel.add(this.senGen);
		//this.addKeyListener(this);
		

		
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Set<NodeVisual<T>> hidden = new HashSet<NodeVisual<T>>();
		
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
				hidden.add(node);
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
			
			if (!src.equals(tgt) && (!hidden.contains(src) || !hidden.contains(tgt))) {
				
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
			x = (int)(cam.getZoom() * (node.getX() - cam.getX() + xOffset - nr));
			y = (int)(cam.getZoom() * (node.getY() - cam.getY() + yOffset - nr));
			g.setColor(Constants.NODE_COLOR);
			g.fillOval(x, y, (int)(2 * nr * cam.getZoom()), (int)(2 * nr * cam.getZoom()));
			g.setColor(Constants.TEXT_COLOR);
			text = node.getValue().toString();
			
			g.drawString(text, x + (int)(cam.getZoom() * nr) - (metrics.stringWidth(text) / 2), y);
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
		T word = mc.nodes().get((int)(Math.random() * mc.nodeCount()));
		int c = 0;
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
				
				if (emotes.containsKey(word)) {
					sentence.append("<img height=\"30\" src=\""+emotes.get(word)+"\"></img> ");
				} else {
					sentence.append(word + " ");
				}
				
				word = nextWords.get(i);
				if (c++ == 10) {
					c = 0;
					sentence.append("<br></br>");
				}
			}
		} while (nextWords.size() > 0 && iters++ < 20);
		
		this.sentence.setText("<html><div>" + sentence.toString() + "</div></html>");
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
