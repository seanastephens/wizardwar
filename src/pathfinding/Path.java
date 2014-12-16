package pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
/**
 * The PathFinder class finds the possible destinations and paths of Units
 * 
 * @author N R Callahan
 * @version 1.0, 22April2014
 */
public class Path {
	private List<Point> moves = new ArrayList<Point>();
/**
 *  Create a new empty path
 */
	public Path() {
		moves = new ArrayList<Point>();
	}
/**
 * Create a path from a list of point
 * @param path A list of points in the path
 */
	public Path(List<Point> path) {
		moves.addAll(path);
	}
/**
 * 
 * @return length of the path
 */
	public int getLength() { return moves.size(); }
/**
 * Add a point to the end of the path
 * @param newPoint the new ending point
 */
	public void appened(Point newPoint) { moves.add(newPoint); }
/**
 * Check if the path contains a specific point
 * @param c The point to check for
 * @return Returns true is point exists, false otherwise
 */
	public boolean contains(Point c) { return moves.contains(c); }
/**
 * Get a specific step in the path
 * @param i The step you want
 * @return The point at the i'th step
 */
	public Point getStep(int i) { return moves.get(i); }
/**
 * Get the entire list of points in the path
 */
	public List<Point> getPath() { return moves;}

	public String toString() { return moves.toString(); }
/**
 * Get the last element in the path
 * @return The ending point
 */
	public Point getLast() { return moves.get(getLength() - 1);	}
}
