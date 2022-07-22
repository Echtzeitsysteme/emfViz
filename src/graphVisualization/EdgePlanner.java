package graphVisualization;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.mxgraph.model.mxGeometry;



public class EdgePlanner {

	private LinkedList<Point> targetPoints;
	private Point centerTarget;
	
	private LinkedList<Point> possibleOrigins;
	
	private HashMap<Point,Point> cameFrom;
	private HashMap<Point, Double> gscore;
	private HashMap<Point, Double> fscore;
	
	private double cellOriginBorderMargin = 0.2;
	private double velocityPenalty =0.0000005;
	private int aStarStepsize = 2;
	private double minInterPointDist = 10;
	private double minCurveStrength =2;

	private boolean log;
	
	private Grid edgeGrid;
	
	private mxGeometry targetGeom;
	private mxGeometry originGeom;
	//private double[][] edgeGrid;
	
	public LinkedList<Point> fullPath;
	
	private int pathCutoff = 3;
	
	private int maxIterations = 2000;
	
	public EdgePlanner(Point nodeOrigin, Point nodeTarget, mxGeometry cellBoundsOrigin, mxGeometry cellBoundsTarget, Grid edgeGrid){//RTree<String, Rectangle> blockTree) {
		
		this.centerTarget = nodeTarget;
		
		this.edgeGrid = edgeGrid;
	
		targetPoints = getCellBorderPoints(nodeTarget, cellBoundsTarget);
		possibleOrigins = getCellBorderPoints(nodeOrigin, cellBoundsOrigin);
		
		this.targetGeom = cellBoundsTarget;
		this.originGeom = cellBoundsOrigin;
		
		
		this.edgeGrid = edgeGrid;
		
		edgeGrid.setGridValues(cellBoundsTarget.getRectangle(), 0);
		edgeGrid.setGridValues(originGeom.getRectangle(), 0);
		
		this.log = log;

	}
	
	
	private LinkedList<Point> getCellBorderPoints(Point nodePosition, mxGeometry cellBounds) {
		
		
		LinkedList<Point> borderPoints = new LinkedList<Point>();

		
		Point topLeft = new Point();
		topLeft.x = (int) cellBounds.getX();
		topLeft.y = (int) cellBounds.getY();
		
		for(int i = (int) (topLeft.x +  cellBounds.getWidth() * cellOriginBorderMargin); i <  topLeft.x + cellBounds.getWidth() * (1-cellOriginBorderMargin); i++) {
			
			Point p1 = new Point();
			p1.x = i;
			p1.y = topLeft.y;
			borderPoints.add(p1);
			
			Point p2 = new Point();
			p2.x = i;
			p2.y = (int) (topLeft.y + cellBounds.getHeight());
			borderPoints.add(p2);
			
		}
		
		for(int i = (int) (topLeft.y + cellBounds.getHeight() * cellOriginBorderMargin); i <  topLeft.y + cellBounds.getHeight() * (1-cellOriginBorderMargin); i++) {
			
			Point p3 = new Point();
			p3.x = topLeft.x;
			p3.y = i;
			borderPoints.add(p3);
			
			Point p4 = new Point();
			p4.x = (int) (topLeft.x + cellBounds.getWidth());
			p4.y = i;
			borderPoints.add(p4);
			
		}
		
		
		return borderPoints;
	}	
	
	private boolean intersectsRectangle(java.awt.Rectangle rectangle, Point p) {
		return rectangle.getMinX() < p.x && p.x < rectangle.getMaxX() && p.y > rectangle.getMinY() && p.y < rectangle.getMaxY();
	}
	
	private double h(Point start) {
		return Math.sqrt(Math.pow(start.x - centerTarget.x , 2) + Math.pow(start.y - centerTarget.y, 2)) + edgeGrid.GetGridValue(Math.max(0,start.x - (int)edgeGrid.visXOffset),Math.max(0,start.y - (int)edgeGrid.visYOffset));
	}
	
	private double d(Point p1, Point p2) {
		return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
	}
	
	private double velocityPenality(Point current, Point next) {
		
		
		if(!cameFrom.containsKey(current))
			return 0;
		
		int velXCurrent = current.x-next.x;
		int velYCurrent = current.y-next.y;
		
		Point prev = cameFrom.get(current);
		
		int velXPrev = prev.x - current.x;
		int velYPrev = prev.y - current.x;
		
		return velocityPenalty* (Math.abs(velXCurrent - velXPrev) + Math.abs(velYCurrent- velYPrev));
	}
	
	private LinkedList<Point> getNeighbours(Point p){
		
		LinkedList<Point> neighbours = new LinkedList<Point>();
		
		
		for(int i = -aStarStepsize; i <= aStarStepsize; i += aStarStepsize) {
		
			for(int j = -aStarStepsize; j<= aStarStepsize; j += aStarStepsize) {
				
				if(j == 0 && i == 0)
					continue;
				
				Point n = new Point();
				
				n.x = Math.min( Math.max(0,p.x + i), edgeGrid.maxXindx);
				n.y = Math.min( Math.max(0,p.y + j), edgeGrid.maxYindx);
				
				neighbours.add(n);
			}
		}
		
		
		return neighbours;
	}
	
	private LinkedList<Point> constructPath(Point start, Point endPoint){

		LinkedList<Point> path = new LinkedList<Point>();
		
		Point p = endPoint;
		
		
		while(p != start) {
			path.addFirst(p);
			p = cameFrom.get(p);
		}
		
		path.addFirst(start);	
		return path;
		
	}
	
	private LinkedList<Point> AStar(Point start, List<Point> targets){
		
		
		fscoreComparator comp = new fscoreComparator();
		PriorityQueue<Point> openSet = new PriorityQueue<Point>(comp);
		openSet.add(start);
		
		cameFrom = new HashMap<Point,Point>();
		
		gscore = new HashMap<Point, Double>();
		gscore.put(start, 0.0);
		
		fscore = new HashMap<Point, Double>();
		fscore.put(start, null);
		
		cameFrom.put(start,start);
		
		int it = 0;
		while (!openSet.isEmpty()) {
			
			
			
			//edgeGrid.setGridValues(targetGeom.getRectangle(), 0);
			//edgeGrid.setGridValues(originGeom.getRectangle(), 0);
			
			Point current = openSet.remove();
			
			if(it++ > maxIterations)
				edgeGrid.setGridValues(targetGeom.getRectangle(), 0);
				edgeGrid.setGridValues(originGeom.getRectangle(), 0);
			
			//if(log)
			//	System.out.println(current.toString());
			
			if(intersectsRectangle(targetGeom.getRectangle(), current)) {
				return constructPath(start, cameFrom.get(current));
			}
			
			List<Point> neighbours = getNeighbours(current);
			
			for(Point neighbour : neighbours){

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
	
	
	private class fscoreComparator implements Comparator<Point>{

		@Override
		public int compare(Point o1, Point o2) {
			return Double.compare(fscore.getOrDefault(o1, Double.MAX_VALUE), fscore.getOrDefault(o2, Double.MAX_VALUE));
		}
		
	}
	
	private class euclidComparator implements Comparator<Point>{
		
		private Point target;
		
		public euclidComparator(Point target) {
			this.target = target;
		}

		@Override
		public int compare(Point o1, Point o2) {
			return Double.compare(distanceToTarget(o1), distanceToTarget(o2));
		}

		private double distanceToTarget(Point p) {
			return Math.pow(p.x - target.x , 2) + Math.pow(p.y - target.y , 2);
		}
	
		
		}
	

	public LinkedList<Point> planEdge(){
		
		euclidComparator comp = new euclidComparator(centerTarget);
		possibleOrigins.sort(comp);
		
		for(Point ori : possibleOrigins) {
			
			
			fullPath = AStar(ori, targetPoints);
			
			if(fullPath == null)
				continue;
			
			edgeGrid.setGridValues(targetGeom.getRectangle(), Double.MAX_VALUE);
			edgeGrid.setGridValues(originGeom.getRectangle(), Double.MAX_VALUE);

			Point[] derivate = getSecondDerivate(fullPath);
			
			LinkedList<Integer> interestingIndices = findCurves(derivate);
			
			if(fullPath.size() > 2 * pathCutoff) {
				interestingIndices.addFirst(pathCutoff - 1);
				interestingIndices.add(fullPath.size()-pathCutoff);
			}
			else {
				interestingIndices.addFirst(0);
				interestingIndices.add(fullPath.size()-1);
			}
			
			LinkedList<Point> reducedPath = new LinkedList<Point>();
			
			for(int idx: interestingIndices) {
				
				reducedPath.add(fullPath.get(idx));
			}
			
			
			reduceWayPoints(reducedPath);
			
			return reducedPath;

		}
		
		edgeGrid.setGridValues(targetGeom.getRectangle(), Double.MAX_VALUE);
		edgeGrid.setGridValues(originGeom.getRectangle(), Double.MAX_VALUE);
		
		return null;
		
		
	}
	
	
	private void reduceWayPoints(LinkedList<Point> fullPath){
		
		for(int i = 0; i < fullPath.size() -1; i++){
			
			Point currentWayPoint = fullPath.get(i);
			Point nextWayPoint = fullPath.get(i+1);
			
			if(currentWayPoint.distance(nextWayPoint.x, nextWayPoint.y) < minInterPointDist){
				fullPath.remove(i+1);			
				i--;
			}
			
		}
		
	}
	
	
	private Point[] getSecondDerivate(LinkedList<Point> valueList) {

		Point[] derivate = new Point[valueList.size()];
		Point[] valueArr = valueList.toArray(new Point[0]);
		
		for(int i = 1; i < valueArr.length -2 ; i++) {
			
			Point p = new Point();
			
			p.x = valueArr[i+2].x - 2 * valueArr[i+1].x + valueArr[i].x;
			p.y = valueArr[i+2].y - 2 * valueArr[i+1].y + valueArr[i].y;
			
			derivate[i] = p;
		}
		
		return derivate;
		
	}
	
	private LinkedList<Integer> findCurves(Point[] derivate){
		
		LinkedList<Integer> foundIdx = new LinkedList<Integer>();
		
		for(int i = 2; i < derivate.length-3; i++) {
			
			if((derivate[i-1].x < derivate[i].x && derivate[i].x > derivate[i+1].x) || (derivate[i-1].x > derivate[i].x && derivate[i].x < derivate[i+1].x)) {		
				if(Math.abs(derivate[i].x) >= minCurveStrength) {
				foundIdx.add(i);				
				continue;
				}
			}
			
			if((derivate[i-1].y < derivate[i].y && derivate[i].y > derivate[i+1].y) || (derivate[i-1].y > derivate[i].y && derivate[i].y < derivate[i+1].y)) {
				if(Math.abs(derivate[i].y) >= minCurveStrength ) {
				foundIdx.add(i);
				}
			}
		}
		
		return foundIdx;
	}
	

}


