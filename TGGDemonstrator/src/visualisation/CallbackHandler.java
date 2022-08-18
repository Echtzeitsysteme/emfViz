package visualisation;


import java.util.Set;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

public class CallbackHandler {
	
	
	private static CallbackHandler instance;
	
	private Set<ITGGMatch> matches;
	private ITGGMatch selectedMatch;
	private Combo combo = null;
	
	private CallbackHandler() {
	}
	
	public static CallbackHandler getInstance() {
        if (instance == null) {
            instance = new CallbackHandler();
        }
        return instance;
    }
	
	
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
		System.out.println("Set selected match");
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
		System.out.println("Get selected match");
		return selectedMatch;
	}
	
}
