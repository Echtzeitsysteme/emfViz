package tggDemonstrator;


import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.strategies.modules.IbexExecutable;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import graphVisualization.Edge;
import graphVisualization.Node;
import language.TGGRule;
import language.TGGRuleEdge;
import language.TGGRuleNode;
import visualisation.CallbackHandler;
import visualisation.DisplayHandler;
import visualisation.TggVisualizer;
import visualisation.UserControlArea;

import org.emoflon.ibex.common.emf.EMFEdge;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

public abstract class TGGDemonstrator implements UserControlArea{
	
	
	public enum LoadingOption {Default, NewModel, SelectedResource};
	protected LoadingOption loadingOption;
		
	protected IbexOptions options;
	protected TGGResourceHandler resourceHandler;
	protected Resource source;
	protected Resource target;
	
	protected String projectPath;
	protected String workspacePath;
	
	protected DisplayHandler graphVisualizer;
	
	protected CallbackHandler callbackHandler;
	
	/*
	 * Class constructor
	 * @param	pP	directory of the project
	 * @param	wP	directory of the workspace
	 */
	public TGGDemonstrator (String pP, String wP) {
		projectPath = pP;
		workspacePath = wP;
		
		callbackHandler = CallbackHandler.getInstance();
		
		callbackHandler.getTGGDemonstratorInstance(this);
	}
	
	/*
	 * Start the visualization and initialize the UI
	 */
	public void startVisualisation(TGGDemonstrator modelLoader) {
		graphVisualizer = new DisplayHandler(modelLoader);
		graphVisualizer.run();
	}	
	

	//--------------------- Abstract Methods --------------------------- 
	
	/*
	 * Generates a new Model
	 * this method should works only if the executable is from type MODELGEN 
	 */
	public abstract void generateNewModel();
	
	/*
	 * Perform loadModels operation from default locations: /instances/src.xmi  /instances/trg.xmi
	 */
	public abstract void loadFromDefault();
	
	/*
	 * Load source and target model from a given path
	 */
	public abstract void createResourcesFromPath(String pathSrc, String pathTrg,  String pathCorr, String pathProtocol, String pathProject);
	
	/*
	 * Save Models
	 */
	public abstract void saveModels();
	
	/*
	 * Starts the ModelLoader in a new thread
	 */
	protected abstract void initThread();
	
	/*
	 * Method called from external to highlight the graph
	 * Implement it in that way that the highlightGraph method is called
	 * 		- Created: Green
	 * 		- Context: Black
	 */
	public abstract void highlightGraph(TggVisualizer visSrc, TggVisualizer visTrg);
	
	/*
	 * Highlights node and edge in source and target graph:
	 * 		- Created: Green
	 * 		- Context: Black
	 */
	protected void highlightingGraphAlgorithm (TggVisualizer visSrc, TggVisualizer visTrg) {
		//check whether a match for highlighting exists or not
		ITGGMatch match = callbackHandler.getSelectedMatch();
		if (match == null)
			return;
		
		//Initialize source and target graph
		mxGraph graphSrc = visSrc.getGraph();	
		mxCell rootSrc = (mxCell)graphSrc.getModel().getRoot();
		
		mxGraph graphTrg = visTrg.getGraph();	
		mxCell rootTrg = (mxCell)graphTrg.getModel().getRoot();
		
		addStyles(graphSrc);
		addStyles(graphTrg);
		
		List<mxICell> updateCellsCreate = new LinkedList<mxICell>();
		List<mxICell> updateCellsContext = new LinkedList<mxICell>();
		List<mxICell> updateEdgeCreate = new LinkedList<mxICell>();
		List<mxICell> updateEdgeContext = new LinkedList<mxICell>();
		
		//First reset highlighted cells
		resetHighlightedCells(graphSrc);
		resetHighlightedCells(graphTrg);
		
		// Find selected rule
		TGGRule myRule = null;
		for (TGGRule rule : options.tgg.flattenedTGG().getRules()) {
			
			if (rule.getName().equals(match.getRuleName())) {	
					myRule = rule;
					break;
			}	
		}
		
		// Will only be continued if the rule was found
		if (myRule == null)
			return;
		
		// Match nodes from rule with nodes in selected match
		@SuppressWarnings("restriction")
		EList<TGGRuleNode> nodes = myRule.getNodes();
		HashMap<Node, String> createdNodes = new HashMap<Node, String>(); 
		
		for (TGGRuleNode n : nodes) {
			
			EObject myNode = (EObject)match.get(n.getName());
			
			if (myNode == null)
				continue;
			Node newNode = new Node(myNode);
			createdNodes.put(newNode, n.getBindingType().getLiteral());
		}
		
		
		// get edges
		@SuppressWarnings("restriction")
		EList<TGGRuleEdge> edges = myRule.getEdges();
		HashMap<Edge, String> createdEdges = new HashMap<Edge, String>(); 
		
		for (TGGRuleEdge e : edges) {
			TGGRuleEdge myEdge = e;
			//TGGRuleNode src = myEdge.getSrcNode();
			EObject src = (EObject)match.get(myEdge.getSrcNode().getName());
			//TGGRuleNode trg = myEdge.getTrgNode();
			EObject trg = (EObject)match.get(myEdge.getTrgNode().getName());
			EReference ref = myEdge.getType();
			
			if (src == null || trg == null || ref == null)
				continue;
			
			Edge newEdge = new Edge(src,trg,ref);	
			createdEdges.put(newEdge, e.getBindingType().getLiteral());
		}
		
		// match maxCell and eObject in src graph
		for (int i = 0; i < rootSrc.getChildAt(0).getChildCount(); i++) {
			mxICell myCellSrc = rootSrc.getChildAt(0).getChildAt(i);
			
			//Match mxCell to Node and assign it to update array
			if (myCellSrc.isVertex()) {
				
				Node refN = (Node)myCellSrc.getValue();
				
				if (refN != null) {
					
					for (Entry<Node, String> entry : createdNodes.entrySet()){
						
						Node key = entry.getKey();
						
						if (key.equals(refN)) {
							if (entry.getValue().equals("CONTEXT")) {
								updateCellsContext.add(myCellSrc);
							} else if(entry.getValue().equals("CREATE")) {
								updateCellsCreate.add(myCellSrc);
							}
						}
					}
				}
			}
			
			//Match mxCell to Edge and assign it to update array
			if (myCellSrc.isEdge()) {
				
				//just a short fix for a ClassPathException - because sometimes the value of getValue() is a String (makes no sense)
				if (!(myCellSrc.getValue() instanceof Edge))
					continue;
				
				Edge refE = (Edge)myCellSrc.getValue();
				
				if (refE == null)
					return;
				
				for (Entry<Edge, String> entry : createdEdges.entrySet()){
					Edge key = entry.getKey();
					
					if (key.equals(refE)) {
						if (entry.getValue().equals("CONTEXT")) {
							updateEdgeContext.add(myCellSrc);
						} else if(entry.getValue().equals("CREATE")) {
							updateEdgeCreate.add(myCellSrc);
						}
					}
				}
			}
		}
		
		//update cells
		graphSrc.setCellStyle("CreateNode", updateCellsCreate.toArray());
		graphSrc.setCellStyle("ContextNode", updateCellsContext.toArray());
		
		graphSrc.setCellStyle("CreateEdge", updateEdgeCreate.toArray());
		graphSrc.setCellStyle("ContextEdge", updateEdgeContext.toArray());
		
		//remove all objects to fill it with new objects
		updateCellsCreate.clear();
		updateCellsContext.clear();
		
		updateEdgeCreate.clear();
		updateEdgeContext.clear();
		
		// match maxCell and eObject in trg graph
		for (int i = 0; i < rootTrg.getChildAt(0).getChildCount(); i++) {
			mxICell myCellTrg = rootTrg.getChildAt(0).getChildAt(i);
			
			//Match mxCell to Node and assign it to update array
			if (myCellTrg.isVertex()) {
			
				Node refN = (Node)myCellTrg.getValue();
				
				if (refN != null) {
					
					for (Entry<Node, String> entry : createdNodes.entrySet()){
						
						Node key = entry.getKey();
						
						if (key.equals(refN)) {
							if (entry.getValue().equals("CONTEXT")) {
								updateCellsContext.add(myCellTrg);
							} else if(entry.getValue().equals("CREATE")) {
								updateCellsCreate.add(myCellTrg);
							}
						}
					}
				}
			}
			
			//Match mxCell to Edge and assign it to update array
			if (myCellTrg.isEdge()) {
				
				if (!(myCellTrg.getValue() instanceof Edge))
					continue;
				
				Edge refE = (Edge)myCellTrg.getValue();
				
				if (refE == null)
					return;

				for (Entry<Edge, String> entry : createdEdges.entrySet()){
					Edge key = entry.getKey();
					
					if (key.equals(refE)) {
						if (entry.getValue().equals("CONTEXT")) {
							updateEdgeContext.add(myCellTrg);
						} else if(entry.getValue().equals("CREATE")) {
							updateEdgeCreate.add(myCellTrg);
						}
					}
				}	
			}
		}
		
		//update cells
		graphTrg.setCellStyle("CreateNode", updateCellsCreate.toArray());
		graphTrg.setCellStyle("ContextNode", updateCellsContext.toArray());
		
		graphTrg.setCellStyle("CreateEdge", updateEdgeCreate.toArray());
		graphTrg.setCellStyle("ContextEdge", updateEdgeContext.toArray());
	}
	
	/*
	 * Removes the highlighting of the referred graph.
	 * Just calls the resetHighlightedCells method.
	 * It represents a public method to reset the graph layout.
	 */
	public void removeGraphHighlighting(TggVisualizer vis) {
		resetHighlightedCells(vis.getGraph());
	}
	
	/*
	 * Reset layout of all cells to their original one
	 */
	private void resetHighlightedCells(mxGraph graph) {
		List<mxICell> resetTempE = new LinkedList<mxICell>();
		List<mxICell> resetTempN = new LinkedList<mxICell>();
		mxCell root = (mxCell)graph.getModel().getRoot();
		
		for (int i = 0; i < root.getChildAt(0).getChildCount(); i++) {
			mxICell myCell = root.getChildAt(0).getChildAt(i);
			
			if (myCell.getStyle().equals("defaultNode") || myCell.getStyle().equals("defaultEdges") )
				continue;
			
			if (myCell.isEdge())
				resetTempE.add(myCell);
			
			if(myCell.isVertex())
				resetTempN.add(myCell);
				
		}
		
		graph.setCellStyle("defaultEdges", resetTempE.toArray());
		graph.setCellStyle("defaultNode", resetTempN.toArray());
	}
	
	/*
	 * Creates two different highlighting styles (create and context) for edges and nodes
	 */
	protected void addStyles(mxGraph graph) {
		
		mxStylesheet stylesheetCreated = graph.getStylesheet();
		Hashtable<String, Object> cellStyleCreate = new Hashtable<String, Object>();
		cellStyleCreate.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		cellStyleCreate.put(mxConstants.STYLE_OPACITY, 90);
		cellStyleCreate.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		cellStyleCreate.put(mxConstants.STYLE_FONTSIZE, "13");
		cellStyleCreate.put(mxConstants.STYLE_FILLCOLOR, "#76b32a");
		cellStyleCreate.put(mxConstants.ALIGN_CENTER, "1");
		cellStyleCreate.put(mxConstants.STYLE_OVERFLOW, "hidden");
		stylesheetCreated.putCellStyle("CreateNode", cellStyleCreate); // Create
		
		mxStylesheet stylesheetContex = graph.getStylesheet();
		Hashtable<String, Object> cellStyleContex = new Hashtable<String, Object>();
		cellStyleContex.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		cellStyleContex.put(mxConstants.STYLE_OPACITY, 90);
		cellStyleContex.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		cellStyleContex.put(mxConstants.STYLE_FONTSIZE, "13");
		cellStyleContex.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		cellStyleContex.put(mxConstants.ALIGN_CENTER, "1");
		cellStyleContex.put(mxConstants.STYLE_OVERFLOW, "hidden");
		cellStyleContex.put(mxConstants.STYLE_STROKEWIDTH, "3");
		cellStyleContex.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheetContex.putCellStyle("ContextNode", cellStyleContex);
		
		mxStylesheet stylesheetEContex = graph.getStylesheet();
		Hashtable<String, Object> edgeStyleCreate = new Hashtable<String, Object>();
		edgeStyleCreate.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		edgeStyleCreate.put(mxConstants.STYLE_OPACITY, 90);
		edgeStyleCreate.put(mxConstants.STYLE_FONTSIZE, "10");
		edgeStyleCreate.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		edgeStyleCreate.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyleCreate.put(mxConstants.STYLE_OVERFLOW, "fill");
		edgeStyleCreate.put(mxConstants.STYLE_STROKEWIDTH, "2");
		edgeStyleCreate.put(mxConstants.STYLE_STROKECOLOR, "#76b32a");
		stylesheetEContex.putCellStyle("CreateEdge", edgeStyleCreate);
		
		mxStylesheet stylesheetECreate = graph.getStylesheet();
		Hashtable<String, Object> edgeStyleContex = new Hashtable<String, Object>();
		edgeStyleContex.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		edgeStyleContex.put(mxConstants.STYLE_OPACITY, 90);
		edgeStyleContex.put(mxConstants.STYLE_FONTSIZE, "10");
		edgeStyleContex.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		edgeStyleContex.put(mxConstants.ALIGN_LEFT, "1");
		edgeStyleContex.put(mxConstants.STYLE_OVERFLOW, "fill");
		edgeStyleContex.put(mxConstants.STYLE_STROKEWIDTH, "2");
		edgeStyleContex.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheetECreate.putCellStyle("ContextEdge", edgeStyleContex);
	}
	
	
	//implemented methods from interface
	
	@Override
	public String buttonTranslateTxt() {
		return "Start Translation Process";
	}
	
	@Override
	public Combo createComboBox(Group g) {
		return new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
	}
	
	
	//--------------------- Setter & Getter Methods --------------------- 
	
	
	/*
	 * Set source resource
	 */	
	public void setSource(Resource source) {
		this.source = source;
	}
	
	/*
	 * Return source resource
	 */
	public Resource getSource(){
		return source;
	}
	
	/*
	 * Set target resource
	 */
	public void setTarget(Resource target) {
		this.target = target;
	}
	/*
	 * Returns target resource
	 */
	public Resource getTarget() {
		return target;
	}
	/*
	 * Returns IbexExecutable
	 */
	public IbexExecutable getExectuable() {
		return options.executable();
	}
	
	/*
	 * Returns IbexOptions
	 */
	public IbexOptions getOptions() {
		return options;
	}
	
	/*
	 * Returns TGGResourceHandler instance
	 */
	public TGGResourceHandler getResourceHandler() {
		return resourceHandler;
	}
	
	/*
	 * Returns value of loadingOption
	 */
	public LoadingOption getLoadingOption() {
		return loadingOption;
	}
	
	/*
	 * Returns DisplayHandler
	 */
	public DisplayHandler getDisplayHandler() {
		return graphVisualizer;
	}
}


