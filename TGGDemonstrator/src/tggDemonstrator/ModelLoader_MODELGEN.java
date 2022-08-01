package tggDemonstrator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<String[], MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	private String[] paths	= new String[2];
	
	private ModelgenThread thread;
	private int ruleIndex;
	
	
	public ModelLoader_MODELGEN (Function<String[], MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_MODELGEN");
		modelgen_demonstrator = modelgen;
		
		
		startVisualisation(this);
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
		// TODO Auto-generated method stub
		paths[0] = pathSrc;
		paths[1] = pathTrg;
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
	}

	@Override
	public void loadFromDefault() {
		// TODO Auto-generated method stub
		
		paths[0] = projectPath + "/instances/src.xmi";
		paths[1] = projectPath + "/instances/trg.xmi";
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
	}

	@Override
	public void generateNewModel() {
		// TODO Auto-generated method stub
		
		System.out.println("Current thread is " + Thread.currentThread().getId());
		
		paths[0] = projectPath + "/instances/src.xmi";
		paths[1] = projectPath + "/instances/trg.xmi";
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		
		// create empty models
		source = resourceHandler.createResource(paths[0]);
		
		target = resourceHandler.createResource(paths[1]);
		
	

		
		
		// set basic stop criterion
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
    	//stop.setMaxRuleCount("HospitaltoAdministrationRule", 1);
		//stop.setMaxElementCount(12);
		modelgen.setStopCriterion(stop);
		
		// Start new thread
		thread = new ModelgenThread(modelgen);
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
		
		String[] ruleNames = new String[modelgen.getMatchContainer().getMatches().size()];
		int i = 0;
		
		
		for (ITGGMatch m : modelgen.getMatchContainer().getMatches()) {
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
}



class ModelgenThread extends Thread{

	
	private MODELGEN modelgen;
	private int ruleIndex;
	
	public ModelgenThread(MODELGEN m) {
		this.modelgen = m;
		ruleIndex = -1;
	}
	
	@Override
	public void run(){
		
		initilaizeModelgenStart();
		
		runModelgeneration();
		
		/*
		while (true) {
			//do something
			System.out.println("Thread is running!");
		}*/
	}
	
	
	/*
	 * Wake up thread after sleep
	 */
	public boolean wakeUp() {
		
		System.out.println("Hey thread " + getId() + " wake up!");
		
		interrupt();
		
		return true;
	}
	
	public void initilaizeModelgenStart() {
		modelgen.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				
				System.out.println("Thread sleeps again for a very long time!");
				
				try {
					sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					
					System.out.println("RuleIndex: " + ruleIndex);
					
					if (ruleIndex >= 0) {
						ITGGMatch match = null;
						
						System.out.println(matchContainer.getMatches().toString());
						
						for (int i = 0; i <= ruleIndex; i++) {
							
							if (matchContainer.getMatches().iterator().hasNext())
								match = matchContainer.getMatches().iterator().next();
							else
								System.out.println("ERROR !!!! - selected rule does not exist");
						}
						
						System.out.println("the rule " + match.getRuleName() + " will be performed");
						
						return match;
					}else {
						//if no rule is selected then just use the next one
						ITGGMatch match = matchContainer.getNext();
						
						System.out.println("the rule " + match.getRuleName() + " will be performed");
					}
				}
				
				return null;
						
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
}


