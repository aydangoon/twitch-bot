package utils;
import java.awt.Color;
import java.awt.Font;

// Constants used throughout the project.
public final class Constants {

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 800;
	public static final double GRAPH_SCALE_FACTOR = 5; // im not
	public static final int NODE_RADIUS = 10;
	public static final int EDGE_THICKNESS = 2;
	public static final Color NODE_COLOR = Color.decode("#444444");
	public static final Color EDGE_COLOR = Color.decode("#cccccc");
	public static final Color TEXT_COLOR = Color.black;//Color.decode("#82C09A");
	public static final double CAMERA_ACCEL = 0.5;
	public static final Font FONT = new Font("courier new", Font.BOLD, 14);
	public static final int MILLISECONDS_PER_FRAME = 1000 / 60;
	public static final String FILENAME = "./data/clean.csv";
	public static final int LINES = 239823;
	public static final int SPACE_BETWEEN_NODES = 10 * NODE_RADIUS;
}
