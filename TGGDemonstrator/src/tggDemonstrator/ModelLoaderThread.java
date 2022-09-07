package tggDemonstrator;

import java.util.HashSet;
import java.util.Set;

import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import visualisation.CallbackHandler;

public abstract class ModelLoaderThread extends Thread {
	
	protected CallbackHandler callbackHandler;
	protected Set<ITGGMatch> matches = new HashSet<> ();
	protected boolean restart = false;
	
	public ModelLoaderThread() {
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run() {
		while (true) {
			initialize();	
			startProcess();
			
			matches = new HashSet<> ();
			callbackHandler.setMatches(matches);
			
			try {
				sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				System.out.println("Thread " + getId() + " restarts");
			}
		}
		
		//System.out.println("Thread " + getId() + " is not alive anymore!");
	}
	
	/*
	 * Wakes up the thread and continues the translation process
	 */
	public void wakeUp() {
		System.out.println("Hey thread " + getId() + " wake up!");
		
		interrupt();
	}
	
	/*
	 * Overrides method chooseOneMatch
	 */
	protected abstract void initialize();
	
	/*
	 * Initiates translation process (MODELGEN, INITIAL_FWD, INITIAL_BWD, SYNC)
	 */
	protected abstract void startProcess();	
	
	
}
