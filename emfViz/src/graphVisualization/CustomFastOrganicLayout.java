package graphVisualization;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class CustomFastOrganicLayout extends mxFastOrganicLayout{
	
	public CustomFastOrganicLayout(mxGraph graph)
	{
		super(graph);
	}
	
	@Override
	public boolean isVertexIgnored(Object vertex) {
		var ignored = super.isVertexIgnored(vertex);
		if(vertex instanceof mxCell cell) {
			var value = cell.getValue();
			if(value instanceof Node node)
				ignored = ignored || node.ignored;
		}
		return ignored;
	}

	@Override
	public void execute(Object parent)
	{
		super.execute(parent);
	}
}
