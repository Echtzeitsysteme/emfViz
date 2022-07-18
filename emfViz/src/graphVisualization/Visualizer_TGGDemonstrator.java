package graphVisualization;

import java.awt.Frame;
import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Rectangle;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;


public class Visualizer_TGGDemonstrator extends Visualizer {

private Frame frame;
	
	
	public Visualizer_TGGDemonstrator(DataLoader dataLoader, Frame frame, Rectangle r) {
		super(dataLoader, null);
	
		shellBounds = r;
		this.frame = frame;		
		
	}
	
	/*
	 * create a mxGraph from instanceModel and add this graph to a given frame
	 */
	
	@Override
	public void init () {
		
		this.dataLoader.loadData();
		
		defaultNodePosition = new Point2D.Double(((double) shellBounds.width) * 0.5 - defaultNodeWidth * 0.5 , ((double) shellBounds.height) * 0.5 - defaultNodeHeight * 0.5);
		
		graph = new mxGraph();
		graphModel = ((mxGraphModel)graph.getModel());
		
		addStyles();
		insertDataIntoGraph();
		setUpLayout();
		runLayout();
		
		graphComponent = new mxGraphComponent(graph);
		frame.add(graphComponent);
	}
}
