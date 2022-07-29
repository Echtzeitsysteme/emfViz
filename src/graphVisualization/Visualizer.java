package graphVisualization;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
	private Grid grid;
	
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
		
		org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		
		//System.out.println("Monitor bounds:" + monitorBounds.toString());
		
		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.4 - defaultNodeWidth *0.5, ((double) shellBounds.height) * 0.4- defaultNodeHeight *0.5);
		//defaultNodePosition = new Point2D.Double(shellBounds.width, shellBounds.height);
		//System.out.println("Shell bounds:" + shell.getBounds().toString());
		//System.out.println("Default Position:" + defaultNodePosition.toString());
		
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
		dataLoader.edges.put(edge, edge);
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
		
		preLayout = new mxFastOrganicLayout(graph);
		preLayout.setForceConstant(150);
		preLayout.setMinDistanceLimit(8);
		preLayout.setUseInputOrigin(false);
		preLayout.setDisableEdgeStyle(false);
		
		org.eclipse.swt.graphics.Rectangle shellBounds = shell.getBounds();
		
		//System.out.println("Shellbounds: " + shellBounds.toString());
		
		double hWRatio = (double) shellBounds.height / (double)shellBounds.width;
		
		int baseSize = Math.min(dataLoader.nodes.size() * dataLoader.nodes.size(), shellBounds.height * shellBounds.width);
		
		//System.out.println("baseSize: " + String.valueOf(baseSize));
		
		int sizeX = (int) Math.ceil(Math.sqrt(baseSize));
		int sizeY = (int) Math.ceil(Math.sqrt(baseSize));
		
		//System.out.println("SizeX: " + String.valueOf(sizeX));
		//System.out.println("SizeY: " + String.valueOf(sizeY));
		
		grid = new Grid(shellBounds, sizeX, sizeY,  minNodeDistanceNodes);
		
		blockedAreas = new ArrayList<mxGeometry>();
		
	}
	
	private void updateGraphShellTransformation() {
	
		double graphWidth = graph.getGraphBounds().getWidth();
		double graphHeight = graph.getGraphBounds().getHeight();
		
		graphCenterX = graph.getGraphBounds().getCenterX();
		graphCenterY = graph.getGraphBounds().getCenterY();
		
		xStretch = (shell.getMonitor().getClientArea().width *(1-grid.margin))/graphWidth;
		yStretch = (shell.getMonitor().getClientArea().height*(1-grid.margin))/graphHeight;
		
		
	}
	
	private void runLayout() {

		preLayout.execute(graph.getDefaultParent());
		
		blockedAreas.clear();
		
		System.out.println("Placing nodes");
		placeNodes();
		System.out.println("Routing edges");
		placeEdges();
		System.out.println("Placing edge labels");
		placeLabels();
		System.out.println("Ready");
		
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
			
			mxPoint nearestGridPoint = grid.placeInFreeGridPosition(c, position, c.getGeometry().getRectangle().getWidth(),
					c.getGeometry().getRectangle().getHeight());
			
			if (nearestGridPoint == null) {
				System.out.println("Node grid position not found");
				nearestGridPoint = new mxPoint(position.x, position.y);						
			}
	
			
			Object[] cellsToMove = {cell};				
			graph.moveCells(cellsToMove,  nearestGridPoint.getX() - geom.getX(), nearestGridPoint.getY() - geom.getY());
			
			//System.out.println("New geom: " + c.getGeometry().toString() + " With rectangle: " + c.getGeometry().getRectangle().toString());
			blockedAreas.add(c.getGeometry());
			
		}
		
	}
	
	private void placeEdges() {
		
		HashMap<Edge, LinkedList<mxPoint>> plottedEdges = new HashMap<Edge,LinkedList<mxPoint>>();
		Map<String,Object> cells = graphModel.getCells();
		
		ArrayList<EdgePlannerThread> activeThreads = new ArrayList<EdgePlannerThread>();
		LinkedList<Area> activeAreas = new LinkedList<Area>();
		synchronizedWorkingAreas synAreas = new synchronizedWorkingAreas(activeAreas);
		//List<Area> activeAreasSyn = Collections.synchronizedList(activeAreas);
		
		Set<Object> copy = new HashSet<Object>(cells.values());

		for(Object cell : cells.values()) {
			
			mxCell node = (mxCell) cell;
			
			if(node.isEdge())
				continue;
			
			//public EdgePlannerThread(mxGraphModel graphModel, Grid nodeGrid, Grid edgeGrid, mxICell cell, HashMap<String, ArrayList<mxPoint>> plottedEdges, List<Area> activeAreas) {
			EdgePlannerThread newThread = new EdgePlannerThread(graphModel, grid, edgeGrid, node, plottedEdges, synAreas);
			
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
			mxPoint pos = lP.planLabel(labelBounds, edgePath, edgeState.getLabel());
			
			//System.out.println("New Pos: " + String.valueOf(pos.getX()) + " , " + String.valueOf(pos.getY()));
			
			labelBounds.setX(pos.getX());
			labelBounds.setY(pos.getY());
			
			edgeState.setLabelBounds(labelBounds);
			geometry.setX(pos.getX());
			geometry.setY(pos.getY());
			grid.placeInFreeGridPosition(labelBounds, new Point2D.Double(pos.getX(), pos.getY()), labelBounds.getWidth(), labelBounds.getHeight());
//			nodeGrid.setGridValues(labelBounds.getRectangle(), Double.MAX_VALUE);
			
		}
		
	}
	
	
	private class EdgePlannerThread extends Thread{
		
		private int workingAreaHeightMargin = 30;
		private int workingAreaWidthMargin = 60;
		
		private synchronizedWorkingAreas activeAreas;
		private Area ownArea;
		private HashMap<Edge, LinkedList<mxPoint>> plottedEdges;
		
		private mxGraphModel graphModel;
		private Grid nodeGrid;
		private Grid edgeGrid;
		private mxICell node;
		
		public EdgePlannerThread(mxGraphModel graphModel, Grid nodeGrid, Grid edgeGrid, mxICell cell, HashMap<Edge,LinkedList<mxPoint>> plottedEdges, synchronizedWorkingAreas activeAreas) {
			
			if(cell.getValue() == null)	
				this.setName("unknown");
			else {
				this.setName(cell.getValue().toString());
			}
			
			this.graphModel = graphModel;
			this.nodeGrid = nodeGrid;
			this.edgeGrid = edgeGrid;
			this.node = cell;
			
			this.plottedEdges = plottedEdges;
			this.activeAreas = activeAreas;
			
		}
		
		public void run() {
			
			//EdgePlanner edgePlanner = new EdgePlanner(sourceCenter, targetCenter, source.getGeometry(), terminal.getGeometry(), edgeGrid);
			
			//System.out.println("Thread " + String.valueOf(this.getId()) + " started. Plotting " + node.getValue()); 
			
			for(int i = 0; i < node.getEdgeCount(); i++) {
				
				mxICell edgeGraph = node.getEdgeAt(i);
				mxICell source = edgeGraph.getTerminal(true);
				mxICell terminal = edgeGraph.getTerminal(false);
				
				if(!source.getId().toString().equals(node.getId().toString())) {
					continue;
				}
				
				//System.out.println("Source: " + node.getValue() + " " + String.valueOf(this.getId()) );
				
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
				
				
				//String edgeId = source.getId().toString()+edgeGraph.getValue().toString()+terminal.getId().toString();
				
				Edge edge = (Edge) edgeGraph.getValue();
				
				if(edge.ref.getEOpposite() != null) {

				Edge oppositeEdge = dataLoader.edges.get(Objects.hash(edge.target, edge.ref.getEOpposite(),edge.source));
				
	
					if(plottedEdges.containsKey(oppositeEdge)) { 						
						LinkedList<mxPoint> oppositePlot = (LinkedList<mxPoint>) plottedEdges.get(oppositeEdge).clone();					
						Collections.reverse(oppositePlot);		
						geometry.setPoints(oppositePlot);
						graphModel.setGeometry(edgeGraph, geometry);

						continue;
					}

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
			
				ownArea = new Area(s);
				
				boolean allocated = false;
				while(!allocated) {
					
					allocated = activeAreas.allocateWorkingArea(ownArea);
					
					if(allocated)
						break;
					
					synchronized(activeAreas) {
						try {
							activeAreas.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					
					}
					
					
				}
				
				boolean log = false;

							
				EdgePlanner edgePlanner = new EdgePlanner(sourceCenter, targetCenter, source.getGeometry(), terminal.getGeometry(), edgeGrid); 
				
				LinkedList<Point> edgePath = edgePlanner.planEdge();
				
				if(edgePath == null) {
					activeAreas.deallocateWorkingArea(ownArea);
					continue;
				}
				
				LinkedList<mxPoint> points = new LinkedList<mxPoint>();
				
				for(Point p : edgePath) {
					points.add(new mxPoint(p.x, p.y));
				}
						
				geometry.setPoints(points);
				graphModel.setGeometry(edgeGraph, geometry);
				
				plottedEdges.put(edge, points);
				
				//System.out.println("Deallocate" + String.valueOf(this.getId())); 
				
				activeAreas.deallocateWorkingArea(ownArea);

			}
			
			//if(activeAreas.isActive(this.getId()))
			//	System.out.println("Terminating: " + String.valueOf(this.getId())); 
			//System.out.println("Terminating: " + String.valueOf(this.getId()));
			//if(node.getValue().equals("Hospital"))
			//	activeAreas.printStatus();
		}
		
		
	}
	
	public class synchronizedWorkingAreas{
		
		private List<Area> activeAreas;
		private List<Long> activeThreads;
		//private List<Long> waitingThreads;
	
		
		public synchronizedWorkingAreas(LinkedList<Area> areas) {
			activeAreas = areas;
			activeThreads = new LinkedList<Long>();
			//waitingThreads = new LinkedList<Long>();
		}
		
		public synchronized boolean allocateWorkingArea(Area threadArea) {
			
			//System.out.println("Trying to allocate: " + String.valueOf(threadID));
			//System.out.println("Active threads: " + String.valueOf(activeThreads.toString()));
			
			if(blocked(threadArea)) {
				
				//waitingThreads.add(callingThread);
				//if(!waitingThreads.contains(callingThreadID))
				//waitingThreads.add(callingThreadID);
				return false;
			}
			else {
				activeAreas.add(threadArea);
				//activeThreads.add(callingThreadID);

				//waitingThreads.remove(callingThread);
				
				
			}
			
			return true;
		}
		
		public synchronized void deallocateWorkingArea(Area threadArea) {
			
			activeAreas.remove(threadArea);
			//activeThreads.remove(threadID);
			
			/*for(Thread t : Thread.getAllStackTraces().keySet()) {
				
				boolean found =	waitingThreads.remove(t.getId());
					
				if(found) {
					synchronized(t){
					t.notify();
					}
					
				}
					
			}*/
			
			this.notifyAll();
			//printStatus();

		}
		
		private synchronized boolean blocked(Area threadArea) {
			
			for(Area a : activeAreas) {
				
				Area copy = (Area) a.clone();
				
				copy.intersect(threadArea);
				
				if(!copy.isEmpty())
					return true;
			}
			
			return false;
			
		}
		
		
		private synchronized void printStatus() {
			System.out.println("Active Threads : " + activeThreads.toString());
			//System.out.println("Allocated Areas : ");
			
			//for(Area a : activeAreas) {
			//	System.out.println(a.getBounds().toString());
			//}
			
			System.out.println("Waiting Threads : ");
			
			/*for(long id : waitingThreads) {
				System.out.print(id + ", ");
			}*/
		}
	}
	
}
	
	
	
	


