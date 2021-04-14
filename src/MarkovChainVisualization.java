import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Timer;

public class MarkovChainVisualization<T> extends JPanel implements KeyListener {

	private List<NodeVisual<T>> nodes;
	private List<EdgeVisual<T>> edges;
	private int hor;
	private int ver;
	private Camera cam;
	private Timer t;
	
	public MarkovChainVisualization(Digraph<T> g) {
		
		nodes = new LinkedList<NodeVisual<T>>();
		edges = new LinkedList<EdgeVisual<T>>();
		
		Map<T, NodeVisual<T>> lookup = new HashMap<T, NodeVisual<T>>();
		
		List<T> rawNodes = g.nodes();
		Sunflower s = new Sunflower(g.nodeCount());
		Tuple<Integer, Integer> pos;
		
		// generate node visuals and add to lookup table
		// for quick generation of edge visuals
		for (T node : rawNodes) {
			pos = s.nextPoint();
			lookup.put(node, new NodeVisual<T>(node, pos._1, pos._2));
		}
		
		// generate and store edge visuals, store node visuals
		for (T src : rawNodes) {
			for (T tgt : g.childrenOf(src)) {
				edges.add(new EdgeVisual<T>(lookup.get(src), lookup.get(tgt), g.getWeight(src, tgt)));
			}
			nodes.add(lookup.get(src));
		}
		
		cam = new Camera();
		
		t = new Timer(1000 / 60, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
		t.restart();
		
		this.setFocusable(true);
		this.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		this.addKeyListener(this);
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Set<NodeVisual<T>> hidden = new HashSet<NodeVisual<T>>();
		
		g.clearRect(0, 0, Constants.WIDTH, Constants.HEIGHT);
		g.setFont(Constants.FONT);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(1));
		
		int xOffset = Constants.WIDTH / 2;
		int yOffset = Constants.HEIGHT / 2;
		int nr = Constants.NODE_RADIUS;
		
		for (NodeVisual<T> node : nodes) {
			if (node.getX() < cam.getX() - xOffset || node.getX() > cam.getX() + xOffset
					|| node.getY() < cam.getY() - yOffset || node.getY() > cam.getY() + yOffset) {
				hidden.add(node);
			}
		}
		
		int x, y, x2, y2, sx, sy;
		NodeVisual<T> src;
		NodeVisual<T> tgt;
		for (EdgeVisual<T> edge : edges) {
			src = edge.getSrc();
			tgt = edge.getTgt();
			x = src.getX() + xOffset - cam.getX();
			y = src.getY() + yOffset - cam.getY();
			x2 = tgt.getX() + xOffset - cam.getX();
			y2 = tgt.getY() + yOffset - cam.getY();
			
			if (!src.equals(tgt)) {
				
				sx = x2 - x;
				sy = y2 - y;
				int len = (int) Math.sqrt(sx*sx + sy*sy);
				sx = nr * sx / len;
				sy = nr * sy / len;
				x2 -= sx;
				y2 -= sy;
				int px = (int)(0.6 * sy);
				int py = (int)(-0.6 * sx);
				
				int p1x = x2 - sx + px;
				int p1y = y2 - sy + py;
				
				int p2x = x2 - sx - px;
				int p2y = y2 - sy - py;
				
				int[] poiX = {p1x, p2x, x2};
				int[] poiY = {p1y, p2y, y2};
				
				if (!hidden.contains(src) || !hidden.contains(tgt)) {
					g.setColor(Constants.EDGE_COLOR);
					g.drawLine(x, y, x2, y2);
					g.fillPolygon(poiX, poiY, 3);
					g.setColor(Constants.TEXT_COLOR);
					g.drawString(String.format("%.2f", edge.getWeight()), (x + x2) / 2, (y + y2) / 2);
				}	
				
			}
			
		}
		
		
		for (NodeVisual<T> node : nodes) {
			x = node.getX() + xOffset - cam.getX() - nr;
			y = node.getY() + yOffset - cam.getY() - nr;
			g.setColor(Constants.NODE_COLOR);
			g.fillOval(x, y, 2 * nr, 2 * nr);
			g.setColor(Constants.TEXT_COLOR);
			g.drawString(node.getValue().toString(), x, y);
		}
		
	}
	
	public void tick() {

		cam.update(this.hor, this.ver);		
		this.repaint();
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				this.ver = -1;
				break;
			case KeyEvent.VK_DOWN:
				this.ver = 1;
				break;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				this.hor = -1;
				break;
			case KeyEvent.VK_RIGHT:
				this.hor = 1;
				break;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				this.ver = 0;
				break;
		}
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				this.hor = 0;
				break;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

}
