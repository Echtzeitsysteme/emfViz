package tggDemonstrator;

import java.util.HashSet;
import java.util.Set;

import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;

import visualisation.CallbackHandler;

public abstract class ModelLoaderThread extends Thread {
	protected CallbackHandler callbackHandler;
	protected Set<ITGGMatch> matches = new HashSet<> ();
	
	public ModelLoaderThread() {
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run() {
		initialize();	
		startProcess();

		while (true) {}
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
