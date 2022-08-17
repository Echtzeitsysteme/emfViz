package graphVisualization;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class customFastOrganicLayout extends mxFastOrganicLayout{
	
	public customFastOrganicLayout(mxGraph graph)
	{
		super(graph);
	}
	
	@Override
	public boolean isVertexIgnored(Object vertex) {
		
		return super.isVertexIgnored(vertex) || ((Node)((mxCell)vertex).getValue()).ignored;
	}

	@Override
	public void execute(Object parent)
	{
		super.execute(parent);
	}
}