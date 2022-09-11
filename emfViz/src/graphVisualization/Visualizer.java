package graphVisualization;

import java.awt.Frame;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class Visualizer {

	// Gui
	private Shell shell;
	private Composite composite;
	protected Frame frame;

	// mxGraph elements
	protected mxGraph graph;
	protected mxGraphModel graphModel;
	protected mxGraphComponent graphComponent;

	// contains data from eMoflon model
	protected DataLoader dataLoader;

	// describes max size of the graph
	protected Rectangle shellBounds;

	protected int defaultNodeWidth = 80;
	protected int defaultNodeHeight = 40;
	protected Point2D.Double defaultNodePosition;

	// primitive layout algorithm
	private CustomFastOrganicLayout preLayout;

	// layout grid
	private Grid grid;

	// transformation variables
	private double graphCenterX;
	private double graphCenterY;
	private double xStretch;
	private double yStretch;

	// margin to GUI border
	private double margin = 0.1;

	// spacing around placed nodes and labels
	private int minNodeDistanceNodes = 15;

	public Visualizer(Shell shell, DataLoader dataLoader) {
		this.dataLoader = dataLoader;
		this.shell = shell;
	}

	/*
	 * First, create a new frame and add it to the shell Second, create a mxGraph
	 * from instanceModel and add it to the frame created previously
	 */
	public void init() {

		this.dataLoader.loadData();

		composite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		composite.setVisible(true);
		frame = SWT_AWT.new_Frame(composite);

		graph = new mxGraph();
		graphModel = ((mxGraphModel) graph.getModel());

		shellBounds = shell.getBounds();

		// System.out.println("Monitor bounds:" + monitorBounds.toString());

		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.4 - defaultNodeWidth * 0.5,
				((double) shellBounds.height) * 0.4 - defaultNodeHeight * 0.5);
		// defaultNodePosition = new Point2D.Double(shellBounds.width,
		// shellBounds.height);
		// System.out.println("Shell bounds:" + shell.getBounds().toString());
		// System.out.println("Default Position:" + defaultNodePosition.toString());

		addStyles();
		insertDataIntoGraph();
		setUpLayout();
		runLayout();

		graphComponent = new mxGraphComponent(graph);
		frame.add(graphComponent);
	}

	protected void addStyles() {

		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> cellStyle = new Hashtable<String, Object>();
		cellStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		cellStyle.put(mxConstants.STYLE_OPACITY, 50);
		cellStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		cellStyle.put(mxConstants.STYLE_FONTSIZE, "13");
		cellStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		cellStyle.put(mxConstants.ALIGN_CENTER, "1");
		cellStyle.put(mxConstants.STYLE_OVERFLOW, "hidden");
		stylesheet.putCellStyle("defaultNode", cellStyle);

		Hashtable<String, Object> edgeStyle = new Hashtable<String, Object>();
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		edgeStyle.put(mxConstants.STYLE_FONTSIZE, "10");
		edgeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		edgeStyle.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyle.put(mxConstants.STYLE_OVERFLOW, "fill");
		stylesheet.putCellStyle("defaultEdges", edgeStyle);

	}

	protected void insertNodeIntoGraph(Node node, double x, double y) {

		graph.insertVertex(graph.getDefaultParent(), String.valueOf(node.hashCode()), node, x, y, defaultNodeWidth,
				defaultNodeHeight, node.styleCategory);

	}

	protected void insertNodeIntoGraph(Node node) {

		double centerX = graph.getGraphBounds().getCenterX();
		double centerY = graph.getGraphBounds().getCenterY();

		insertNodeIntoGraph(node, defaultNodePosition.x - centerX, defaultNodePosition.y - centerY);
		dataLoader.nodes.add(node);
	}
	
	protected void insertNewNodeIntoGraph(Node node, double x, double y) {
		dataLoader.nodes.add(node);
		graph.insertVertex(graph.getDefaultParent(), String.valueOf(node.hashCode()), node, x, y, defaultNodeWidth,
				defaultNodeHeight, node.styleCategory);
	}

	protected void insertEdgeIntoGraph(Edge edge) {
		graph.insertEdge(graph.getDefaultParent(), String.valueOf(edge.hashCode()), edge,
				graphModel.getCell(edge.getSourceID()), graphModel.getCell(edge.getTargetID()), edge.styleCategory);
		dataLoader.edges.put(edge.hashCode(), edge);
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

		Object[] cells = new Object[] { graphModel.getCell(hashCode) };

		graph.removeCells(cells);
	}

	protected void insertDataIntoGraph() {

		for (Node n : dataLoader.nodes) {
			insertNodeIntoGraph(n);
		}

		for (Edge edge : dataLoader.edges.values()) {
			insertEdgeIntoGraph(edge);
		}

	}

	protected void setUpLayout() {

		preLayout = new CustomFastOrganicLayout(graph);
		preLayout.setForceConstant(150);
		preLayout.setMinDistanceLimit(8);
		preLayout.setUseInputOrigin(false);
		preLayout.setDisableEdgeStyle(false);

		if(shellBounds == null)
			shellBounds = shell.getBounds();

		// Initialize grid with size of the shell and a point distance of 1
		grid = new Grid(shellBounds, (int) Math.floor((1 - margin) * shellBounds.width),
				(int) Math.floor((1 - margin) * shellBounds.height), minNodeDistanceNodes);
	}

	private void updateGraphShellTransformation() {

		// Graph bounds will be strechted to monitor bounds. Set up the transformation
		// of element coordinates accordingly.
		double graphWidth = graph.getGraphBounds().getWidth();
		double graphHeight = graph.getGraphBounds().getHeight();

		graphCenterX = graph.getGraphBounds().getCenterX();
		graphCenterY = graph.getGraphBounds().getCenterY();

		xStretch = (shellBounds.width * (1 - grid.margin)) / graphWidth;
		yStretch = (shellBounds.height * (1 - grid.margin)) / graphHeight;
	}

	protected void runLayout() {

		preLayout.execute(graph.getDefaultParent());

		System.out.println("Build graph");
		//System.out.println("Placing nodes");
//		placeNodes();
//		//System.out.println("Routing edges");
//		placeEdges();
//		//System.out.println("Placing edge labels");
//		placeLabels();
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

		Map<String, Object> cells = graphModel.getCells();
		for (Object cell : cells.values()) {

			mxCell c = (mxCell) cell;

			if (c.isEdge()) {
				if(c.getValue() instanceof Edge edge) {
						edge.ignored = ignored;
					continue;
				}
			}

			if (c.isVertex()) {
				((Node) c.getValue()).ignored = ignored;
				continue;
			}

		}

	}

	private void transformGraphPositionToVisBounds(Point2D.Double position) {

		position.x = Math.max((position.x - graphCenterX) * xStretch + defaultNodePosition.x, 0);
		position.y = Math.max((position.y - graphCenterY) * yStretch + defaultNodePosition.y, 0);

	}

	private void placeNodes() {

		updateGraphShellTransformation();

		Map<String, Object> cells = graphModel.getCells();

		for (Object cell : cells.values()) {

			mxCell c = (mxCell) cell;

			mxGeometry geom = c.getGeometry();

			if (geom == null || c.isEdge())
				continue;
			if (((Node) c.getValue()).ignored)
				continue;

			Point2D.Double position = new Point2D.Double(geom.getX(), geom.getY());

			transformGraphPositionToVisBounds(position);

			// get free grid point with minimal collisions to other elements near the origin
			// that was proposed by the fastOrganic layout.
			mxPoint nearestGridPoint = grid.getFreeGridPosition(position, c.getGeometry().getRectangle());

			if (nearestGridPoint == null) {
				// keep original guess
				nearestGridPoint = new mxPoint(position.x, position.y);
			}

			// move cell to calculated origin
			Object[] cellsToMove = { cell };
			graph.moveCells(cellsToMove, nearestGridPoint.getX() - geom.getX(), nearestGridPoint.getY() - geom.getY());

			// block grid
			grid.setGridValues(c.getGeometry().getRectangle(), Double.MAX_VALUE);

			((Node) c.getValue()).ignored = true;

		}

	}

	private void placeEdges() {

		// Already plotted Edges
		HashMap<Edge, LinkedList<mxPoint>> plottedEdges = new HashMap<Edge, LinkedList<mxPoint>>();

		Map<String, Object> cells = graphModel.getCells();

		// Multithreading overhead
		ArrayList<EdgePlannerThread> activeThreads = new ArrayList<EdgePlannerThread>();
		LinkedList<Area> activeAreas = new LinkedList<Area>();
		SynchronizedWorkingAreas synAreas = new SynchronizedWorkingAreas(activeAreas);

		for (Object cell : cells.values()) {

			mxCell node = (mxCell) cell;

			if (node.isEdge())
				continue;

			if ((Node) node.getValue() == null)
				continue;

			// Start a new thread for every node with outgoing edges
			EdgePlannerThread newThread = new EdgePlannerThread(this, graphModel, grid, node, plottedEdges, synAreas);

			newThread.start();
			activeThreads.add(newThread);

		}

		for (EdgePlannerThread t : activeThreads) {

			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void placeLabels() {

		Map<String, Object> cells = graphModel.getCells();

		LabelPlanner lP = new LabelPlanner(grid);

		for (Object cell : cells.values()) {

			mxCell c = (mxCell) cell;

			if (!c.isEdge())
				continue;
			if (((Edge) c.getValue()).ignored)
				continue;

			mxGeometry geometry = graphModel.getGeometry(c);

			if (geometry == null) {
				geometry = new mxGeometry();
				geometry.setRelative(true);
			} else {
				geometry = (mxGeometry) geometry.clone();
			}

			mxCellState edgeState = graph.getView().getState(c);
			// extract corresponding edge (viapoints)
			List<mxPoint> edgePath = edgeState.getAbsolutePoints();

			mxRectangle labelBounds = edgeState.getLabelBounds();
			mxPoint pos = lP.planLabel(labelBounds, edgePath, edgeState.getLabel());

			labelBounds.setX(pos.getX());
			labelBounds.setY(pos.getY());

			// block area in grid
			edgeState.setLabelBounds(labelBounds);
			grid.setGridValues(labelBounds.getRectangle(), Double.MAX_VALUE);

		}

	}
	
	public DataLoader getDataLoader() {
		return dataLoader;
	}

	public mxGraph getGraph() {
		return graph;
	}

}
