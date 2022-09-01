package graphVisualization;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;

public class EdgePlannerThread extends Thread {

	/**
	 * 
	 */
	private final Visualizer visualizer;
	private int workingAreaHeightMargin = 30;
	private int workingAreaWidthMargin = 60;

	private SynchronizedWorkingAreas activeAreas;
	private Area ownArea;
	private HashMap<Edge, LinkedList<mxPoint>> plottedEdges;

	private mxGraphModel graphModel;
	private Grid grid;
	private mxICell node;

	public EdgePlannerThread(Visualizer visualizer, mxGraphModel graphModel, Grid grid, mxICell cell,
			HashMap<Edge, LinkedList<mxPoint>> plottedEdges, SynchronizedWorkingAreas activeAreas) {

		this.visualizer = visualizer;
		if (cell.getValue() == null)
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

		for (int i = 0; i < node.getEdgeCount(); i++) {

			mxICell mxedgeInGraph = node.getEdgeAt(i);
			mxICell source = mxedgeInGraph.getTerminal(true);
			mxICell terminal = mxedgeInGraph.getTerminal(false);

			// only route outgoing edges
			if (!source.getId().toString().equals(node.getId().toString())) {
				continue;
			}

			if(mxedgeInGraph instanceof Edge edge) {
				if(edge.ignored)
					continue;
			}
			
			mxGeometry geometry = mxedgeInGraph.getGeometry();
			if (geometry == null) {
				geometry = new mxGeometry();
				geometry.setRelative(true);
			} else {
				geometry = (mxGeometry) geometry.clone();
			}

			int[] xcords = new int[8];
			int[] ycords = new int[8];

			Point sourceCenter = new Point();
			sourceCenter.x = (int) source.getGeometry().getCenterX();
			sourceCenter.y = (int) source.getGeometry().getCenterY();

			Point targetCenter = new Point();
			targetCenter.x = (int) terminal.getGeometry().getCenterX();
			targetCenter.y = (int) terminal.getGeometry().getCenterY();

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

			Polygon s = new Polygon(xcords, ycords, 8);

			// build a bounding box encompassing target and source node geometries as well
			// as the space in between them.
			ownArea = new Area(s.getBounds2D());

			boolean allocated = false;
			while (!allocated) {

				// try to rout current edge -> check if required area is currently worked on
				allocated = activeAreas.allocateWorkingArea(ownArea);

				if (allocated)
					break;

				synchronized (activeAreas) {
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

			// check if an opposite for the current edge was specified
			if (edge.ref.getEOpposite() != null) {

				Edge oppositeEdge = visualizer.getDataLoader().edges
						.get(Objects.hash(edge.target, edge.ref.getEOpposite(), edge.source));

				// if it was already plotted use the reverse of its path
				if (plottedEdges.containsKey(oppositeEdge)) {
					LinkedList<mxPoint> oppositePlot = (LinkedList<mxPoint>) plottedEdges.get(oppositeEdge).clone();
					Collections.reverse(oppositePlot);
					geometry.setPoints(oppositePlot);
					graphModel.setGeometry(mxedgeInGraph, geometry);

					activeAreas.deallocateWorkingArea(ownArea);
					continue;
				}

			}

			// use EdgePlanner class for routing
			EdgePlanner edgePlanner = new EdgePlanner(sourceCenter, targetCenter, source.getGeometry(),
					terminal.getGeometry(), grid);

			LinkedList<Point> edgePath = edgePlanner.planEdge();

			if (edgePath == null) {
				activeAreas.deallocateWorkingArea(ownArea);
				continue;
			}

			LinkedList<mxPoint> points = new LinkedList<mxPoint>();

			for (Point p : edgePath) {
				points.add(new mxPoint(p.x, p.y));
			}

			// set edge via points
			geometry.setPoints(points);
			graphModel.setGeometry(mxedgeInGraph, geometry);

			plottedEdges.put(edge, points);
			activeAreas.deallocateWorkingArea(ownArea);

		}
	}
}