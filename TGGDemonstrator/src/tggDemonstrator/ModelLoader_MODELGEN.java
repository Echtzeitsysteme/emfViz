package tggDemonstrator;


import java.io.IOException;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import tggDemonstrator.DataObject.Modelgeneration;
import visualisation.TggVisualizer;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<DataObject, MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	private ModelgenThread thread;
	
	
	public ModelLoader_MODELGEN (Function<DataObject, MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_MODELGEN");
		modelgen_demonstrator = modelgen;
		
		startVisualisation(this);
	}
	
	@Override
	protected void initThread() {
		thread = new ModelgenThread(modelgen);
		thread.setName("Modelgen Thread");
		thread.start();
		
		System.out.println("Model generation process is running on thread " + thread.getId());
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
		initThread();
		
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
		initThread();
		
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
		
		// set basic stop criterion
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
		modelgen.setStopCriterion(stop);
		
		// Start new thread
		initThread();
		
	}
	
	@Override
	public void highlightGraph(TggVisualizer visSrc, TggVisualizer visTrg) {
		/*
		 * Possibility of new implementation for further adjustments 
		*/
		highlightingGraphAlgorithm(visSrc, visTrg);
	}
	
	// Implemented methods from interface

	@Override
	public void buttonTranslateFunction() {
		//next step functionalities
		
		System.out.println("Button Next Rule is clicked...");
		
		try {
			thread.wakeUp();
			
		}catch(Exception e) {
			System.out.println(e);
		}	
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



class ModelgenThread extends ModelLoaderThread{
	
	private MODELGEN modelgen;
	
	public ModelgenThread(MODELGEN m) {
		super();
		
		this.modelgen = m;
		
		translateButtonTitle = "Next Step";

	}

	@Override
	protected void initialize() {
		modelgen.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
			
			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				
				ITGGMatch match = null;
				
				callbackHandler.updateGraph();
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
				}
				
				System.out.println("MODELGEN_Match: " + match.getRuleName());
				
				return match;			
			}
		});
	}

	@Override
	protected void startProcess() {
		try {
			System.out.println("------- run ------");
			modelgen.run();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}