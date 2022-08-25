package tggDemonstrator;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.common.emf.EMFEdge;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import language.TGG;
import language.TGGRule;
import language.TGGRuleEdge;
import language.TGGRuleNode;
import runtime.TGGRuleApplication;
import tggDemonstrator.DataObject.Modelgeneration;
import visualisation.CallbackHandler;
import visualisation.DisplayHandler;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<DataObject, MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	
	private ModelgenThread thread;
	private int ruleIndex;
	private Set<ITGGMatch> matches;
	
	
	public ModelLoader_MODELGEN (Function<DataObject, MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_MODELGEN");
		modelgen_demonstrator = modelgen;
		
		
		startVisualisation(this);
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		// TODO Auto-generated method stub
		String pathProjectTemp = pathProject;
		
		if (pathSrc.equals("") || pathTrg.equals(" "))
			return;		
			
		if (pathProjectTemp.equals("") || pathProjectTemp.equals(" "))
			pathProjectTemp = projectPath + "/instances/";

		
		if (pathCorr.equals("") || pathCorr.equals(" "))
			pathCorr = pathProjectTemp + "corr.xmi";
	
		
		if (pathProtocol.equals("") || pathProtocol.equals(" "))
			pathProtocol = pathProjectTemp + "protocol.xmi";
	
		
		DataObject data = new DataObject(pathSrc, 
				pathTrg, 
				pathCorr, 
				pathProtocol, 
				Modelgeneration.LOAD_MODEL);
		
		modelgen = modelgen_demonstrator.apply(data);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.SelectedResource;
		
		// set basic stop criterion
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
		modelgen.setStopCriterion(stop);
		
		// Start new thread
		thread = new ModelgenThread(modelgen, this);
		thread.setName("Modelgen Thread");
		thread.start();
		
		System.out.println("Model generation process is running on thread " + thread.getId());
		
	}

	@Override
	public void loadFromDefault() {
		
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi", 
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.LOAD_MODEL);
		
		modelgen = modelgen_demonstrator.apply(data);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.Default;
		
		// set basic stop criterion
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
		modelgen.setStopCriterion(stop);
		
		// Start new thread
		thread = new ModelgenThread(modelgen, this);
		thread.setName("Modelgen Thread");
		thread.start();
		
		System.out.println("Model generation process is running on thread " + thread.getId());
		
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
	/*public String[] getRuleNames() {
		
		matches = thread.getMatches();
		
		String[] ruleNames = new String[matches.size()];
		int i = 0;
		
		
		for (ITGGMatch m : matches) {
			ruleNames[i] = m.getRuleName();
			
			i++;
		}
		
		return ruleNames;	
	}*/
	
	/*
	 * Set rule index
	 */
	/*public void setSelectedRuleIndex(int i) {
		ruleIndex = i;
		
		thread.setRuleIndex(i);
	}
	*/
	/*
	 * Get rule index
	 */
	public int getSelectedRuleIndex() {
		return ruleIndex;
	}

	@Override
	public String buttonTranslateTxt() {
		// TODO Auto-generated method stub
		return "New Model";
	}

	@Override
	public void buttonTranslateFunction() {
		// TODO Auto-generated method stub
		//next step functionalities
		
		System.out.println("Button Next Rule is clicked...");
		
		wakeUpThread();	
		
		
	}
	@Override
	public Combo createComboBox(Group g) {
		return new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
	}

	@Override
	public boolean isFrameSourceActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return false;
	}
}



class ModelgenThread extends Thread{
	
	private MODELGEN modelgen;
	private int ruleIndex;
	
	private Set<ITGGMatch> matches = new HashSet<> ();
	private ITGGMatch selectedMatch;
	private CallbackHandler callbackHandler;
	
	
	private ModelLoader_MODELGEN modelLoader;
	
	public ModelgenThread(MODELGEN m, ModelLoader_MODELGEN modelLoader) {
		this.modelgen = m;
		this.modelLoader = modelLoader;
		ruleIndex = -1;
		
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run(){
		
		initializeModelgenStart();
		
		runModelgeneration();

		
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
				
				ITGGMatch match = null;
				
				callbackHandler.updateGraph(CallbackHandler.UpdateGraphType.ALL);
				try {
					
					matches = matchContainer.getMatches();
					
					callbackHandler.setMatches(matches);
					
					System.out.println("Thread " + getId() + " sleeps for a very long time!");
					sleep(Long.MAX_VALUE);

					
				} catch (InterruptedException e) {
					
					
					match = callbackHandler.getSelectedMatch();
					if(match == null) {
						//if no match is selected then just use the next match
						match = matchContainer.getNext();
					}
					/*
					Collection<String> s = match.getParameterNames();
					
					for (String so : s) {
						Object o = match.get(so);
						
					}
					System.out.println(s);
					*/
					
					TGGRule myRule = null;
					TGG tgg = modelLoader.options.tgg.flattenedTGG();
					for (TGGRule rule : tgg.getRules()) {
						
							myRule = rule;
						
					}
					
					if (myRule != null) {
						EList<TGGRuleNode> nodes = myRule.getNodes();
						EList<TGGRuleEdge> edges = myRule.getEdges();
					}
				}
				
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
	/*
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
	}*/
}


