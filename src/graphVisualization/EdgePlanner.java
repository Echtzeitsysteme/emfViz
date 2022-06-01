package graphVisualization;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import com.mxgraph.model.mxGeometry;



public class EdgePlanner {

	private ArrayList<Point> targetPoints;
	private Point centerTarget;
	
	private ArrayList<Point> possibleOrigins;
	
	private HashMap<Point,Point> cameFrom;
	private HashMap<Point, Double> gscore;
	private HashMap<Point, Double> fscore;
	
	private double cellOriginBorderMargin = 0.2;
	private double velocityPenalty =0.0000005;
	private int aStarStepsize = 2;
	private double minInterPointDist = 10;
	private double minCurveStrength =2;
	private int maxIterations = 1000;
	
	private Grid edgeGrid;
	
	//private RTree<String, Rectangle> blockTree;
	private ArrayList<mxGeometry> blockedAreas;
	
	private mxGeometry targetGeom;
	private mxGeometry originGeom;
	//private double[][] edgeGrid;
	
	public ArrayList<Point> fullPath;
	
	private int pathCutoff = 3;
	
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

	}
	
	
	private ArrayList<Point> getCellBorderPoints(Point nodePosition, mxGeometry cellBounds) {
		
		
		ArrayList<Point> borderPoints = new ArrayList<Point>();

		
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
	
	private ArrayList<Point> getNeighbours(Point p){
		
		ArrayList<Point> neighbours = new ArrayList<Point>();
		
		
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
	
	private ArrayList<Point> constructPath(Point start, Point endPoint){

		ArrayList<Point> path = new ArrayList<Point>();
		
		Point p = endPoint;
		
		
		while(p != start) {
			path.add(0, p);
			p = cameFrom.get(p);
		}
		
		path.add(0, start);	
		return path;
		
	}
	
	private ArrayList<Point> AStar(Point start, List<Point> targets){
		
		
		fscoreComparator comp = new fscoreComparator();
		PriorityQueue<Point> openSet = new PriorityQueue<Point>(comp);
		openSet.add(start);
		
		cameFrom = new HashMap<Point,Point>();
		
		gscore = new HashMap<Point, Double>();
		gscore.put(start, 0.0);
		
		fscore = new HashMap<Point, Double>();
		fscore.put(start, null);
		
		cameFrom.put(start,start);
		
		while (!openSet.isEmpty()) {
			
			
			Point current = openSet.remove();
			
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
	

	public ArrayList<Point> planEdge(){
		
		euclidComparator comp = new euclidComparator(centerTarget);
		possibleOrigins.sort(comp);
		
		for(Point ori : possibleOrigins) {
			
			
			fullPath = AStar(ori, targetPoints);
			
			if(fullPath == null)
				continue;
			
			edgeGrid.setGridValues(targetGeom.getRectangle(), Double.MAX_VALUE);
			edgeGrid.setGridValues(originGeom.getRectangle(), Double.MAX_VALUE);

			Point[] derivate = getSecondDerivate(fullPath);
			
			ArrayList<Integer> interestingIndices = findCurves(derivate);
			
			if(fullPath.size() > 2 * pathCutoff) {
				interestingIndices.add(0, pathCutoff - 1);
				interestingIndices.add(fullPath.size()-pathCutoff);
			}
			else {
				interestingIndices.add(0, 0);
				interestingIndices.add(fullPath.size()-1);
			}

			
			ArrayList<Point> reducedPath = new ArrayList<Point>();
			
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
	
	
	private void reduceWayPoints(ArrayList<Point> fullPath){
		
		for(int i = 0; i < fullPath.size() -1; i++){
			
			Point currentWayPoint = fullPath.get(i);
			Point nextWayPoint = fullPath.get(i+1);
			
			if(currentWayPoint.distance(nextWayPoint.x, nextWayPoint.y) < minInterPointDist){
				fullPath.remove(i+1);			
				i--;
			}
			
		}
		
	}
	
	
	private Point[] getSecondDerivate(ArrayList<Point> valueList) {

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
	
	private ArrayList<Integer> findCurves(Point[] derivate){
		
		ArrayList<Integer> foundIdx = new ArrayList<Integer>();
		
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


