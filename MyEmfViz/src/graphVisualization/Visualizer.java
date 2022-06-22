package graphVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import userInterface.MainWindow;

public class Visualizer {
	
	private Shell shell;
	private Panel panelSrc;
	private Panel panelTrg;
	
	private mxGraph graph;
	private mxGraphModel graphModel;
	private mxGraphComponent graphComponent;
	private DataLoader dataLoader;
	
	private mxGraph graphTarget;
	private mxGraphModel graphModelTarget;
	private mxGraphComponent graphComponentTarget;
	private DataLoader dataLoaderTarget;
	
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
	
	
	
	public Visualizer(Shell shell, DataLoader dataLoader, DataLoader dataLoaderTarget) {
		
		System.out.println("overload constructor...");
		
		/*source dataModel*/
		this.dataLoader = dataLoader;
		dataLoader.loadData();
		
		/*target dataModel*/
		this.dataLoaderTarget = dataLoaderTarget;
		dataLoaderTarget.loadData();
		
		
		this.shell = shell;
		
		
		//initialize shell layout
		
		MainWindow graphVisualizer = new MainWindow(this.shell);
		
		panelSrc = graphVisualizer.panelSrc;
		panelTrg = graphVisualizer.panelTrg;
		
		
		/* source graph*/
		graph = new mxGraph();
		graphModel = ((mxGraphModel)graph.getModel());
		
		/*target graph*/
		graphTarget = new mxGraph();
		graphModelTarget = ((mxGraphModel)graphTarget.getModel()); 
		
		
		org.eclipse.swt.graphics.Rectangle shellBounds = this.shell.getBounds();
		
		//System.out.println("Monitor bounds:" + monitorBounds.toString());
		
		//nur noch 0.5 mal so viel zu vor in x-Dimension
		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.5 * 0.4 - defaultNodeWidth *0.5, ((double) shellBounds.height) * 0.4- defaultNodeHeight *0.5);
		
		
		/*src graph*/
		addStyles();
		insertDataIntoGraph(graph, dataLoader);
		setUpLayout(graph, panelSrc);
		runLayout(graph, graphModel);
		
		/*add src graph to src panel*/
		graphComponent = new mxGraphComponent(graph);
		panelSrc.add(graphComponent);
		
		
		/*trg graph*/
		insertDataIntoGraph(graphTarget, dataLoaderTarget);
		setUpLayout(graphTarget, panelTrg);
		runLayout(graphTarget, graphModelTarget);
		
		/* add trg graph to trg panel*/
		graphComponentTarget = new mxGraphComponent(graphTarget);
		panelTrg.add(graphComponentTarget);
		
	}
	
	private void addStyles() {
		
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> cellStyle = new Hashtable<String, Object>();
		cellStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		cellStyle.put(mxConstants.STYLE_OPACITY, 50);
		cellStyle.put(mxConstants.STYLE_FONTCOLOR, "#00000");
		cellStyle.put(mxConstants.STYLE_FONTSIZE, "10");
		cellStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		cellStyle.put(mxConstants.ALIGN_CENTER, "1");
		cellStyle.put(mxConstants.STYLE_OVERFLOW, "hidden");
		stylesheet.putCellStyle("defaultNode", cellStyle);
		
		
		Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#00000");
		edgeStyle.put(mxConstants.STYLE_FONTSIZE, "8");
		edgeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		edgeStyle.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyle.put(mxConstants.STYLE_OVERFLOW, "fill");
		stylesheet.putCellStyle("defaultEdges", edgeStyle);
		
		//stylesheet.setDefaultVertexStyle(cellStyle);
		//stylesheet.setDefaultEdgeStyle(edgeStyle);
	}
	
	private void insertDataIntoGraph(mxGraph graph, DataLoader dataLoader) {
		
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
		
		//System.out.println("Center X: " + String.valueOf(centerX));
		//System.out.println("Center Y: " + String.valueOf(centerY));
	}
	
	private void setUpLayout(mxGraph graph, Panel panel) {
		
		preLayout = new mxFastOrganicLayout(graph);
		preLayout.setForceConstant(150);
		preLayout.setMinDistanceLimit(4);
		preLayout.setUseInputOrigin(false);
		preLayout.setDisableEdgeStyle(false);
		
		//org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		Rectangle shellBounds = panel.getBounds();
		
		//System.out.println("Shellbounds: " + shellBounds.toString());
		
		double hWRatio = (double) shellBounds.height / (double)shellBounds.width;
		
		int baseSize = Math.min(dataLoader.nodes.size() * dataLoader.nodes.size(), shellBounds.height * shellBounds.width);
		
		//System.out.println("baseSize: " + String.valueOf(baseSize));
		
		int sizeX = (int) Math.ceil(Math.sqrt(baseSize));
		int sizeY = (int) Math.ceil(Math.sqrt(baseSize));
		
		//System.out.println("SizeX: " + String.valueOf(sizeX));
		//System.out.println("SizeY: " + String.valueOf(sizeY));
		
		nodeGrid = new Grid(shellBounds, sizeX, sizeY,  minNodeDistanceNodes);
		edgeGrid = new Grid(shellBounds,(int)Math.floor((1-margin ) *shellBounds.width), (int) Math.floor((1-margin ) *shellBounds.height), minNodeDistanceEdges);
		
		blockedAreas = new ArrayList<mxGeometry>();
		
	}
	
	private void updateGraphShellTransformation(mxGraph graph) {
	
		double graphWidth = graph.getGraphBounds().getWidth();
		double graphHeight = graph.getGraphBounds().getHeight();
		
		graphCenterX = graph.getGraphBounds().getCenterX();
		graphCenterY = graph.getGraphBounds().getCenterY();
		
		//xStretch = (shell.getMonitor().getClientArea().width *(1-nodeGrid.margin))/graphWidth;
		//yStretch = (shell.getMonitor().getClientArea().height*(1-nodeGrid.margin))/graphHeight;
		
		//angepasst da nun nur halber Platz pro Graph zur Verfügung steht - betrifft nur x Dimension
		xStretch = (shell.getMonitor().getClientArea().width * 0.5 *(1-nodeGrid.margin))/graphWidth;
		yStretch = (shell.getMonitor().getClientArea().height*(1-nodeGrid.margin)*0.9-30)/graphHeight;
		
	}
	
	private void runLayout(mxGraph graph, mxGraphModel graphModel) {

		preLayout.execute(graph.getDefaultParent());
		
		blockedAreas.clear();
		
		System.out.println("Placing nodes");
		placeNodes(graphModel, graph);
		System.out.println("Routing edges");
		placeEdges(graphModel, graph);
		System.out.println("Placing edge labels");
		placeLabels(graphModel, graph);
		System.out.println("Ready");
		
	}
	
	private void transformGraphPositionToVisBounds(Point2D.Double position) {
		
		
		position.x = Math.max((position.x - graphCenterX) * xStretch + defaultNodePosition.x,0);
		position.y = Math.max((position.y - graphCenterY) * yStretch + defaultNodePosition.y,0);
		
	}

	
	
	private void placeNodes(mxGraphModel graphModel, mxGraph graph) {
		
		updateGraphShellTransformation(graph);
		
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
			
			
			//System.out.println("Old geom: " + c.getGeometry().toString() + " With rectangle: "+ c.getGeometry().getRectangle().toString());
			
			Object[] cellsToMove = {cell};				
			graph.moveCells(cellsToMove,  nearestGridPoint.getX() - geom.getX(), nearestGridPoint.getY() - geom.getY());
			
			//System.out.println("New geom: " + c.getGeometry().toString() + " With rectangle: " + c.getGeometry().getRectangle().toString());
			blockedAreas.add(c.getGeometry());
			
		}
		
		for(mxGeometry g : blockedAreas) {
			edgeGrid.setGridValues(g.getRectangle(), Double.MAX_VALUE);
		}
		
	}
	
	private void placeEdges(mxGraphModel graphModel, mxGraph graph) {
		
		HashMap<String, ArrayList<mxPoint>> plottedEdges = new HashMap<String, ArrayList<mxPoint>>();
		Map<String,Object> cells = graphModel.getCells();	
		
		for(Object cell : cells.values()) {
			
			mxCell node = (mxCell) cell;
			
			if(node.isEdge())
				continue;
			
			
			System.out.println("Node: " + node.getValue());
			
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
				
				
				// Added by JL
				/*if (source == null || edgeGraph == null || terminal == null){
					continue;
				}*/
				
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
	
	private void placeLabels(mxGraphModel graphModel, mxGraph graph) {
		
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
			
			//System.out.println("EdgePath " + edgePath.toString());
			mxPoint pos = lP.planLabel(labelBounds, edgePath);
			
			//System.out.println("New Pos: " + String.valueOf(pos.getX()) + " , " + String.valueOf(pos.getY()));
			
			labelBounds.setX(pos.getX());
			labelBounds.setY(pos.getY());
			
			edgeState.setLabelBounds(labelBounds);
			
			nodeGrid.setGridValues(labelBounds.getRectangle(), Double.MAX_VALUE);
			
		}
		
	}
	
}
	
	
	
	


