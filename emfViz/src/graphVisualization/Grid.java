package graphVisualization;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import com.mxgraph.util.mxPoint;

public class Grid {
	
	private double[][] grid;
	
	public int maxXindx;
	public int maxYindx;
	
	private double visWidth;
	private double visHeight;
	
	private org.eclipse.swt.graphics.Rectangle shellBounds;
	
	double visXOffset;
	double visYOffset;
	private double visXCap;
	private double visYCap;
	
	//controls distance to GUI Border
	double margin =0.1;
	
	private double horizontalGridDist;
	private double verticalGridDist;

	public Point2D.Double defaultNodePosition;
	
	//padding around each Node
	private int blockMarginX;
	private int blockMarginY;
	
	private int maxDistanceToOrigin = 50;
	
	public Grid(org.eclipse.swt.graphics.Rectangle shellBounds, int sizeX, int sizeY, double blockMargin) {
		
		this.shellBounds = shellBounds;
		
		grid = new double[sizeX][sizeY];
		maxXindx = sizeX -1;
		maxYindx = sizeY -1;
		
		calculateGraphBounds();
		
		//span of grid
		horizontalGridDist =  Math.max((double)(1-margin )* shellBounds.width / (double)sizeX , 1);
		verticalGridDist = Math.max((double)(1-margin )* shellBounds.height / (double)sizeY, 1);
		
		this.blockMarginX = (int) Math.ceil(blockMargin / horizontalGridDist);
		this.blockMarginY = (int) Math.ceil(blockMargin / verticalGridDist);
		

	}
	
	private void calculateGraphBounds() {
		
		int visX =  0;//shellBounds.x;
		int visY = 0;//shellBounds.y;
		
		
		visWidth = (1-margin)*shellBounds.width;
		visHeight = (1-margin)*shellBounds.height;
		
		//minimum coordinates
		visXOffset = visX + margin * 0.5 * shellBounds.width;
		visYOffset = visY + margin * 0.5 * shellBounds.height;
		
		//maximum coordinates
		visXCap = visX + (1- 3*margin )* shellBounds.width;
		visYCap = visY + (1- 3*margin )* shellBounds.height;
		
		//default placement for unconnected nodes
		defaultNodePosition = new Point2D.Double();
		defaultNodePosition.x = visX + 0.5 * visWidth;
		defaultNodePosition.y =  visY + 0.5 * visHeight;
		
	}
	
	mxPoint getAbsolutePosition(int gridX, int gridY) {
		return new mxPoint(Math.min(gridX * horizontalGridDist + visXOffset, visXCap), Math.min(gridY * verticalGridDist + visYOffset, visYCap));
	}
	
	// set all grid values within area
	public void setGridValues(java.awt.Rectangle area, double val) {
		

		Point pos = primeGridSearch(new Point2D.Double(area.getMinX(), area.getMinY()));
		Point maxPos = primeGridSearch(new Point2D.Double(area.getMaxX(), area.getMaxY()));

		
		for(int i = Math.max(0,(int) pos.x - blockMarginX); i <= Math.min(maxPos.x + blockMarginX, maxXindx); i ++) {
			
			for(int j = Math.max(0,(int) pos.y -blockMarginY); j <= Math.min(maxPos.y + blockMarginY, maxYindx); j ++) {
				
				grid[i][j] = val;
			}
		}
		
	}
	
	
	// returns sum of all grid values within area at point x and y
	public double getCostForArea(java.awt.Rectangle area, int gridX, int gridY, boolean terminateEarly) {
		
		
		int xRange = (int) Math.ceil( area.width/(double)horizontalGridDist);
		int yRange = (int) Math.ceil( area.height/(double)verticalGridDist);
		
		double cost = 0.0;
	
		for(int i = Math.max(0,gridX); i <= Math.min(gridX + xRange, maxXindx); i ++) {
		
			for(int j = Math.max(0, gridY); j <= Math.min(gridY+yRange, maxYindx); j ++) {
			
				cost += grid[i][j];
				
				// early termination for placement of labels and nodes
				if(terminateEarly && cost != 0)
					return 1;
				
			}
		}
		
		return cost;
	}
	
	
	//estimate corresponding point in grid to absolute position
	Point primeGridSearch(Point2D.Double p) {
			int yIdx = (int) Math.min(Math.max(0,(Math.round((p.y - visYOffset)/(double)verticalGridDist))), maxYindx);
			int xIdx = (int) Math.min(Math.max(0,(Math.round((p.x - visXOffset)/(double)horizontalGridDist))), maxXindx);
			
			Point gridPoint = new Point();
			gridPoint.x = xIdx;
			gridPoint.y = yIdx;
			
			return gridPoint;
	}
	
	
	public mxPoint getFreeGridPosition(Point2D.Double initialPosition, java.awt.Rectangle geometry){
		
		Point origin = primeGridSearch(initialPosition);
		java.awt.Rectangle includedArea =  (Rectangle) geometry.clone();
				
	
		for(int d = 0 ; d < maxDistanceToOrigin; d++) {
			
			for(int y = Math.max(origin.y - d, 0);  y <= Math.min(origin.y +  d, maxYindx); y += Math.max(1,2*d)) {
				
				for(int x = Math.max(origin.x - d, 0);  x <= Math.min(origin.x +  d, maxXindx); x += Math.max(1,2*d)) {
			
					if(getCostForArea(includedArea, x, y, true) == 0) {
						
						mxPoint absolutePosition =  getAbsolutePosition(x, y);
						includedArea.setLocation((int)absolutePosition.getX(),(int) absolutePosition.getY());	
						return absolutePosition;
					}
					
				}
				
			}
			
		}
		
		//No free grid point found, visualize at initial estimate
		setGridValues(geometry, Double.MAX_VALUE);	
		return getAbsolutePosition(origin.x, origin.y);
		
	}
	
	public double GetGridValue(int x, int y) {
		return grid[x][y];
	}
	
		
	
}

