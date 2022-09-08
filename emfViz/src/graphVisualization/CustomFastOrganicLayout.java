package graphVisualization;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

public class CustomFastOrganicLayout extends mxFastOrganicLayout {

	public CustomFastOrganicLayout(mxGraph graph) {
		super(graph);
	}

	@Override
	public boolean isVertexMovable(Object vertex) {
		// TODO Auto-generated method stub
		if(vertex instanceof mxCell cell) {
			if(cell.getValue() instanceof Node node) {
				return !node.ignored;
			}
		}
		return super.isVertexMovable(vertex);
	}
	
	@Override
	public void execute(Object parent) {

		super.execute(parent);
	}
}
