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
	private CallbackHandler callbackHandler;
	private FWDThread thread;
	
	
	
	
	public ModelLoader_INITIAL_FWD(Function<DataObject, INITIAL_FWD> fwd, String pP, String wP) {
		// TODO Auto-generated constructor stub
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_FWD");
		fwd_demonstrator = fwd;
		
		callbackHandler = CallbackHandler.getInstance();
		
		startVisualisation(this);
	}

	@Override
	protected void initThread() {
		thread = new FWDThread(fwd, this);
		thread.setName("INITIAL_FWD Thread");
		thread.start();
		
		System.out.println("Model translation process is running on thread " + thread.getId());
	}
	
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		
		String pathProjectTemp = pathProject;
		
		if (!pathSrc.equals(" ") && !pathSrc.equals("")) {
			
			if (pathProjectTemp.equals(" ") || pathProjectTemp.equals("")) {
				pathProjectTemp = projectPath + "/instances/";
			}
			
			if (pathTrg.equals("") || pathTrg.equals("")) {
				pathTrg = pathProjectTemp + "trg.xmi";
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
			
			fwd = fwd_demonstrator.apply(data);
			
			options = fwd.getOptions();
			resourceHandler = fwd.getResourceHandler();
			
			source = resourceHandler.getSourceResource();
			target = resourceHandler.getTargetResource();
			
			loadingOption = LoadingOption.SelectedResource;
			
			initThread();
			
		}else {
			System.out.println("Path is empty...");
		}
	}
	
	@Override
	public void loadFromDefault() {
		
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.LOAD_MODEL);
		
		fwd = fwd_demonstrator.apply(data);
		
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.Default;
		
		initThread();
	}
	
	
	
	/*
	 * creates an empty model 
	 */
	@Override
	public void generateNewModel() {
	
		DataObject data = new DataObject(projectPath + "/instances/src.xmi", 
				projectPath + "/instances/trg.xmi", 
				projectPath + "/instances/corr.xmi",
				projectPath + "/instances/protocol.xmi", 
				Modelgeneration.NEW_MODEL);
		
		fwd = fwd_demonstrator.apply(data);
				
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
		
		initThread();
	}
	
	@Override
	public void highlightGraph(TggVisualizer visSrc, TggVisualizer visTrg) {
		highlightingGraphAlgorithm(visSrc, visTrg, "CreateNode","ContextNode");
	}
	
	/*
	 * forward translation of the source model
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
	public String buttonTranslateTxt() {
		// TODO Auto-generated method stub
		return "Translate Forward";
	}


	@Override
	public Combo createComboBox(Group g) {
		return new Combo(g, SWT.DROP_DOWN | SWT.READ_ONLY);
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


class FWDThread extends Thread{
	
	private INITIAL_FWD fwd;
	private ModelLoader_INITIAL_FWD modelLoader;
	private CallbackHandler callbackHandler;
	
	private Set<ITGGMatch> matches = new HashSet<> ();
	
	public FWDThread (INITIAL_FWD fwd, ModelLoader_INITIAL_FWD modelLoader) {
		this.fwd = fwd;
		this.modelLoader = modelLoader;
		
		callbackHandler = CallbackHandler.getInstance();
	}
	
	@Override
	public void run(){
		
		initializeModelgenStart();
		
		runForwardTranslation();

		
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
	
	private void initializeModelgenStart() {
		fwd.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){
			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				
				ITGGMatch match = null;
				
				callbackHandler.updateGraph(CallbackHandler.UpdateGraphType.TRG);
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
				
				System.out.println("FWD_Match: " + match.getRuleName());
				
				return match;			
			}
		});
	}
	
	private void runForwardTranslation() {
		try {
			System.out.println("------- Forward ------");
			fwd.forward();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
