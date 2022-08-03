package graphVisualization;

import java.awt.Frame;
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

import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

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
	private DataLoader dataLoader;
	
	private int defaultNodeWidth = 80;
	private int defaultNodeHeight = 40;
	private Point2D.Double defaultNodePosition;
	
	private mxFastOrganicLayout preLayout;
	private Grid grid;
	private GeometryFactory geoFac;
	
	private double graphCenterX ;
	private double graphCenterY;
	
	private double xStretch ;
	private double yStretch;
	
	private ArrayList<mxGeometry> blockedAreas;
	
	
	
	public Visualizer(Shell shell, DataLoader dataLoader) {
		
		this.dataLoader = dataLoader;
		dataLoader.loadData();
		
		this.shell = shell;
		
		
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
		
		GraphControl graphControl = new GraphControl(graph);
//		graphComponent = graphControl;
		composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		frame = SWT_AWT.new_Frame(composite);
		JScrollPane sp = new JScrollPane(graphControl);
		sp.setAutoscrolls(true);
		frame.add(sp);
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
		
		Hashtable<String, Object> edgeStyle2 = new Hashtable<String, Object>();
		edgeStyle2.put(mxConstants.STYLE_FONTCOLOR, "#00000");
		edgeStyle2.put(mxConstants.STYLE_FONTSIZE, "10");
		edgeStyle2.put(mxConstants.STYLE_FILLCOLOR, "#FF0000");
		edgeStyle2.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyle2.put(mxConstants.STYLE_OVERFLOW, "fill");
		stylesheet.putCellStyle("brokenEdges", edgeStyle2);
		
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
		
		geoFac = new GeometryFactory();
		grid = new Grid(geoFac, new Envelope(
				graph.getGraphBounds().getX(), 
				graph.getGraphBounds().getX() + graph.getGraphBounds().getWidth(), 
				graph.getGraphBounds().getY(), 
				graph.getGraphBounds().getY() + graph.getGraphBounds().getHeight()));
		
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
		
		//System.out.println("Placing edge labels");
		//placeLabels();
		
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
			
//			transformGraphPositionToVisBounds(position);
			
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
		LinkedList<Polygon> activeAreas = new LinkedList<Polygon>();
		SynchronizedWorkingAreas synAreas = new SynchronizedWorkingAreas(activeAreas);
		//List<Area> activeAreasSyn = Collections.synchronizedList(activeAreas);
		
		Set<Object> copy = new HashSet<Object>(cells.values());

		for(Object cell : cells.values()) {
			
			mxCell node = (mxCell) cell;
			
			if(node.isEdge())
				continue;
			
			//public EdgePlannerThread(mxGraphModel graphModel, Grid nodeGrid, Grid edgeGrid, mxICell cell, HashMap<String, ArrayList<mxPoint>> plottedEdges, List<Area> activeAreas) {
			EdgePlannerThread newThread = new EdgePlannerThread(graphModel, grid, node, plottedEdges, synAreas);
			
			newThread.start();
			activeThreads.add(newThread);
			try {
				newThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		
		private double workingAreaHeightMargin = 30.0;
		private double workingAreaWidthMargin = 60.0;
		
		private SynchronizedWorkingAreas activeAreas;
		private Polygon ownArea;
		private HashMap<Edge, LinkedList<mxPoint>> plottedEdges;
		
		private mxGraphModel graphModel;
		private Grid grid;
		private mxICell node;
		
		public EdgePlannerThread(mxGraphModel graphModel, Grid grid, mxICell cell, HashMap<Edge,LinkedList<mxPoint>> plottedEdges, SynchronizedWorkingAreas activeAreas) {
			
			if(cell.getValue() == null)	
				this.setName("unknown");
			else {
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
				mxICell edgeGraph = node.getEdgeAt(i);
				mxICell source = edgeGraph.getTerminal(true);
				mxICell terminal = edgeGraph.getTerminal(false);
				
				if(!source.getId().toString().equals(node.getId().toString())) {
					continue;
				}
				
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
				
				Coordinate[] coords = new Coordinate[5];
				double minX = (source.getGeometry().getRectangle().getMinX()<=terminal.getGeometry().getRectangle().getMinX()) ? 
						source.getGeometry().getRectangle().getMinX() : 
							terminal.getGeometry().getRectangle().getMinX();
				double maxX = (source.getGeometry().getRectangle().getMaxX()>=terminal.getGeometry().getRectangle().getMaxX()) ? 
						source.getGeometry().getRectangle().getMaxX() : 
							terminal.getGeometry().getRectangle().getMaxX();
				double minY = (source.getGeometry().getRectangle().getMinY()<=terminal.getGeometry().getRectangle().getMinY()) ? 
						source.getGeometry().getRectangle().getMinY() : 
							terminal.getGeometry().getRectangle().getMinY();
				double maxY = (source.getGeometry().getRectangle().getMaxY()>=terminal.getGeometry().getRectangle().getMaxY()) ? 
						source.getGeometry().getRectangle().getMaxY() : 
							terminal.getGeometry().getRectangle().getMaxY();
				
				coords[0] = new Coordinate(minX, minY);
				coords[1] = new Coordinate(minX, maxY);
				coords[2] = new Coordinate(maxX, maxY);
				coords[3] = new Coordinate(maxX, minY);
				coords[4] = new Coordinate(minX, minY);
				
				ownArea = geoFac.createPolygon(coords);
				
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
				
//				boolean log = false;

				Envelope srcRect = new Envelope(source.getGeometry().getX(), source.getGeometry().getX()+source.getGeometry().getWidth(),
						source.getGeometry().getY(), source.getGeometry().getY()+source.getGeometry().getWidth());
				Envelope trgRect = new Envelope(terminal.getGeometry().getX(), terminal.getGeometry().getX()+terminal.getGeometry().getWidth(),
						source.getGeometry().getY(), terminal.getGeometry().getY()+terminal.getGeometry().getWidth());
				Envelope bounds = ownArea.getEnvelopeInternal();
				
				EdgePlanner edgePlanner = new EdgePlanner(edge, srcRect, trgRect, bounds, grid); 
				LinkedList<Point2D> edgePath = edgePlanner.planEdge();
				
				if(edgePath == null) {
					edgeGraph.setStyle("brokenEdges");
					graphModel.setStyle(edgeGraph, "brokenEdges");
					activeAreas.deallocateWorkingArea(ownArea);
					continue;
				}
				
				LinkedList<mxPoint> points = new LinkedList<mxPoint>();
				
				for(Point2D p : edgePath) {
					points.add(new mxPoint(p.getX(), p.getY()));
				}
						
				geometry.setPoints(points);
				graphModel.setGeometry(edgeGraph, geometry);
				plottedEdges.put(edge, points);
				activeAreas.deallocateWorkingArea(ownArea);

			}
		}
		
		
	}
	
	public class SynchronizedWorkingAreas{
		
		private List<Polygon> activeAreas;
		private List<Long> activeThreads;
		
		public SynchronizedWorkingAreas(LinkedList<Polygon> areas) {
			activeAreas = areas;
			activeThreads = new LinkedList<Long>();
			//waitingThreads = new LinkedList<Long>();
		}
		
		public synchronized boolean allocateWorkingArea(Polygon threadArea) {
			if(blocked(threadArea)) {
				return false;
			}
			else {
				activeAreas.add(threadArea);
			}
			
			return true;
		}
		
		public synchronized void deallocateWorkingArea(Polygon threadArea) {
			activeAreas.remove(threadArea);
			this.notifyAll();
		}
		
		private synchronized boolean blocked(Polygon threadArea) {
			for(Polygon a : activeAreas) {
				try {
					if(a.overlaps(threadArea)) {
						return true;
					}
				} catch(Exception e) {
					return false;
				}	
			}
			
			return false;
		}
		
		
		private synchronized void printStatus() {
			System.out.println("Active Threads : " + activeThreads.toString());
			System.out.println("Waiting Threads : ");
		}
	}
	
}
	
	
	
	


