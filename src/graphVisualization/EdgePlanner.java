package graphVisualization;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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
	
//	private HashMap<Point2D, Point2D> cameFrom;
//	private HashMap<Point2D, Double> gscore;
//	private HashMap<Point2D, Double> fscore;

	private double velocityPenalty =0.0000005;
	private double aStarStepsize = 5.0;
	private double minInterPointDist = 10.0;
	private double minCurveStrength =2.0;
	private double edgeSize = 10.0;
	
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
	}
	
	private double h(Point2D start) {
		EuclidComparator comp = new EuclidComparator(start);
		targetPoints.sort(comp);
		
		return start.distance(targetPoints.getFirst()) + grid.getCostForArea(
						new Envelope(	start.getX()-aStarStepsize/2, 
										start.getX()+aStarStepsize/2, 
										start.getY()-aStarStepsize/2, 
										start.getY()+aStarStepsize/2));
	}
	
	private double d(Point2D p1, Point2D p2) {
		return p1.distance(p2);
	}
	
//	private double velocityPenality(Point2D current, Point2D next) {
//		if(!cameFrom.containsKey(current))
//			return 0;
//		
//		double velXCurrent = current.getX()-next.getX();
//		double velYCurrent = current.getY()-next.getY();
//		
//		Point2D prev = cameFrom.get(current);
//		
//		double velXPrev = prev.getX() - current.getX();
//		double velYPrev = prev.getY() - current.getX();
//		
//		return velocityPenalty * (Math.abs(velXCurrent - velXPrev) + Math.abs(velYCurrent- velYPrev));
//	}
	
	private LinkedList<Point2D> getNeighbours(Point2D p, Point2D direction){
		LinkedList<Point2D> neighbours = new LinkedList<Point2D>();
		
		for(double x = p.getX()-aStarStepsize; x <= aStarStepsize + p.getX(); x += aStarStepsize) {		
			for(double y = p.getY()-aStarStepsize; y <= aStarStepsize + p.getY(); y += aStarStepsize) {
				
				if(Math.abs(y-p.getY()) <= EPS && Math.abs(x-p.getX()) <= EPS)
					continue;
				
				if(x>bounds.getMaxX() || x<bounds.getMinX())
					continue;
				
				if(y>bounds.getMaxY() || y<bounds.getMinY())
					continue;
				
				double cost = grid.getCostForArea(
						new Envelope(	x-aStarStepsize/2, 
										x+aStarStepsize/2, 
										y-aStarStepsize/2, 
										y+aStarStepsize/2));
				
				if(Double.MAX_VALUE <= cost)
					continue;
				
				Point2D n = new Point2D.Double(x, y);
				neighbours.add(n);
			}
		}
		
//		Point2D p1 = new Point2D.Double(p.getX()+direction.getX()*(aStarStepsize/2), p.getY()+direction.getY()*(aStarStepsize/2));
//		for(double angle = 0; angle <= Math.PI; angle += Math.PI/2.0) {
//			double xFactor = Math.sin(angle)*aStarStepsize/2;
//			double yFactor = Math.cos(angle)*aStarStepsize/2;
//			Point2D p3 = new Point2D.Double(p1.getX()+xFactor, p1.getY()+yFactor);
//			
//			for(double t = 0; t <= 1.0; t += 1.0) {
//				Point2D sample = bezierCurve(p, p1, p3, t);
//				
//				double cost = grid.getCostForArea(
//						new Envelope(	
//								sample.getX()-aStarStepsize/2, 
//								sample.getX()+aStarStepsize/2, 
//								sample.getY()-aStarStepsize/2, 
//								sample.getY()+aStarStepsize/2));
//				
//				if(Double.MAX_VALUE <= cost) {
//					continue;
//				} else {
//					neighbours.add(sample);
//				}
//			}
//		}
		
//		
		return neighbours;
	}
	
	private Point2D bezierCurve(Point2D p0, Point2D p1, Point2D p2, double t) {
		// B(t) = (1-t)^2*P0 + 2t(1-t)*P1 + t^2*P2, t in [0, 1]
		Point2D p0t = new Point2D.Double(Math.pow(1-t, 2) * p0.getX(), Math.pow(1-t, 2) * p0.getY());
		Point2D p1t = new Point2D.Double(2*t*(1-t)*p1.getX(), 2*t*(1-t)*p1.getY());
		Point2D p2t = new Point2D.Double(Math.pow(t, 2)*p2.getX(), Math.pow(t, 2)*p2.getY());
		return new Point2D.Double(p0t.getX() + p1t.getX() + p2t.getX(), p0t.getY() + p1t.getY() + p2t.getY());
	}
	
	private LinkedList<Point2D> constructPath(Point2D start, Point2D endPoint, Map<Point2D, Point2D> cameFrom){
		LinkedList<Point2D> path = new LinkedList<Point2D>();	
		Point2D p = endPoint;
		
		while(p != null) {
			path.addFirst(p);
			p = cameFrom.get(p);
		}
		
		path.addFirst(start);	
		return path;
	}
	
	private LinkedList<Point2D> AStar(Point2D start, List<Point2D> targets){
		Map<Point2D, Double> gScores = new HashMap<>();
		Map<Point2D, Double> fScores = new HashMap<>();
		Map<Point2D, Point2D> cameFrom = new HashMap<>();
		Set<Point2D> openSet = new HashSet<>();
		
		double trgRadius = 20.0;
		FScoreComparator comp = new FScoreComparator(fScores);
		PriorityQueue<Point2D> openQueue = new PriorityQueue<Point2D>(comp);
		
		openQueue.add(start);
		openSet.add(start);
		gScores.put(start, 0.0);
		fScores.put(start, h(start));
		
		while (!openQueue.isEmpty()) {
			Point2D current = openQueue.remove();
			openSet.remove(current);
			double gscore = gScores.getOrDefault(current, Double.MAX_VALUE);
			
			EuclidComparator eComp = new EuclidComparator(current);
			targetPoints.sort(eComp);
			Point2D trg = targetPoints.getFirst();
			if(current.distance(trg)<=trgRadius) {;
				//cameFrom.put(trg, current);
				return constructPath(start, current, cameFrom);
			}
			
			List<Point2D> neighbours = new LinkedList<>();
			Point2D direction = null;
			Point2D previous = cameFrom.get(current);
			if(previous == null) {
				double d = current.distance(trg);
				direction = new Point2D.Double((trg.getX()-current.getX())/d, (trg.getY()-current.getY())/d);
			} else {
				double d = previous.distance(current);
				direction = new Point2D.Double((current.getX()-previous.getX())/d, (current.getY()-previous.getY())/d);
			}
			
			neighbours = getNeighbours(current, direction);
//			for(Point2D p : getNeighbours(current, direction)) {
//				double cost = grid.getCostForArea(
//						new Envelope(	p.getX()-aStarStepsize/2, 
//										p.getX()+aStarStepsize/2, 
//										p.getY()-aStarStepsize/2, 
//										p.getY()+aStarStepsize/2));
//				
//				if(cost >= Double.MAX_VALUE) {
//					System.out.println("Candidate point collision!");
//					System.out.println("->remove!");
//				} else {
//					neighbours.add(p);
//				}
//					
//			}
			
			for(Point2D neighbour : neighbours){
				double t_gscore = gscore + d(current, neighbour); // + velocityPenality(current, neighbour);
				double nGscore = gScores.getOrDefault(neighbour, Double.MAX_VALUE); // + velocityPenality(current, neighbour);
				if (t_gscore < nGscore) {
					cameFrom.put(neighbour, current);
					gScores.put(neighbour, t_gscore);
					fScores.put(neighbour, t_gscore + h(neighbour));
					if(!openSet.contains(neighbour)) {
						openSet.add(neighbour);
						openQueue.add(neighbour);
					}
				}
			}
		}
		return null;
	}

	public LinkedList<Point2D> planEdge(){
		EuclidComparator comp = new EuclidComparator(targetCenter);
		possibleOrigins.sort(comp);
		
		for(Point2D ori : possibleOrigins) {
			fullPath = AStar(ori, targetPoints);
			
			if(fullPath == null)
				continue;

//			Point2D[] derivate = getSecondDerivate(fullPath);
//			LinkedList<Integer> interestingIndices = findCurves(derivate);
//			
//			if(fullPath.size() > 2 * pathCutoff) {
//				interestingIndices.addFirst(pathCutoff - 1);
//				interestingIndices.add(fullPath.size()-pathCutoff);
//			}
//			else {
//				interestingIndices.addFirst(0);
//				interestingIndices.add(fullPath.size()-1);
//			}
//			
//			LinkedList<Point2D> reducedPath = new LinkedList<Point2D>();
//			
//			for(int idx: interestingIndices) {
//				
//				reducedPath.add(fullPath.get(idx));
//			}
			
			reduceWayPoints(fullPath);
			
			
			for(Point2D p : fullPath) {
				double cost = grid.getCostForArea(
						new Envelope(	p.getX()-aStarStepsize/2, 
										p.getX()+aStarStepsize/2, 
										p.getY()-aStarStepsize/2, 
										p.getY()+aStarStepsize/2));
				
				if(cost >= Double.MAX_VALUE) {
					System.out.println("Path point collision!");
					System.out.println("->No path found!");
					return null;
				}
					
			}
			
			insertIntoGrid(fullPath);
			
			return fullPath;

		}
		
		System.out.println("No path found!");
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
		gos.add(new GridObject(edge, center, env, 100));
		while(itr.hasNext()) {
			src = trg;
			trg = itr.next();
			
			env = envelopeOfSegment(src, trg);
			center = new Point2D.Double(env.centre().x, env.centre().y);
			gos.add(new GridObject(edge, center, env, 100));
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
	
	class FScoreComparator implements Comparator<Point2D>{
		final Map<Point2D, Double> fScores;
		
		public FScoreComparator(final Map<Point2D, Double> fScores) {
			this.fScores = fScores;
		}
		@Override
		public int compare(Point2D o1, Point2D o2) {
			return Double.compare(fScores.getOrDefault(o1, Double.MAX_VALUE), fScores.getOrDefault(o2, Double.MAX_VALUE));
		}
		
	}
	
	class EuclidComparator implements Comparator<Point2D>{
		private Point2D target;
		
		public EuclidComparator(Point2D target) {
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
}


