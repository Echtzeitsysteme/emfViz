package graphVisualization;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.locationtech.jts.geom.Envelope;

//import com.mxgraph.model.mxGeometry;

public class EdgePlanner {
	public static final double EPS = 0.0001;
	private Object edge;
	private Envelope sourceCell;
	private Envelope targetCell;
	private Point2D sourceCenter;
	private Point2D targetCenter;
	private LinkedList<Point2D> targetPoints;
	private LinkedList<Point2D> possibleOrigins;
	
	private HashMap<Point2D, Point2D> cameFrom;
	private HashMap<Point2D, Double> gscore;
	private HashMap<Point2D, Double> fscore;

	private double velocityPenalty =0.0000005;
	private double aStarStepsize = 10.0;
	private double minInterPointDist = 20.0;
	private double minCurveStrength =2.0;
	private double edgeSize = 15.0;
	
	private Grid grid;
	private Envelope bounds;
	
	public LinkedList<Point2D> fullPath;
	
	private int pathCutoff = 3;
	
	public EdgePlanner(Object edge, Envelope source, Envelope target, Envelope bounds, Grid grid){//RTree<String, Rectangle> blockTree) {
		this.edge = edge;
		this.sourceCell = source;
		this.targetCell = target;
		this.sourceCenter = new Point2D.Double(source.centre().x, source.centre().y);
		this.targetCenter = new Point2D.Double(target.centre().x, target.centre().y);
		
		this.grid = grid;
		this.bounds = bounds;
	
		possibleOrigins = getCellBorderPoints(sourceCenter, sourceCell);
		targetPoints = getCellBorderPoints(targetCenter, targetCell);

	}
	
	
	private LinkedList<Point2D> getCellBorderPoints(Point2D nodePosition, Envelope cellBounds) {		
		LinkedList<Point2D> borderPoints = new LinkedList<Point2D>();		
		Point2D topLeft = new Point2D.Double(cellBounds.getMinX(), cellBounds.getMaxY());
		
		for(double x = topLeft.getX(); x <  topLeft.getX() + cellBounds.getWidth(); x += cellBounds.getWidth()/10.0) {
			Point2D p1 = new Point2D.Double(x, topLeft.getY());
			borderPoints.add(p1);
			Point2D p2 = new Point2D.Double(x, topLeft.getY()-cellBounds.getHeight());
			borderPoints.add(p2);
		}
		
		for(double y = topLeft.getY(); y >  topLeft.getY() - cellBounds.getHeight(); y -= cellBounds.getHeight()/10.0) {
			Point2D p3 = new Point2D.Double(topLeft.getX(), y);
			borderPoints.add(p3);
			Point2D p4 = new Point2D.Double(topLeft.getX() + cellBounds.getWidth(), y);
			borderPoints.add(p4);
		}
		
		return borderPoints;
	}	
	
	private boolean intersectsRectangle(Envelope rectangle, Point2D p) {
		return rectangle.contains(p.getX(), p.getY());
//		return rectangle.getMinX() < p.gx && p.x < rectangle.getMaxX() && p.y > rectangle.getMinY() && p.y < rectangle.getMaxY();
	}
	
	private double h(Point2D start) {
		return Math.sqrt(Math.pow(start.getX() - targetCenter.getX() , 2) + Math.pow(start.getY() - targetCenter.getY(), 2))
					+ 	grid.getCostForArea(
						new Envelope(	start.getX()-aStarStepsize/2, 
										start.getX()+aStarStepsize/2, 
										start.getY()-aStarStepsize/2, 
										start.getY()+aStarStepsize/2)
						);
	}
	
	private double d(Point2D p1, Point2D p2) {
		return p1.distance(p2);
	}
	
	private double velocityPenality(Point2D current, Point2D next) {
		if(!cameFrom.containsKey(current))
			return 0;
		
		double velXCurrent = current.getX()-next.getX();
		double velYCurrent = current.getY()-next.getY();
		
		Point2D prev = cameFrom.get(current);
		
		double velXPrev = prev.getX() - current.getX();
		double velYPrev = prev.getY() - current.getX();
		
		return velocityPenalty * (Math.abs(velXCurrent - velXPrev) + Math.abs(velYCurrent- velYPrev));
	}
	
	private LinkedList<Point2D> getNeighbours(Point2D p){
		LinkedList<Point2D> neighbours = new LinkedList<Point2D>();
		
		for(double x = p.getX()-aStarStepsize; x <= aStarStepsize + p.getX(); x += aStarStepsize) {		
			for(double y = p.getY()-aStarStepsize; y <= aStarStepsize + p.getY(); y += aStarStepsize) {
				
				if(Math.abs(y-p.getY()) <= EPS && Math.abs(x-p.getX()) <= EPS)
					continue;
				
				if(x>bounds.getMaxX() || x<bounds.getMinX())
					continue;
				
				if(y>bounds.getMaxY() || y<bounds.getMinY())
					continue;
				
				Point2D n = new Point2D.Double(x, y);
				neighbours.add(n);
			}
		}
		
		return neighbours;
	}
	
	private LinkedList<Point2D> constructPath(Point2D start, Point2D endPoint){
		LinkedList<Point2D> path = new LinkedList<Point2D>();	
		Point2D p = endPoint;
		
		while(p != start) {
			path.addFirst(p);
			p = cameFrom.get(p);
		}
		
		path.addFirst(start);	
		return path;
	}
	
	private LinkedList<Point2D> AStar(Point2D start, List<Point2D> targets){
		fscoreComparator comp = new fscoreComparator();
		PriorityQueue<Point2D> openSet = new PriorityQueue<Point2D>(comp);
		openSet.add(start);
		
		cameFrom = new HashMap<Point2D, Point2D>();
		gscore = new HashMap<Point2D, Double>();
		gscore.put(start, 0.0);
		
		fscore = new HashMap<Point2D, Double>();
		fscore.put(start, null);
		cameFrom.put(start,start);
		
		while (!openSet.isEmpty()) {
			Point2D current = openSet.remove();
			
			if(intersectsRectangle(targetCell, current)) {
				return constructPath(start, cameFrom.get(current));
			}
			
			List<Point2D> neighbours = getNeighbours(current);
			
			for(Point2D neighbour : neighbours){
				double t_gscore = gscore.getOrDefault(current, Double.MAX_VALUE) + d(current, neighbour) + velocityPenality(current, neighbour);
				
				if (t_gscore < gscore.getOrDefault(neighbour, Double.MAX_VALUE)) {
					cameFrom.put(neighbour, current);
					gscore.put(neighbour, t_gscore);
					fscore.put(neighbour, t_gscore + h(neighbour));
					
					if(!openSet.contains(neighbour)) 
						openSet.add(neighbour);
				}				
			}
		}
		return null;
	}
	
	
	private class fscoreComparator implements Comparator<Point2D>{

		@Override
		public int compare(Point2D o1, Point2D o2) {
			return Double.compare(fscore.getOrDefault(o1, Double.MAX_VALUE), fscore.getOrDefault(o2, Double.MAX_VALUE));
		}
		
	}
	
	private class euclidComparator implements Comparator<Point2D>{
		private Point2D target;
		
		public euclidComparator(Point2D target) {
			this.target = target;
		}

		@Override
		public int compare(Point2D o1, Point2D o2) {
			return Double.compare(distanceToTarget(o1), distanceToTarget(o2));
		}

		private double distanceToTarget(Point2D p) {
			return p.distance(target);
		}
	}
	

	public LinkedList<Point2D> planEdge(){
		euclidComparator comp = new euclidComparator(targetCenter);
		possibleOrigins.sort(comp);
		
		for(Point2D ori : possibleOrigins) {
			fullPath = AStar(ori, targetPoints);
			
			if(fullPath == null)
				continue;

			Point2D[] derivate = getSecondDerivate(fullPath);
			LinkedList<Integer> interestingIndices = findCurves(derivate);
			
			if(fullPath.size() > 2 * pathCutoff) {
				interestingIndices.addFirst(pathCutoff - 1);
				interestingIndices.add(fullPath.size()-pathCutoff);
			}
			else {
				interestingIndices.addFirst(0);
				interestingIndices.add(fullPath.size()-1);
			}
			
			LinkedList<Point2D> reducedPath = new LinkedList<Point2D>();
			
			for(int idx: interestingIndices) {
				
				reducedPath.add(fullPath.get(idx));
			}
			
			reduceWayPoints(reducedPath);
			insertIntoGrid(reducedPath);
			
			return reducedPath;

		}
		
		return null;
		
		
	}
	
	
	private void reduceWayPoints(LinkedList<Point2D> fullPath){
		for(int i = 0; i < fullPath.size() -1; i++){
			Point2D currentWayPoint = fullPath.get(i);
			Point2D nextWayPoint = fullPath.get(i+1);
			
			if(currentWayPoint.distance(nextWayPoint) < minInterPointDist){
				fullPath.remove(i+1);			
				i--;
			}
		}
	}
	
	private synchronized void insertIntoGrid(LinkedList<Point2D> path) {
		List<GridObject> gos = new LinkedList<>();
		
		Point2D src = sourceCenter;
		Iterator<Point2D> itr = path.iterator();
		Point2D trg = itr.next();
		
		Envelope env = envelopeOfSegment(src, trg);
		Point2D.Double center = new Point2D.Double(env.centre().x, env.centre().y);
		gos.add(new GridObject(edge, center, env, 1));
		while(itr.hasNext()) {
			src = trg;
			trg = itr.next();
			
			env = envelopeOfSegment(src, trg);
			center = new Point2D.Double(env.centre().x, env.centre().y);
			gos.add(new GridObject(edge, center, env, Double.MAX_VALUE));
		}
		
		grid.insertIntoTree(gos);
	}
	
	private Envelope envelopeOfSegment(Point2D src, Point2D trg) {
		double maxX = (src.getX()>=trg.getX())?src.getX():trg.getX();
		double minX = (src.getX()<trg.getX())?src.getX():trg.getX();
		if(Math.abs(maxX-minX) < edgeSize) {
			maxX += edgeSize/2.0;
			minX -= edgeSize/2.0;
		}
		
		double maxY = (src.getY()>=trg.getY())?src.getY():trg.getY();
		double minY = (src.getY()<trg.getY())?src.getY():trg.getY();
		if(Math.abs(maxY-minY) < edgeSize) {
			maxY += edgeSize/2.0;
			minY -= edgeSize/2.0;
		}
		
		return new Envelope(minX, maxX, minY, maxY);
	}
	
	private Point2D[] getSecondDerivate(LinkedList<Point2D> valueList) {
		Point2D[] derivate = new Point2D[valueList.size()];
		Point2D[] valueArr = valueList.toArray(new Point2D[0]);
		
		for(int i = 1; i < valueArr.length -2 ; i++) {
			Point2D p = new Point2D.Double(valueArr[i+2].getX() - 2 * valueArr[i+1].getX() + valueArr[i].getX(), 
					valueArr[i+2].getY() - 2 * valueArr[i+1].getY() + valueArr[i].getY());
			derivate[i] = p;
		}
		
		return derivate;
		
	}
	
	private LinkedList<Integer> findCurves(Point2D[] derivate){
		
		LinkedList<Integer> foundIdx = new LinkedList<Integer>();
		
		for(int i = 2; i < derivate.length-3; i++) {
			
			if((derivate[i-1].getX() < derivate[i].getX() && derivate[i].getX() > derivate[i+1].getX()) 
					|| (derivate[i-1].getX() > derivate[i].getX() && derivate[i].getX() < derivate[i+1].getX())) {		
				if(Math.abs(derivate[i].getX()) >= minCurveStrength) {
				foundIdx.add(i);				
				continue;
				}
			}
			
			if((derivate[i-1].getY() < derivate[i].getY() && derivate[i].getY() > derivate[i+1].getY()) 
					|| (derivate[i-1].getY() > derivate[i].getY() && derivate[i].getY() < derivate[i+1].getY())) {
				if(Math.abs(derivate[i].getY()) >= minCurveStrength ) {
				foundIdx.add(i);
				}
			}
		}
		
		return foundIdx;
	}
	

}


