package tggDemonstrator;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import tggDemonstrator.DataObject.Modelgeneration;
import visualisation.CallbackHandler;
import visualisation.TggVisualizer;

public class ModelLoader_SYNC extends TGGDemonstrator{
	
	private Function<DataObject, SYNC> sync_demonstrator;
	private SYNC sync;
	private SYNCThread thread;
	
	public ModelLoader_SYNC(Function<DataObject, SYNC> sync, String pP, String wP) {
		super(pP, wP);
				
		System.out.println("Initialize ModelLoader_SYNC");
		sync_demonstrator	= sync;
		
		startVisualisation(this);	
	}
	
	@Override
	protected void initThread() {
		thread = new SYNCThread(sync);
		thread.setName("SYNC Thread");
		thread.start();
		
		System.out.println("Model translation process is running on thread " + thread.getId());
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		
		String pathProjectTemp = pathProject;
		Modelgeneration loadingTypeTmp = Modelgeneration.LOAD_MODEL;
		
		if (pathSrc.isBlank() && pathTrg.isBlank())
			return;
			
		if (pathProjectTemp.isBlank()) 
			pathProjectTemp = projectPath + "/instances/";
		
		
		if (pathTrg.isBlank() && !pathSrc.isBlank()) {
			pathTrg = pathProjectTemp + "trg.xmi";
			loadingTypeTmp = Modelgeneration.LOAD_SRC_CREATE_TRG;
		}
			
		if (pathSrc.isBlank() && !pathTrg.isBlank()) {
			pathSrc = pathProjectTemp + "src.xmi";
			loadingTypeTmp = Modelgeneration.CREATE_SRC_LOAD_TRG;
		}
		
		if (pathCorr.isBlank())
			pathCorr = pathProjectTemp + "corr.xmi";
		
		if (pathProtocol.isBlank())
			pathProtocol = pathProjectTemp + "protocol.xmi";
		
		
		DataObject data = new DataObject(pathSrc, 
				pathTrg, 
				pathCorr,
				pathProtocol,
				loadingTypeTmp);
		
		sync = sync_demonstrator.apply(data);
		
		options = sync.getOptions();
		resourceHandler = sync.getResourceHandler();
		
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
		
		sync = sync_demonstrator.apply(data);
		
		options = sync.getOptions();
		resourceHandler = sync.getResourceHandler();
		
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
		
		sync = sync_demonstrator.apply(data);
				
		options = sync.getOptions();
		resourceHandler = sync.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
		
		initThread();
		
	}
	
	@Override
	public  void saveModels() {
		try {
			sync.saveModels();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * Continues graph sync function 
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
		return true;
	}


	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return true;
	}
}

class SYNCThread extends ModelLoaderThread{
	
	private SYNC sync;
	
	public SYNCThread(SYNC sync) {
		super();
		
		this.sync = sync;
		
		translateButtonTitle = "Next Step";
	}

	@Override
	protected void initialize() {
		sync.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
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
						//if no match is selected then just use the next one
						match = matchContainer.getNext();
					}
				}
				
				System.out.println("FWD_Match: " + match.getRuleName());
				
				return match;			
			}
		});	
	}

	@Override
	protected void startProcess() {
		try {
			if (callbackHandler.getLastProcessedGraph() == null)
				return;
			
			System.out.println("------- sync ------");
			
			if (callbackHandler.getLastProcessedGraph().equals("SRC"))
				sync.forward();
			else if(callbackHandler.getLastProcessedGraph().equals("TRG"))
				sync.backward();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
}
