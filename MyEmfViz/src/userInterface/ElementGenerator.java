package userInterface;

import java.awt.event.*;
import java.awt.geom.Point2D;

import com.mxgraph.view.mxGraph;

import graphVisualization.*;

public class ElementGenerator {
	private int defaultNodeWidth = 80;
	private int defaultNodeHeight = 40;
	private mxGraph graph;
	private Point2D.Double defaultNodePosition;
	
	public ElementGenerator(mxGraph graph, Point2D.Double defaultPos) {
		this.graph = graph;
		this.defaultNodePosition = defaultPos;
	}
	
	private void generateNode() {
		double centerX = graph.getGraphBounds().getCenterX();
		double centerY = graph.getGraphBounds().getCenterY();
		
		
		graph.insertVertex(graph.getDefaultParent(),
				null, "Test", defaultNodePosition.x - centerX, defaultNodePosition.y - centerY, 
				defaultNodeWidth, defaultNodeHeight, "defaultVertex");
	}
}



