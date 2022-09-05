package visualisation;


import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.Node;
import graphVisualization.VisContentAdapter;
import graphVisualization.Visualizer;
import language.TGG;
import language.TGGRule;
import language.TGGRuleEdge;
import language.TGGRuleNode;
import tggDemonstrator.TGGDemonstrator;

public class CallbackHandler {
	
	
	private static CallbackHandler instance;
	
	public enum UpdateGraphType { ALL, SRC, TRG}
	private Set<ITGGMatch> matches = new HashSet<> ();
	private ITGGMatch selectedMatch;
	private Combo combo = null;
	private VisContentAdapter srcContentAdapter = null;
	private VisContentAdapter trgContentAdapter = null;
	
	private mxGraph graph = null;
	
	private CallbackHandler() {
	}
	
	public static CallbackHandler getInstance() {
        if (instance == null) {
            instance = new CallbackHandler();
        }
        return instance;
    }
 
	//-------------------------Model Generation ----------------------------------------
	/*
	 * This section of the CallbackHandler provides functions 
	 * to keep the visualization (select next step) and the model generation (execute selected step) in sync 
	 */
	
	
	public void setMatches(Set<ITGGMatch> matches) {
		this.matches = matches;
		
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	if (combo != null && !matches.isEmpty()) {
		    			
		    		String[] matchesTemp = new String[matches.size()];
		    		
		    		int index = 0;
					for (ITGGMatch match : matches) {
		    			matchesTemp[index] = match.getRuleName();
		    			index++;
		    		}
					
					combo.setItems(matchesTemp);
					combo.select(0);
				}
		    }
		});
	}
	
	public void setSelectedMatch(int selectionNo) {
		
		int indexList = 0;
		
		for (ITGGMatch match : matches) {
			if (indexList == selectionNo) {
				selectedMatch = match;
				return;
			}
			indexList++;
		}
		
		selectedMatch = null;
	}
	
	/**
	 * 
	 * @param match
	 */
	public void setSelectedMatch(ITGGMatch match) {
		
		if (match != null)
			selectedMatch = match;

	}
	
	public Combo registerComboBox(Combo combo) {
		
		if(this.combo == null) {
			this.combo = combo;
		}
		
		if(!matches.isEmpty()) {
			String[] matchesTemp = new String[matches.size()];
    		
    		int index = 0;
			for (ITGGMatch match : matches) {
    			matchesTemp[index] = match.getRuleName();
    			index++;
    		}
    		
			combo.setItems(matchesTemp);
			combo.select(0);
		}
		
		return this.combo;
	}
	
	public ITGGMatch getSelectedMatch() {
		return selectedMatch;
	}
	
	//-------------------------- VisContentAdapter -------------------------------	
	/*
	 * This section of the CallbackHandler provides functions 
	 * to connect source and target resource with visualization to keep both in sync 
	 */
	
	
	/*
	 * connect source resource with visualization to keep both in sync
	 */
	public VisContentAdapter registerSourceContentAdapter(Resource r, Visualizer vis) {
		if (srcContentAdapter == null) {
			srcContentAdapter = new VisContentAdapter(r, vis);
		}
		return srcContentAdapter;
	}
	
	public VisContentAdapter registerNewSourceContentAdapter(Resource r, Visualizer vis) {
		srcContentAdapter = new VisContentAdapter(r, vis);
		return srcContentAdapter;
	}
	
	/*
	 * connect target resource with visualization to keep both in sync
	 */
	public VisContentAdapter registerTargetContentAdapter(Resource r, Visualizer vis) {
		if (trgContentAdapter == null) {
			trgContentAdapter = new VisContentAdapter(r, vis);
		}
		return trgContentAdapter;
	}
	
	public VisContentAdapter registerNewTargetContentAdapter(Resource r, Visualizer vis) {
		trgContentAdapter = new VisContentAdapter(r, vis);
		return trgContentAdapter;
	}
	
	/*
	 * Update graph visualization
	 */
	public void updateGraph(UpdateGraphType type) {
		System.out.println("----- UPDATE GRAPH -----");
		
		switch (type) {
		case ALL:
			if(srcContentAdapter != null)
				srcContentAdapter.processNotifications();
			if(trgContentAdapter != null)
				trgContentAdapter.processNotifications();
			break;
		case SRC:
			if(srcContentAdapter != null)
				srcContentAdapter.processNotifications();
			break;
		case TRG:
			if(trgContentAdapter != null)
				trgContentAdapter.processNotifications();
			break;
		default:
			break;
		}
	}
	
	public void setPositionForNewNode(int x, int y) {
		srcContentAdapter.setPosition(x,y);
		trgContentAdapter.setPosition(x,y);
	}
	
}
