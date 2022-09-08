package tggDemonstrator;

import java.util.HashSet;
import java.util.Set;

import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import visualisation.CallbackHandler;

public abstract class ModelLoaderThread extends Thread {
	
	protected CallbackHandler callbackHandler;
	protected Set<ITGGMatch> matches = new HashSet<> ();
	protected boolean restart = false;
	protected String translateButtonTitleTmp;
	protected String translateButtonTitle = "Next Step";
	
	public ModelLoaderThread() {
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run() {
		while (true) {
			translateButtonTitleTmp = translateButtonTitle;
			
			initialize();	
			startProcess();
			
			matches = new HashSet<> ();
			callbackHandler.setMatches(matches);
			
			translateButtonTitleTmp = "Start Translation";
			
			try {
				sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				System.out.println("Thread " + getId() + " restarts");
			}
		}
		
		//System.out.println("Thread " + getId() + " is not alive anymore!");
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
		System.out.println("Hey thread " + getId() + " wake up!");
		
		interrupt();
	}
	
	public String getNewTranslateButtonTitle() {
		return translateButtonTitleTmp;
	}
}
