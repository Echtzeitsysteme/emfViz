package visualisation;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import graphVisualization.VisContentAdapter;
import graphVisualization.Visualizer;

public class CallbackHandler {
	
	
	private static CallbackHandler instance;
	
	private Set<ITGGMatch> matches = new HashSet<> ();
	private ITGGMatch selectedMatch;
	private Combo combo = null;
	private Button button = null;
	private VisContentAdapter srcContentAdapter = null;
	private VisContentAdapter trgContentAdapter = null;
	private String lastProcessedGraph = "SRC";
	
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
					combo.setEnabled(true);
					
				}else if(combo != null && matches.isEmpty()) {
					String[] matchesTemp = new String[1];
					matchesTemp[0] = "";
					
					combo.setItems(matchesTemp);
					combo.select(0);
					combo.setEnabled(false);
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
	
	/*
	 * Register translate model button to keep it in sync
	 */
	public Button registerTranslateButton(Button button) {
		if (this.button != null)
			return this.button;
		
		this.button = button;
		
		return this.button;
	}
	
	/*
	 * Updates title of the translate button
	 */
	public void setButtonTitle(String title) {
		if (button == null)
			return;
		
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	button.setText(title);
		    }
		});
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
	public void updateGraph() {
		System.out.println("----- UPDATE GRAPH -----");
		
		if(srcContentAdapter != null)
			srcContentAdapter.processNotifications();
		if(trgContentAdapter != null)
			trgContentAdapter.processNotifications();
	}
	
	public void setPositionForNewNode(int x, int y) {
		srcContentAdapter.setPosition(x,y);
		trgContentAdapter.setPosition(x,y);
	}
	
	public void setLastProcessedGraph(String change) {
		lastProcessedGraph = change;
	}
	
	public String getLastProcessedGraph() {
		return lastProcessedGraph;
	}
	
}
