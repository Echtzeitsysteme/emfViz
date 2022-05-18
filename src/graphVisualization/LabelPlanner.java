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
	
	private Grid entityGrid;
	private double distanceToEdge = 5;
	private double stepsize = 10;
	
	private mxPoint finalPosition;
	private List<mxPoint> edgePath;
	
	private Rectangle currentLabelBounds;
	
	public LabelPlanner(Grid entityGrid) {
		this.entityGrid = entityGrid;
		this.edgePath = null;
	}
	
	
	public mxPoint planLabel(mxRectangle labelBounds, List<mxPoint> edgePath) {
		
		this.edgePath = edgePath;
		currentLabelBounds = labelBounds.getRectangle();
		finalPosition = edgePath.get(edgePath.size()-1);
		
		for(int i = edgePath.size()-1; i >= 1; i--) {
		
			
			mxPoint currentPoint = edgePath.get(i);
			mxPoint nextPoint = edgePath.get(i-1);
			
			double xPlace = currentPoint.getX();
			double yPlace = currentPoint.getY();
			
			double xStep = (nextPoint.getX() - xPlace)/stepsize;
			double yStep = (nextPoint.getY() - yPlace)/stepsize;
			
			double angle = Math.atan2(yStep,xStep) + Math.PI/2;
			
			xPlace += xStep;
			yPlace += yStep;

			for(int step = 1 ; step < stepsize; step++) {
				
				if(goodPlace(xPlace,yPlace , angle))
					return finalPosition;
				

				xPlace += xStep;
				yPlace += yStep;
			}
			
			
		}
		
		System.out.println("Not good Place");
		return finalPosition;
	}
	
	
	public boolean goodPlace(double xPlace, double yPlace, double angle) {
		
		double xPotential = xPlace + distanceToEdge * Math.cos(angle);
		double yPotential = yPlace + distanceToEdge * Math.sin(angle);
		
		Point gridP = entityGrid.primeGridSearch(new Point2D.Double(xPotential,yPotential));
				
		if(entityGrid.getCostForArea(currentLabelBounds, gridP.x, gridP.y, true) < 1) {
			System.out.println("Good Place");
			finalPosition = new mxPoint(xPotential, yPotential);
			return true;
		}
		
		return false;
	}
	

}
