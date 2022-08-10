package tggDemonstrator;


import java.io.IOException;
import java.lang.Thread.State;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import tggDemonstrator.DataObject.Modelgeneration;
import visualisation.TggVisualizerDisplay;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<DataObject, MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	private String[] paths	= new String[3];
	
	private ModelgenThread thread;
	private int ruleIndex;
	private Set<ITGGMatch> matches;
	
	private TggVisualizerDisplay vd;
	
	
	public ModelLoader_MODELGEN (Function<DataObject, MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_MODELGEN");
		modelgen_demonstrator = modelgen;
		
		
		startVisualisation(this);
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
		// TODO Auto-generated method stub

		
		DataObject data = new DataObject(pathSrc, pathTrg, projectPath + "/instances/corr.xmi", projectPath + "/instances/protocol.xmi", Modelgeneration.PRE_DEFiNED_MODEL);
		
		modelgen = modelgen_demonstrator.apply(data);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.SelectedResource;
		
	}

	@Override
	public void loadFromDefault() {
		
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", projectPath + "/instances/trg.xmi", projectPath + "/instances/corr.xmi", projectPath + "/instances/protocol.xmi", Modelgeneration.PRE_DEFiNED_MODEL);
		
		modelgen = modelgen_demonstrator.apply(data);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.Default;
		
	}

	@Override
	public void generateNewModel() {
		// TODO Auto-generated method stub
		
		System.out.println("Current thread is " + Thread.currentThread().getId());
		
		
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", projectPath + "/instances/trg.xmi", projectPath + "/instances/corr.xmi", projectPath + "/instances/protocol.xmi", Modelgeneration.NEW_MODEL);
		
		modelgen = modelgen_demonstrator.apply(data);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
		
		// create empty models  -- falsches vorgehen! --> boolean im constructor im modelgen zur unterscheidung zw. load und createResource
		
		
		
		// set basic stop criterion
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
    	//stop.setMaxRuleCount("HospitaltoAdministrationRule", 1);
		//stop.setMaxElementCount(12);
		modelgen.setStopCriterion(stop);
		
		// Start new thread
		thread = new ModelgenThread(modelgen, this);
		thread.setName("Modelgen Thread");
		thread.start();
		
		System.out.println("Model generation process is running on thread " + thread.getId());
		
	}
	
	/*
	 * Interrupt sleep
	 */
	public boolean wakeUpThread() {
		
		try {
			thread.wakeUp();
		
			return true;
			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		return false;
		
	}
	
	/*
	 * Return rule names from possible matches
	 */
	public String[] getRuleNames() {
		
		matches = thread.getMatches();
		
		String[] ruleNames = new String[matches.size()];
		int i = 0;
		
		
		for (ITGGMatch m : matches) {
			ruleNames[i] = m.getRuleName();
			
			i++;
		}
		
		return ruleNames;	
	}
	
	/*
	 * Set rule index
	 */
	public void setSelectedRuleIndex(int i) {
		ruleIndex = i;
		
		thread.setRuleIndex(i);
	}
	
	/*
	 * Get rule index
	 */
	public int getSelectedRuleIndex() {
		return ruleIndex;
	}
	
	public State getModelgenThreadState() {
		return thread.getState();
	}
}



class ModelgenThread extends Thread{
	
	private MODELGEN modelgen;
	private int ruleIndex;
	
	private Set<ITGGMatch> matches = new HashSet<> ();
	private ITGGMatch selectedMatch;
	
	
	private ModelLoader_MODELGEN modelLoader;
	
	public ModelgenThread(MODELGEN m, ModelLoader_MODELGEN modelLoader) {
		this.modelgen = m;
		this.modelLoader = modelLoader;
		ruleIndex = -1;
	}
	
	@Override
	public void run(){
		
		initializeModelgenStart();
		
		runModelgeneration();
		
		/*
		while (true) {
			//do something
			System.out.println("Thread is running!");
		}
		*/
		
		while (true) {}
	}
	
	
	/*
	 * Wake up thread after sleep
	 */
	public boolean wakeUp() {
		
		System.out.println("Hey thread " + getId() + " wake up!");
		
		interrupt();
		
		return true;
	}
	
	public void initializeModelgenStart() {
		modelgen.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				

				//System.out.println("Thread sleeps again for a very long time!");
				
				ITGGMatch match = null;
				/*
				try {
					
					matches = matchContainer.getMatches();
					
					sleep(Long.MAX_VALUE);
					//sleep(1000);
					
				} catch (InterruptedException e) {
					
					
					
					
					if (ruleIndex >= 0 && matches.size() - 1 == ruleIndex) {
						
						//System.out.println(matchContainer.getMatches().size());
						
						Object[] t = matches.toArray();
						match = (ITGGMatch)t[ruleIndex];
			
						
					}else {
						//if no rule is selected then just use the next one
						match = matchContainer.getNext();
					}
					
					System.out.println("the rule " + match.getRuleName() + " will be performed");
					
					//return match;
					 
					 
				}*/
				
				//return matchContainer.getNext();
				/*
				if (ruleIndex >= 0 && matches.size() - 1 == ruleIndex) {
					
					//System.out.println(matchContainer.getMatches().size());
					
					Object[] t = matches.toArray();
					match = (ITGGMatch)t[ruleIndex];
		
					
				}else {
					//if no rule is selected then just use the next one
					match = matchContainer.getNext();
				}
				
				*/
				
				//match = matchContainer.getNext();
				
				match = getSelectedMatch();
				
				System.out.println("the rule " + match.getRuleName() + " will be performed");
				
				return match;
						
			}
		});
	}
	
	private void runModelgeneration() {
		// start model generation		
		try {
			System.out.println("------- run()------");
			modelgen.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  void setRuleIndex(int i) {
		ruleIndex = i;
	}
	
	public ITGGMatch getSelectedMatch() {
		ITGGMatch match = null;
		
		if (ruleIndex >= 0 && matches != null) {
		
			Object[] t = matches.toArray();
			match = (ITGGMatch)t[ruleIndex];
		}
		return match;
	}
	
	public Set<ITGGMatch> getMatches(){
		matches =  modelgen.getMatchContainer().getMatches();
		
		return matches;
	}
}


