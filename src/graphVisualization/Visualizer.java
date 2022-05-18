package graphVisualization;

import java.awt.Frame;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
import org.eclipse.emf.ecore.EPackage;
import org.emoflon.smartemf.runtime.SmartPackage;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.shape.mxDoubleRectangleShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;



import HospitalExample.HospitalExamplePackage;
import HospitalExample.Hospital;

public class Visualizer {
	
	private Shell shell;
	private Composite composite;
	private Frame frame;
	
	private mxGraph graph;
	private mxGraphModel graphModel;
	private mxGraphComponent graphComponent;
	private DataLoader dataLoader;
	
	
	private int defaultNodeWidth = 80;
	private int defaultNodeHeight = 40;
	private Point2D.Double defaultNodePosition;
	
	private mxFastOrganicLayout preLayout;
	private Grid nodeGrid;
	private Grid edgeGrid;
	
	private double graphCenterX ;
	private double graphCenterY;
	
	private double xStretch ;
	private double yStretch;
	private double margin = 0.1;
	private int minNodeDistanceNodes = 30;
	private int minNodeDistanceEdges = 10;
	
	private ArrayList<mxGeometry> blockedAreas;
	
	
	
	public Visualizer(Shell shell, DataLoader dataLoader) {
		
		this.dataLoader = dataLoader;
		dataLoader.loadData();
		
		this.shell = shell;
		composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		composite.setVisible(true);
		frame = SWT_AWT.new_Frame(composite);
		
		graph = new mxGraph();
		graphModel = ((mxGraphModel)graph.getModel());
		
		Rectangle shellBounds = shell.getBounds();
		
		//System.out.println("Monitor bounds:" + monitorBounds.toString());
		
		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.4 - defaultNodeWidth *0.5, ((double) shellBounds.height) * 0.4- defaultNodeHeight *0.5);
		//defaultNodePosition = new Point2D.Double(shellBounds.width, shellBounds.height);
		System.out.println("Shell bounds:" + shell.getBounds().toString());
		System.out.println("Default Position:" + defaultNodePosition.toString());
		
		addStyles();
		insertDataIntoGraph();
		setUpLayout();
		runLayout();
		
		graphComponent = new mxGraphComponent(graph);
		frame.add(graphComponent);
		
	}
	
	private void addStyles() {
		
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> cellStyle = new Hashtable<String, Object>();
		cellStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		cellStyle.put(mxConstants.STYLE_OPACITY, 50);
		cellStyle.put(mxConstants.STYLE_FONTCOLOR, "#00000");
		cellStyle.put(mxConstants.STYLE_FONTSIZE, "13");
		cellStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		cellStyle.put(mxConstants.ALIGN_CENTER, "1");
		cellStyle.put(mxConstants.STYLE_OVERFLOW, "hidden");
		stylesheet.putCellStyle("defaultNode", cellStyle);
		
		
		Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#00000");
		edgeStyle.put(mxConstants.STYLE_FONTSIZE, "10");
		edgeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		edgeStyle.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyle.put(mxConstants.STYLE_OVERFLOW, "fill");
		stylesheet.putCellStyle("defaultEdges", edgeStyle);
		
		//stylesheet.setDefaultVertexStyle(cellStyle);
		//stylesheet.setDefaultEdgeStyle(edgeStyle);
	}
	
	private void insertDataIntoGraph() {
		
		double centerX = graph.getGraphBounds().getCenterX();
		double centerY = graph.getGraphBounds().getCenterY();
	
		
		for(Node n: dataLoader.nodes) {
			graph.insertVertex(graph.getDefaultParent(),n.id, n.name, defaultNodePosition.x - centerX, defaultNodePosition.y - centerY, defaultNodeWidth, defaultNodeHeight, n.styleCategory);
		}
		
		mxGraphModel graphModel = (mxGraphModel) graph.getModel();
		
		for(ArrayList<Edge> outgoingEdges : dataLoader.edges.values()) {
			
			
			for(Edge e : outgoingEdges) {
				graph.insertEdge(graph.getDefaultParent(), e.id, e.label, graphModel.getCell(e.sourceNID), graphModel.getCell(e.targetNID),e.styleCategory);
			}
			
		}
		
		centerX = graph.getGraphBounds().getCenterX();
		centerY = graph.getGraphBounds().getCenterY();
		
		System.out.println("Center X: " + String.valueOf(centerX));
		System.out.println("Center Y: " + String.valueOf(centerY));
	}
	
	private void setUpLayout() {
		
		preLayout = new mxFastOrganicLayout(graph);
		preLayout.setForceConstant(150);
		preLayout.setMinDistanceLimit(8);
		preLayout.setUseInputOrigin(false);
		preLayout.setDisableEdgeStyle(false);
		
		org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		
		System.out.println("Shellbounds: " + shellBounds.toString());
		
		double hWRatio = (double) shellBounds.height / (double)shellBounds.width;
		
		int baseSize = Math.min(dataLoader.nodes.size() * dataLoader.nodes.size(), shellBounds.height * shellBounds.width);
		
		System.out.println("baseSize: " + String.valueOf(baseSize));
		
		int sizeX = (int) Math.ceil(Math.sqrt(baseSize));
		int sizeY = (int) Math.ceil(Math.sqrt(baseSize));
		
		//System.out.println("SizeX: " + String.valueOf(sizeX));
		//System.out.println("SizeY: " + String.valueOf(sizeY));
		
		nodeGrid = new Grid(shellBounds, sizeX, sizeY,  minNodeDistanceNodes);
		edgeGrid = new Grid(shellBounds,(int)Math.floor((1-margin ) *shellBounds.width), (int) Math.floor((1-margin ) *shellBounds.height), minNodeDistanceEdges);
		
		blockedAreas = new ArrayList<mxGeometry>();
		
	}
	
	private void updateGraphShellTransformation() {
	
		double graphWidth = graph.getGraphBounds().getWidth();
		double graphHeight = graph.getGraphBounds().getHeight();
		
		graphCenterX = graph.getGraphBounds().getCenterX();
		graphCenterY = graph.getGraphBounds().getCenterY();
		
		xStretch = (shell.getMonitor().getClientArea().width *(1-nodeGrid.margin))/graphWidth;
		yStretch = (shell.getMonitor().getClientArea().height*(1-nodeGrid.margin))/graphHeight;
		
		
	}
	
	private void runLayout() {

		preLayout.execute(graph.getDefaultParent());
		
		blockedAreas.clear();
		placeNodes();
		placeEdges();
		placeLabels();
		
	}
	
	private void transformGraphPositionToVisBounds(Point2D.Double position) {
		
		
		position.x = Math.max((position.x - graphCenterX) * xStretch + defaultNodePosition.x,0);
		position.y = Math.max((position.y - graphCenterY) * yStretch + defaultNodePosition.y,0);
		
	}

	
	
	private void placeNodes() {
		
		updateGraphShellTransformation();
		
		Map<String,Object> cells = graphModel.getCells();	
		for(Object cell : cells.values()) {
			
			mxCell c = (mxCell) cell;
			mxGeometry geom = c.getGeometry();
			
			if(geom == null || c.isEdge())
				continue;
			
			Point2D.Double position = new Point2D.Double( geom.getX(), geom.getY() );
			
			transformGraphPositionToVisBounds(position);
			
			mxPoint nearestGridPoint = nodeGrid.getFreeGridPosition(position, c.getGeometry().getRectangle());
			
			if (nearestGridPoint == null) {
				System.out.println("Node grid position not found");
				nearestGridPoint = new mxPoint(position.x, position.y);						
			}
					
			//System.out.println("Final pos: " + nearestGridPoint.toString());
			
			
			System.out.println("Old geom: " + c.getGeometry().toString() + " With rectangle: "+ c.getGeometry().getRectangle().toString());
			
			Object[] cellsToMove = {cell};				
			graph.moveCells(cellsToMove,  nearestGridPoint.getX() - geom.getX(), nearestGridPoint.getY() - geom.getY());
			
			System.out.println("New geom: " + c.getGeometry().toString() + " With rectangle: " + c.getGeometry().getRectangle().toString());
			blockedAreas.add(c.getGeometry());
			
		}
		
		for(mxGeometry g : blockedAreas) {
			System.out.println("Block Area:");
			edgeGrid.setGridValues(g.getRectangle(), Double.MAX_VALUE);
		}
		
	}
	
	private void placeEdges() {
		
		HashMap<String, ArrayList<mxPoint>> plottedEdges = new HashMap<String, ArrayList<mxPoint>>();
		Map<String,Object> cells = graphModel.getCells();	
		
		for(Object cell : cells.values()) {
			
			mxCell node = (mxCell) cell;
			
			if(node.isEdge())
				continue;
			
			
			System.out.println("Node: " + node.getId());
			
			for(int i = 0; i < node.getEdgeCount(); i++) {
				
				mxICell edgeGraph = node.getEdgeAt(i);
				
				mxICell source = edgeGraph.getTerminal(true);
				mxICell terminal = edgeGraph.getTerminal(false);
				
				mxGeometry geometry = edgeGraph.getGeometry();
				if (geometry == null)
				{
					geometry = new mxGeometry();
					geometry.setRelative(true);
				}
				else
				{
					geometry = (mxGeometry) geometry.clone();
				}
				
				//ID: HospitalExample.impl.NurseImpl@2ca923bb (name: Stefanie Jones, staffID: 7)worksHospitalExample.impl.DepartmentImpl@64ec96c6 (dID: 2, maxRoomCount: 4)
				
				String edgeId = source.getId().toString()+edgeGraph.getValue().toString()+terminal.getId().toString();
				
				
				Edge edge = null;
				
				for(Edge outgoingEdge : dataLoader.edges.get(source.getId())){
									
					if(outgoingEdge.id.equals(edgeId)) {
						edge = outgoingEdge;
						break;
					}
				}
				
				
				if(edge.oppositeId != null) {
							
					
					if(plottedEdges.containsKey(edge.oppositeId)) { 						
						ArrayList<mxPoint> oppositePlot = (ArrayList<mxPoint>) plottedEdges.get(edge.oppositeId).clone();					
						Collections.reverse(oppositePlot);		
						geometry.setPoints(oppositePlot);
						graphModel.setGeometry(edgeGraph, geometry);

						continue;
					}

				}

				
				Point sourceCenter = new Point();
				sourceCenter.x = (int) source.getGeometry().getCenterX();
				sourceCenter.y = (int) source.getGeometry().getCenterY();
				
				Point targetCenter = new Point();
				targetCenter.x = (int) terminal.getGeometry().getCenterX();
				targetCenter.y = (int)terminal.getGeometry().getCenterY();
				
				
				
				EdgePlanner edgePlanner = new EdgePlanner(sourceCenter, targetCenter, source.getGeometry(), terminal.getGeometry(), edgeGrid); 
				
				ArrayList<Point> edgePath = edgePlanner.planEdge();
				
				if(edgePath == null)
					continue;
				
				ArrayList<mxPoint> points = new ArrayList<mxPoint>();
				
				for(Point p : edgePath) {
					points.add(new mxPoint(p.x, p.y));
				}
						
				geometry.setPoints(points);
				graphModel.setGeometry(edgeGraph, geometry);
				
				plottedEdges.put(edgeId, points);


			}
			

		}
	}
	
	private void placeLabels() {
		
		Map<String,Object> cells = graphModel.getCells();	
		
		LabelPlanner lP = new LabelPlanner(nodeGrid);
		
		for(Object cell : cells.values()) {
			
			mxCell c = (mxCell) cell;
			
			if(!c.isEdge()) 
				continue;
			
			mxGeometry geometry = graphModel.getGeometry(c);

			if (geometry == null)
			{
				geometry = new mxGeometry();
				geometry.setRelative(true);
			}
			else
			{
				geometry = (mxGeometry) geometry.clone();
			}
			
			
			mxCellState edgeState = graph.getView().getState(c);
			List<mxPoint> edgePath = edgeState.getAbsolutePoints();
			
			mxRectangle labelBounds = edgeState.getLabelBounds();
			
			System.out.println("EdgePath " + edgePath.toString());
			mxPoint pos = lP.planLabel(labelBounds, edgePath);
			
			System.out.println("New Pos: " + String.valueOf(pos.getX()) + " , " + String.valueOf(pos.getY()));
			
			labelBounds.setX(pos.getX());
			labelBounds.setY(pos.getY());
			
			edgeState.setLabelBounds(labelBounds);
			
			nodeGrid.setGridValues(labelBounds.getRectangle(), Double.MAX_VALUE);
			
		}
		
	}
	
}
	
	
	
	


