package visualisation;

import java.awt.Frame;
import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Rectangle;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import graphVisualization.DataLoader;
import graphVisualization.Visualizer;

public class TggVisualizer extends Visualizer{

	
	public TggVisualizer(DataLoader dataLoader, Frame frame, Rectangle r) {
		super(null, dataLoader);
		
		shellBounds = r;
		this.frame = frame;	
	}
	
	/*
	 * create a mxGraph from instanceModel and add this graph to a given frame
	 */
	
	@Override
	public void init () {
		
		dataLoader.loadData();
		
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
