package tggDemonstrator;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import visualisation.CallbackHandler;

public abstract class ModelLoaderThread extends Thread {
	
	protected CallbackHandler callbackHandler;
	protected Set<ITGGMatch> matches = new HashSet<> ();
	protected boolean restart = false;
	protected String translateButtonTitle = "Start Translation";
	protected final static Logger logger = Logger.getLogger(TGGDemonstrator.class);
	
	public ModelLoaderThread() {
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run() {
		initialize();
		
		while (true) {
			
			callbackHandler.setButtonTitle("Start Translation Process"); //process 
			callbackHandler.setTranslationIsDone(false);

			try {
				//Thread sleeps until translate button with title "Start Translation" is pressed for the first time
				sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				
				callbackHandler.setButtonTitle(translateButtonTitle);
				
				startProcess();
				
				//no more matches -> return empty HashSet 
				matches = new HashSet<> ();
				callbackHandler.setMatches(matches);
				
				callbackHandler.setButtonTitle("Start Translation Process");
				
				callbackHandler.updateGraph();
				
				callbackHandler.getTGGDemonstratorInstance(null).removeGraphHighlighting(callbackHandler.getTGGDemonstratorInstance(null).getDisplayHandler().getTrgTggVisualizer());
				callbackHandler.getTGGDemonstratorInstance(null).removeGraphHighlighting(callbackHandler.getTGGDemonstratorInstance(null).getDisplayHandler().getSrcTggVisualizer());
			}
		}
	}
	
	/*
	 * Overrides method chooseOneMatch
	 */
	protected abstract void initialize();
	
	/*
	 * Initiates translation process (MODELGEN, INITIAL_FWD, INITIAL_BWD, SYNC)
	 */
	protected abstract void startProcess();	
	
	
	/*
	 * Wakes up the thread and continues the translation process
	 */
	public void wakeUp() {
		logger.info("Hey thread " + getId() + " wake up!");
		
		interrupt();
	}
}
