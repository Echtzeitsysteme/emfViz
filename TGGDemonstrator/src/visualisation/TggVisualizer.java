package visualisation;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.graphics.Rectangle;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxGraphModel.mxChildChange;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;

import graphVisualization.DataLoader;
import graphVisualization.Edge;
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
		graph.setAllowDanglingEdges(false);
		/*graph.getModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
			System.out.println(evt.getName() + " ---- " + evt);
			var changes = evt.getProperty("changes");
			if(changes instanceof Iterable<?> it) {
				for(var change : it) {
					if(change instanceof mxChildChange childChange) {
						var child = childChange.getChild();
						if(child instanceof mxCell cell) {
							if(cell.isEdge()) {
								if(!(cell.getValue() instanceof Edge))
									System.out.println("Detected new edge without value: " + cell);
								else {
									System.out.println("Detected new edge: " + cell);
								}
							}
						}
					}
				}				
			}
		});*/
		frame.add(graphComponent);
	}

	public Frame getFrame() {
		return frame;
	}
	public mxGraphComponent getGraphComponent() {
		return graphComponent;
	}

}
