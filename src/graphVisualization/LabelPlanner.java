package graphVisualization;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;

import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class LabelPlanner {
	
	private Grid grid;
	private double distanceToEdge = 1;
	
	private mxPoint finalPosition;
	private Rectangle currentLabelBounds;
	
	
	public LabelPlanner(Grid edgeGrid) {
		this.grid = edgeGrid;
	}
	
	
	public mxPoint planLabel(mxRectangle labelBounds, List<mxPoint> edgePath, String label) {
		
		currentLabelBounds = (Rectangle) labelBounds.getRectangle();

		// reduced size of label bounds (mxGraph sets them too big)
		currentLabelBounds.grow(-10, -10);
		
		// use end of edge path as initial estimation of label position
		finalPosition = edgePath.get(edgePath.size()-2);
		
		double xPlace = edgePath.get(edgePath.size()-1).getX();
		double yPlace = edgePath.get(edgePath.size()-1).getY();
			
		double xStep = (edgePath.get(edgePath.size()-2).getX() - xPlace);
		double yStep = (edgePath.get(edgePath.size()-2).getY() - yPlace);
		
		// Offset -> Label is placed right below edge in relation to its direction
		double xOffset = xStep < 0 ? distanceToEdge : -distanceToEdge;
		double yOffset = yStep < 0 ? distanceToEdge : -distanceToEdge;
		
		//search free point in grid
		finalPosition = grid.getFreeGridPosition(new Point2D.Double(xPlace + xOffset,yPlace + yOffset), currentLabelBounds);

		
		return finalPosition;
	}
	
	
	

}
