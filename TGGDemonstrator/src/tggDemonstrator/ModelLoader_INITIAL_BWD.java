package tggDemonstrator;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import graphVisualization.Node;
import language.TGGRule;
import language.TGGRuleNode;
import tggDemonstrator.DataObject.Modelgeneration;
import visualisation.CallbackHandler;
import visualisation.TggVisualizer;


public class ModelLoader_INITIAL_BWD extends TGGDemonstrator{
	
	private Function<DataObject, INITIAL_BWD> bwd_Demonstrator;
	private INITIAL_BWD bwd;
	private BWDThread thread;
	
	
	public ModelLoader_INITIAL_BWD (Function<DataObject, INITIAL_BWD> bwd, String pP, String wP) {
		super(pP, wP);
		
		logger.info("Initialize ModelLoader_INITIAL_BWD");
		bwd_Demonstrator = bwd;
		
		startVisualisation(this);
	}

	
	@Override
	protected void initThread() {
		thread = new BWDThread(bwd);
		thread.setName("INITIAL_BWD Thread");
		thread.start();
		
		logger.info("Model translation process is running on thread " + thread.getId());
	}
	
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		
		String pathProjectTemp = pathProject;
		
		//ERROR check
		if (pathTrg.isBlank())
			return;
			
		if (pathProjectTemp.equals(" ") || pathProjectTemp.equals("")) {
			pathProjectTemp = projectPath + "/instances/";
		}
		
		if (pathSrc.equals(" ") || pathSrc.equals("")) {
			pathSrc = pathProjectTemp + "src.xmi";
		}
		
		if (pathCorr.equals(" ") || pathCorr.equals("")) {
			pathCorr = pathProjectTemp + "corr.xmi";
		}
		
		if (pathProtocol.equals(" ") || pathProtocol.equals("")) {
			pathProtocol = pathProjectTemp + "protocol.xmi";
		}
		
		DataObject data = new DataObject(pathSrc, 
				pathTrg, 
				pathCorr,
				pathProtocol, 
				Modelgeneration.LOAD_MODEL);
		
		bwd = bwd_Demonstrator.apply(data);
		
		options = bwd.getOptions();
		resourceHandler = bwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.SelectedResource;
		
		initThread();
	}

	@Override
	public void loadFromDefault() {
		// TODO Auto-generated method stub
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.LOAD_MODEL);
		
		bwd = bwd_Demonstrator.apply(data);
		
		options = bwd.getOptions();
		resourceHandler = bwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.Default;
		
		initThread();
		
	}
	
	/*
	 * Creates an empty model 
	 */
	@Override
	public void generateNewModel() {
	
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.NEW_MODEL);
		
		bwd = bwd_Demonstrator.apply(data);
				
		options = bwd.getOptions();
		resourceHandler = bwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
		
		initThread();
	}
	
	@Override
	public void saveModels() {
		try {
			bwd.saveModels();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("IOException: an error occured while model is saving!", e);
		}
	}
	
	@Override
	public void highlightGraph(TggVisualizer visSrc, TggVisualizer visTrg) {
		/*
		 * Possibility of new implementation for further adjustments 
		*/
		highlightingGraphAlgorithm(visSrc, visTrg);
	}
	
	// Implemented methods from interface
	
	/*
	 * Continues backward translation of the target model
	 */
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
		return true;
	}
}

class BWDThread extends ModelLoaderThread{
	
	private INITIAL_BWD bwd;
	
	
	public BWDThread (INITIAL_BWD bwd) {
		super();
		
		this.bwd = bwd;
		
		translateButtonTitle = "Next Step";
	}

	@Override
	protected void initialize() {
		bwd.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
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
				
				System.out.println("BWD_Match: " + match.getRuleName());
				
				return match;			
			}
		});
	}

	@Override
	protected void startProcess() {
		try {
			System.out.println("------- Backward ------");
			bwd.backward();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
