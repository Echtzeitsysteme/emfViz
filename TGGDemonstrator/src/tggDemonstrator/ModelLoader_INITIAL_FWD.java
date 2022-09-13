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
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;
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

public class ModelLoader_INITIAL_FWD extends TGGDemonstrator{

	
	
	private Function<DataObject, INITIAL_FWD> fwd_demonstrator;
	private INITIAL_FWD fwd;
	private TGGResourceHandler resourceHandler;
	private FWDThread thread;
	
	
	
	
	public ModelLoader_INITIAL_FWD(Function<DataObject, INITIAL_FWD> fwd, String pP, String wP) {
		// TODO Auto-generated constructor stub
		super(pP, wP);
		
		logger.info("Initialize ModelLoader_INITIAL_FWD");
		fwd_demonstrator = fwd;
		
		startVisualisation(this);
	}

	@Override
	protected void initThread() {
		thread = new FWDThread(fwd);
		thread.setName("INITIAL_FWD Thread");
		thread.start();
		
		logger.info("Model translation process is running on thread " + thread.getId());
	}
	
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		
		String pathProjectTemp = pathProject;
		
		//ERROR check
		if (pathSrc.isBlank())
			return;
			
		if (pathProjectTemp.isBlank()) {
			pathProjectTemp = projectPath + "/instances/";
		}
		
		if (pathTrg.isBlank()) {
			pathTrg = pathProjectTemp + "trg.xmi";
		}
		
		if (pathCorr.isBlank()) {
			pathCorr = pathProjectTemp + "corr.xmi";
		}
		
		if (pathProtocol.isBlank()) {
			pathProtocol = pathProjectTemp + "protocol.xmi";
		}
		
		DataObject data = new DataObject(pathSrc, 
				pathTrg, 
				pathCorr,
				pathProtocol,
				Modelgeneration.LOAD_MODEL);
		
		//Call INITIAL_FWD constructor
		fwd = fwd_demonstrator.apply(data);
		
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.SelectedResource;
		
		initThread();
	}
	
	@Override
	public void loadFromDefault() {
		
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.LOAD_MODEL);
		
		//Call INITIAL_FWD constructor
		fwd = fwd_demonstrator.apply(data);
		
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.Default;
		
		initThread();
	}
	
	@Override
	public void generateNewModel() {
	
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.NEW_MODEL);
		
		//Call INITIAL_FWD constructor
		fwd = fwd_demonstrator.apply(data);
				
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
		
		initThread();
	}
	
	@Override
	public  void saveModels() {
		try {
			logger.info("Models are saved at " + projectPath);
			
			fwd.saveModels();
		} catch (IOException e) {
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
	 * Continues forward translation of the source model
	 */
	@Override
	public void buttonTranslateFunction() {
		//next step functionalities
		try {
			logger.info("Button Next Rule is clicked...");
			
			thread.wakeUp();		
		}catch(Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public boolean isFrameSourceActive() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return false;
	}
}


class FWDThread extends ModelLoaderThread{
	
	private INITIAL_FWD fwd;
	
	public FWDThread (INITIAL_FWD fwd) {
		super();
		
		this.fwd = fwd;
		
		translateButtonTitle = "Next Step";
	}

	@Override
	protected void initialize() {
		fwd.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				
				ITGGMatch match = null;
				
				callbackHandler.updateGraph();
				
				try {
					
					matches = matchContainer.getMatches();
					
					callbackHandler.setMatches(matches);
					
					
					logger.info("Thread " + getId() + " sleeps for a very long time!");
					sleep(Long.MAX_VALUE);
					
				} catch (InterruptedException e) {
					
					
					match = callbackHandler.getSelectedMatch();
					if(match == null) {
						//if no match is selected then just use the next match
						match = matchContainer.getNext();
					}
				}
				
				logger.info("FWD_Match: " + match.getRuleName());
				
				return match;			
			}
		});
	}


	@Override
	protected void startProcess() {
		try {
			logger.info("------- Forward ------");
			fwd.forward();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
