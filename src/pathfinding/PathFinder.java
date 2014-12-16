package pathfinding;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import map.Tile;
import map.TileMap;
import Units.Unit;

/**
 * The PathFinder class finds the possible destinations and paths of Units
 * 
 * @author N R Callahan
 * @version 1.0, 22April2014
 */
public class PathFinder {
	private TileMap map;
	private Unit mover;
	private Map<Point, Node> nodeMap;

	/**
	 * 
	 * @param map
	 *            A TileMap representation of the game board
	 * @param mover
	 *            The unit which you are finding paths for
	 */
	public PathFinder(TileMap map, Unit mover) {
		this.map = map;
		this.mover = mover;
		nodeMap = new HashMap<Point, Node>();
		loadNodes();
		dijkstra();
	}

	private void loadNodes() {
		// load all the tiles from the map into a HashMap (their position is the
		// key)
		for (int i = 0; i < map.getNumRows(); i++) {
			for (int j = 0; j < map.getNumCols(); j++) {
				assert (map.getTile(new Point(i, j)) != null);
				Node newNode = new Node(map.getTile(new Point(i, j)), new Point(i, j));
				nodeMap.put(new Point(i, j), newNode);
			}
		}
		// --Iterate through every node in the map
		for (Map.Entry<Point, Node> entry : nodeMap.entrySet()) {
			// --Get the neighbors associated with that point
			List<Point> neighbors = map.getNeighbors(entry.getKey());
			Node curNode = entry.getValue();
			for (Point p : neighbors) {
				curNode.neighbors.add(p);
				nodeMap.get(p).neighbors.add(entry.getKey());
			}
		}
	}

	/**
	 * Returns all the points the Unit can move to
	 * 
	 * @return A list of points within the unit's range. If there are no
	 *         possible moves returns null
	 */
	public List<Point> getPossibleDestinations() {
		List<Point> dest = new ArrayList<Point>();
		for (Map.Entry<Point, Node> entry : nodeMap.entrySet()) {
			Node curNode = entry.getValue();
			Path curNodePath = getPath(curNode);
			if (curNodePath.getLength() > 1) { // 1 is current loc
				dest.add(curNodePath.getLast());
			}
		}
		return dest;
	}

	private void dijkstra() {
		computeRoutes(nodeMap.get(mover.getLocation()));
	}
	

	private void computeRoutes(Node source) {
		// --set all tiles unmarked, dist to infinity, prev to null (DONE IN
		// Node CONSTRUCTOR)
		// --visit src tile, set src prev null
		source.minDistance = 0;
		PriorityQueue<Node> nodes = new PriorityQueue<Node>();
		nodes.add(source);
		// --while there exists an unmarked tile with infinity distance or dest
		// tile is not marked
		while (!nodes.isEmpty()) {
			Node current = nodes.poll();
			// --foreach neighbor in all unmarked neighbors
			for (int i = 0; i < current.neighbors.size(); i++) {
				Node neighbor = nodeMap.get(current.neighbors.get(i));

				double weight = neighbor.tile.getMoveCost();
				double distance = current.minDistance + weight;
				// --only change neighbors dist who get a shorter path from this
				// tile
				if (distance < neighbor.minDistance) {
					nodes.remove(neighbor);
					neighbor.minDistance = distance;
					neighbor.previous = current;
					nodes.add(neighbor);
				}
			}
		}
	}

	public List<Point> getInRangeTiles(Point p, Unit u) {
		List<Point> points = new ArrayList<Point>();
		for (int i = p.x - u.getRange(); i <= p.x + u.getRange(); i++) {
			for (int j = p.y - u.getRange(); j <= p.y + u.getRange(); j++) {
				Point check = new Point(i, j);
				if (!map.isPointInBounds(check)) {
					continue;
				}
				if (u.getRange() >= manhattanDistance(check, p)) {
					points.add(check);
				}
			}
		}
		return points;
	}
	public Point getBestMoveToward(final Point goal, Unit u) {
		PathFinder pathFinder = new PathFinder(map, u);
		List<Point> validMoves = pathFinder.getPossibleDestinations();
		if (validMoves.size() == 0) {
			return null;
		}
		return Collections.min(validMoves, new Comparator<Point>() {
			public int compare(Point a, Point b) {
				return (int) (goal.distanceSq(a) - goal.distanceSq(b));
			}
		});
	}
	private int manhattanDistance(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	public double distanceToTile(Point a) {
		Node target = nodeMap.get(a);
		Node current = target;
		double cost = 0;
		while (current.previous != null) {
			cost += current.tile.getMoveCost();
			current = current.previous;
		}
		//cost += target.tile.getMoveCost();
		System.err.println("Cost: " + cost);
		return cost;
	}
	public boolean isAdjecent(Point a, Point b) {
		if(Math.abs(a.x - b.x) == 1 && Math.abs(a.y-b.y) == 1) { return true; }
		return false;
	}
	/**
	 * Returns the path from the unit's start point to the target
	 * 
	 * @param target
	 *            The target destination
	 * @return The path to the target
	 */
	public Path getPath(Point target) {
		Path path = getPath(nodeMap.get(target));
		return path;
	}

	private Path getPath(Node target) {
		List<Point> path = new ArrayList<Point>();
		Node current = target;
		double cost = 0 - current.tile.getMoveCost();
		while (current.previous != null) {
			cost += current.tile.getMoveCost();
			path.add(current.previous.position);
			current = current.previous;
		}
		Collections.reverse(path);

		path.add(target.position);
		cost += target.tile.getMoveCost();


		// --Make sure path cost is not > unit movement points

		if (cost > mover.getMovePoints()) {
			return new Path(new LinkedList<Point>());
		}
		
		return new Path(path);
	}
	

	public Map<Point, Node> getHashMap() {
		return nodeMap;
	}

	public class Node implements Comparable<Node> {
		public Tile tile;
		public Node next;
		public Node previous;
		public Point position;
		public List<Point> neighbors;
		public double minDistance = Double.POSITIVE_INFINITY;

		public Node(Tile tile, Point position) {
			this.tile = tile;
			this.position = position;
			next = null;
			previous = null;
			neighbors = new ArrayList<Point>();
		}

		public String toString() {
			return "Node (" + this.position.x + "," + this.position.y + ")";
		}

		@Override
		public int compareTo(Node arg0) {
			Double delta = minDistance - arg0.minDistance;
			if (delta > 0) {
				return 1;
			} else if (delta < 0) {
				return -1;
			} else
				return 0;
		}
	}
}
