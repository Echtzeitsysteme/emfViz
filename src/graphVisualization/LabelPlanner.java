package graphVisualization;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;

public class LabelPlanner {
	
	private Grid edgeGrid;
	private double distanceToEdge = 10;
	
	private mxPoint finalPosition;
	private List<mxPoint> edgePath;
	
	private Rectangle currentLabelBounds;
	
	private String label;
	
	public LabelPlanner(Grid grid) {
		this.edgeGrid = grid;
	}
	
	
	public mxPoint planLabel(mxRectangle labelBounds, List<mxPoint> edgePath, String label) {
		
		this.edgePath = edgePath;
		currentLabelBounds = (Rectangle) labelBounds.getRectangle().clone();
		//System.out.println(currentLabelBounds.height);
		currentLabelBounds.grow(-10, -10);
		finalPosition = edgePath.get(edgePath.size()-2);
		
		double xPlace = edgePath.get(edgePath.size()-1).getX();
		double yPlace = edgePath.get(edgePath.size()-1).getY();
			
		double xStep = (edgePath.get(edgePath.size()-2).getX() - xPlace);
		double yStep = (edgePath.get(edgePath.size()-2).getY() - yPlace);
		
		double xOffset = xStep < 0 ? distanceToEdge : -distanceToEdge;
		double yOffset = yStep < 0 ? distanceToEdge : -distanceToEdge;
		
		finalPosition = edgeGrid.placeInFreeGridPosition(currentLabelBounds, new Point2D.Double(xPlace + xOffset,yPlace + yOffset), currentLabelBounds.width, currentLabelBounds.height);
			
		return finalPosition;
	}
	
	
	

}
