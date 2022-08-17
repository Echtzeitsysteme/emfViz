package graphVisualization;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

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

public class Visualizer {
	
	//Gui
	private Shell shell;
	private Composite composite;
	private Frame frame;
	
	//mxGraph elements
	private mxGraph graph;
	private mxGraphModel graphModel;
	private mxGraphComponent graphComponent;
	
	//contains data from eMoflon model
	private DataLoader dataLoader;
	
	
	private int defaultNodeWidth = 80;
	private int defaultNodeHeight = 40;
	private Point2D.Double defaultNodePosition;
	
	//primitive layout algorithm
	private CustomFastOrganicLayout preLayout;
	
	//layout grid
	private Grid grid;
	
	// transformation variables
	private double graphCenterX ;
	private double graphCenterY;
	private double xStretch ;
	private double yStretch;
	
	//margin to GUI border
	private double margin = 0.1;
	
	//spacing around placed nodes and labels
	private int minNodeDistanceNodes = 15;
	
	
	public Visualizer(Shell shell, DataLoader dataLoader) {
		
		this.dataLoader = dataLoader;
		dataLoader.loadData();
		
		//set up GUI
		this.shell = shell;
		composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		composite.setVisible(true);
		frame = SWT_AWT.new_Frame(composite);
		
		graph = new mxGraph();
		graphModel = ((mxGraphModel)graph.getModel());
		
		org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		
		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.4 - defaultNodeWidth *0.5, ((double) shellBounds.height) * 0.4- defaultNodeHeight *0.5);
		
		addStyles();
		
		//insert graph data
		insertDataIntoGraph();
		
		//set basic layout settings
		setUpLayout();
		
		//layout every inserted element
		runTotalLayout();
		
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
		
	}
	
	protected void insertNodeIntoGraph(Node node, double x , double y) {
		
		graph.insertVertex(graph.getDefaultParent(), String.valueOf(node.hashCode()), node, x, y, defaultNodeWidth, defaultNodeHeight, node.styleCategory);
		
	}
	
	protected void insertNodeIntoGraph(Node node) {

		double centerX = graph.getGraphBounds().getCenterX();
		double centerY = graph.getGraphBounds().getCenterY();
		
		insertNodeIntoGraph(node,defaultNodePosition.x - centerX, defaultNodePosition.y - centerY);
		dataLoader.nodes.add(node);
	}
	
	protected void insertEdgeIntoGraph(Edge edge) {
		graph.insertEdge(graph.getDefaultParent(), String.valueOf(edge.hashCode()), edge, graphModel.getCell(edge.getSourceID()), graphModel.getCell(edge.getTargetID()),edge.styleCategory);
		dataLoader.edges.put(edge.hashCode(),edge);
	}
	
	protected void removeNodeInGraph(Node node) {
		removeElement(String.valueOf(node.hashCode()));
		dataLoader.nodes.remove(node);
	}
	
	protected void removeEdgeInGraph(Edge edge) {
		removeElement(String.valueOf(edge.hashCode()));
		dataLoader.edges.remove(edge);
	}
	
	
	private void removeElement(String hashCode) {
		
		Object[] cells = new Object[] {graphModel.getCell(hashCode)};
		
		graph.removeCells(cells);
	}
	
	
	private void insertDataIntoGraph() {

		
		for(Node n: dataLoader.nodes) {
			insertNodeIntoGraph(n);
		}
		
		for(Edge edge : dataLoader.edges.values()) {
			insertEdgeIntoGraph(edge);
		}
		
	}
	
	private void setUpLayout() {
		
		preLayout = new CustomFastOrganicLayout(graph);
		preLayout.setForceConstant(150);
		preLayout.setMinDistanceLimit(8);
		preLayout.setUseInputOrigin(false);
		preLayout.setDisableEdgeStyle(false);
		
		org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		
		//Initialize grid with size of the shell and a point distance of 1
		grid = new Grid(shellBounds,(int)Math.floor((1-margin ) *shellBounds.width), (int) Math.floor((1-margin ) *shellBounds.height), minNodeDistanceNodes);		
	}
	
	private void updateGraphShellTransformation() {
		
		// Graph bounds will be strechted to monitor bounds. Set up the transformation of element coordinates accordingly.
		double graphWidth = graph.getGraphBounds().getWidth();
		double graphHeight = graph.getGraphBounds().getHeight();
		
		graphCenterX = graph.getGraphBounds().getCenterX();
		graphCenterY = graph.getGraphBounds().getCenterY();
		
		xStretch = (shell.getMonitor().getClientArea().width *(1-grid.margin))/graphWidth;
		yStretch = (shell.getMonitor().getClientArea().height*(1-grid.margin))/graphHeight;
	}
	
	private void runLayout() {

		preLayout.execute(graph.getDefaultParent());
		
		System.out.println("Placing nodes");
		placeNodes();
		System.out.println("Routing edges");
		placeEdges();
		System.out.println("Placing edge labels");
		placeLabels();
		System.out.println("Ready");
		
		setIgnoreStatus(true);
	}
	
	// Layout new elements only. Others keep their positions / routed paths.
	public void runIncrementalLayout() {
		runLayout();
	}
	
	public void runTotalLayout() {
		
		setIgnoreStatus(false);
		runLayout();
		
	}
	
	// Change ignore status of every element in the mxGraph
	private void setIgnoreStatus(boolean ignored) {
		
		Map<String,Object> cells = graphModel.getCells();	
		for(Object cell : cells.values()) {
			
			mxCell c = (mxCell) cell;
		
			if(c.isEdge()) {
				((Edge)c.getValue()).ignored = ignored;
				continue;
			}
				
			if(c.isVertex()){
				((Node)c.getValue()).ignored = ignored;
				continue;
			}

		}
		
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
			if(((Node)c.getValue()).ignored)
				continue;
			
			Point2D.Double position = new Point2D.Double( geom.getX(), geom.getY() );
			
			transformGraphPositionToVisBounds(position);
			
			//get free grid point with minimal collisions to other elements near the origin that was proposed by the fastOrganic layout.
			mxPoint nearestGridPoint = grid.getFreeGridPosition(position, c.getGeometry().getRectangle());
			
			if (nearestGridPoint == null) {
				//keep original guess
				nearestGridPoint = new mxPoint(position.x, position.y);						
			}
	
			// move cell to calculated origin
			Object[] cellsToMove = {cell};				
			graph.moveCells(cellsToMove,  nearestGridPoint.getX() - geom.getX(), nearestGridPoint.getY() - geom.getY());
			
			//block grid
			grid.setGridValues(c.getGeometry().getRectangle(), Double.MAX_VALUE);
			
			((Node)c.getValue()).ignored = true;
			
		}
		
		
	}
	
	private void placeEdges() {
		
		// Already plotted Edges
		HashMap<Edge, LinkedList<mxPoint>> plottedEdges = new HashMap<Edge,LinkedList<mxPoint>>();
		
		Map<String,Object> cells = graphModel.getCells();
		
		//Multithreading overhead
		ArrayList<EdgePlannerThread> activeThreads = new ArrayList<EdgePlannerThread>();
		LinkedList<Area> activeAreas = new LinkedList<Area>();
		synchronizedWorkingAreas synAreas = new synchronizedWorkingAreas(activeAreas);

		for(Object cell : cells.values()) {
			
			mxCell node = (mxCell) cell;
			
			if(node.isEdge())
				continue;
			
			if((Node)node.getValue() == null)
				continue;
			
			// Start a new thread for every node with outgoing edges
			EdgePlannerThread newThread = new EdgePlannerThread(graphModel,grid,node, plottedEdges, synAreas);
			
			newThread.start();
			activeThreads.add(newThread);
			
		}
		
		for(EdgePlannerThread t : activeThreads) {

			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void placeLabels() {
		
		Map<String,Object> cells = graphModel.getCells();	
		
		LabelPlanner lP = new LabelPlanner(grid);
		
		for(Object cell : cells.values()) {
			
			mxCell c = (mxCell) cell;
			
			if(!c.isEdge()) 
				continue;
			if(((Edge)c.getValue()).ignored)
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
			//extract corresponding edge (viapoints)
			List<mxPoint> edgePath = edgeState.getAbsolutePoints();
			
			mxRectangle labelBounds = edgeState.getLabelBounds();
			mxPoint pos = lP.planLabel(labelBounds, edgePath, edgeState.getLabel());
			
			labelBounds.setX(pos.getX());
			labelBounds.setY(pos.getY());
			
			//block area in grid
			edgeState.setLabelBounds(labelBounds);
			grid.setGridValues(labelBounds.getRectangle(), Double.MAX_VALUE);
			
		}
		
	}
	
	
	private class EdgePlannerThread extends Thread{
		
		private int workingAreaHeightMargin = 30;
		private int workingAreaWidthMargin = 60;
		
		private synchronizedWorkingAreas activeAreas;
		private Area ownArea;
		private HashMap<Edge, LinkedList<mxPoint>> plottedEdges;
		
		private mxGraphModel graphModel;
		private Grid grid;
		private mxICell node;
		
		public EdgePlannerThread(mxGraphModel graphModel,Grid grid, mxICell cell, HashMap<Edge,LinkedList<mxPoint>> plottedEdges, synchronizedWorkingAreas activeAreas) {
			
			if(cell.getValue() == null)	
				this.setName("unknown");
			else {
				// identify thread
				this.setName(cell.getValue().toString());
			}
			
			this.graphModel = graphModel;
			this.grid = grid;
			this.node = cell;
			
			this.plottedEdges = plottedEdges;
			this.activeAreas = activeAreas;
			
		}
		
		public void run() {
			
			for(int i = 0; i < node.getEdgeCount(); i++) {
				
				mxICell mxedgeInGraph = node.getEdgeAt(i);
				mxICell source = mxedgeInGraph.getTerminal(true);
				mxICell terminal = mxedgeInGraph.getTerminal(false);
				
				//only route outgoing edges
				if(!source.getId().toString().equals(node.getId().toString())) {
					continue;
				}
				
				if(((Edge)mxedgeInGraph.getValue()).ignored)
					continue;
				

				mxGeometry geometry = mxedgeInGraph.getGeometry();
				if (geometry == null)
				{
					geometry = new mxGeometry();
					geometry.setRelative(true);
				}
				else
				{
					geometry = (mxGeometry) geometry.clone();
				}
				
				
				
				
				int[] xcords = new int[8];
				int[] ycords = new int[8];
				
				Point sourceCenter = new Point();
				sourceCenter.x = (int) source.getGeometry().getCenterX();
				sourceCenter.y = (int) source.getGeometry().getCenterY();
				
				Point targetCenter = new Point();
				targetCenter.x = (int) terminal.getGeometry().getCenterX();
				targetCenter.y = (int)terminal.getGeometry().getCenterY();
				
				xcords[0] = (int) source.getGeometry().getRectangle().getMinX() - workingAreaWidthMargin;
				ycords[0] = (int) source.getGeometry().getRectangle().getMinY() - workingAreaHeightMargin;
				
				xcords[1] = (int) source.getGeometry().getRectangle().getMaxX() + workingAreaWidthMargin;
				ycords[1] = (int) source.getGeometry().getRectangle().getMinY() - workingAreaHeightMargin;
				
				xcords[2] = (int) source.getGeometry().getRectangle().getMinX() - workingAreaWidthMargin;
				ycords[2] = (int) source.getGeometry().getRectangle().getMaxY() + workingAreaHeightMargin;
				
				xcords[3] = (int) source.getGeometry().getRectangle().getMaxX() + workingAreaWidthMargin;
				ycords[3] = (int) source.getGeometry().getRectangle().getMaxY() + workingAreaHeightMargin;
				
				xcords[4] = (int) terminal.getGeometry().getRectangle().getMinX() - workingAreaWidthMargin;
				ycords[4] = (int) terminal.getGeometry().getRectangle().getMinY() - workingAreaHeightMargin;
				
				xcords[5] = (int) terminal.getGeometry().getRectangle().getMaxX() + workingAreaWidthMargin;
				ycords[5] = (int) terminal.getGeometry().getRectangle().getMinY() - workingAreaHeightMargin;
				
				xcords[6] = (int) terminal.getGeometry().getRectangle().getMinX() - workingAreaWidthMargin;
				ycords[6] = (int) terminal.getGeometry().getRectangle().getMaxY() + workingAreaHeightMargin;
				
				xcords[7] = (int) terminal.getGeometry().getRectangle().getMaxX() + workingAreaWidthMargin;
				ycords[7] = (int) terminal.getGeometry().getRectangle().getMaxY() + workingAreaHeightMargin;
				
				Polygon s = new Polygon(xcords,ycords, 8);
			
				// build a bounding box encompassing target and source node geometries as well as the space in between them.
				ownArea = new Area(s.getBounds2D());
				
				boolean allocated = false;
				while(!allocated) {
					
					// try to rout current edge -> check if required area is currently worked on
					allocated = activeAreas.allocateWorkingArea(ownArea);
					
					if(allocated)
						break;
					
					synchronized(activeAreas) {
						try {
							// wait if it is
							activeAreas.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
					}
					
					
				}
				
				Edge edge = (Edge) mxedgeInGraph.getValue();
				
				//check if an opposite for the current edge was specified
				if(edge.ref.getEOpposite() != null) {

				Edge oppositeEdge = dataLoader.edges.get(Objects.hash(edge.target, edge.ref.getEOpposite(),edge.source));
				
					// if it was already plotted use the reverse of its path
					if(plottedEdges.containsKey(oppositeEdge)) { 						
						LinkedList<mxPoint> oppositePlot = (LinkedList<mxPoint>) plottedEdges.get(oppositeEdge).clone();					
						Collections.reverse(oppositePlot);		
						geometry.setPoints(oppositePlot);
						graphModel.setGeometry(mxedgeInGraph, geometry);

						activeAreas.deallocateWorkingArea(ownArea);
						continue;
					}

				}

				
				//use EdgePlanner class for routing
				EdgePlanner edgePlanner = new EdgePlanner(sourceCenter, targetCenter, source.getGeometry(), terminal.getGeometry(), grid); 
				
				LinkedList<Point> edgePath = edgePlanner.planEdge();
				
				if(edgePath == null) {
					activeAreas.deallocateWorkingArea(ownArea);
					continue;
				}
				
				LinkedList<mxPoint> points = new LinkedList<mxPoint>();
				
				for(Point p : edgePath) {
					points.add(new mxPoint(p.x, p.y));
				}
				
				//set edge via points
				geometry.setPoints(points);
				graphModel.setGeometry(mxedgeInGraph, geometry);
				
				plottedEdges.put(edge, points);
				activeAreas.deallocateWorkingArea(ownArea);
	

			}
			
		}
		
		
	}
	
	public class synchronizedWorkingAreas{
		
		private List<Area> activeAreas;
		
		public synchronizedWorkingAreas(LinkedList<Area> areas) {
			activeAreas = areas;
		}
		
		public synchronized boolean allocateWorkingArea(Area threadArea) {
			
			// check if area collides with areas that are currently worked on by other threads
			if(blocked(threadArea)) {

				return false;
			}
			else {
				activeAreas.add(threadArea);
				
			}
			
			return true;
		}
		
		public synchronized void deallocateWorkingArea(Area threadArea) {
			
			activeAreas.remove(threadArea);		
			this.notifyAll();

		}
		
		private synchronized boolean blocked(Area threadArea) {
			
			for(Area a : activeAreas) {
				
				Area copy = (Area) a.clone();
				copy.intersect(threadArea);
				
				// if intersection is empty for all active areas new area can be savely worked on since it has no collisions
				if(!copy.isEmpty())
					return true;
			}
			
			return false;
			
		}
		
	}
	
}
	
	
	
	


