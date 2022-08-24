package tggDemonstrator;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;

import tggDemonstrator.DataObject.Modelgeneration;
import tggDemonstrator.TGGDemonstrator.LoadingOption;
import visualisation.CallbackHandler;

public class ModelLoader_INITIAL_FWD extends TGGDemonstrator{

	
	
	private Function<DataObject, INITIAL_FWD> fwd_demonstrator;
	private INITIAL_FWD fwd;
	//private IbexOptions options;
	private TGGResourceHandler resourceHandler;
	private CallbackHandler callbackHandler;
	
	public ModelLoader_INITIAL_FWD(Function<DataObject, INITIAL_FWD> fwd, String pP, String wP) {
		// TODO Auto-generated constructor stub
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_FWD");
		fwd_demonstrator = fwd;
		
		callbackHandler = CallbackHandler.getInstance();
		
		startVisualisation(this);
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
	}
	

	@Override
	public String buttonTranslateTxt() {
		// TODO Auto-generated method stub
		return "Translate Forward";
	}

	
	/*
	 * forward translation of the source model
	 */
	@Override
	public void buttonTranslateFunction() {
		try {
			fwd.forward();
			
			target = resourceHandler.getTargetResource();
			
			callbackHandler.updateGraph(CallbackHandler.UpdateGraphType.TRG);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Combo createComboBox(Group g) {
		// TODO Auto-generated method stub
		return null;
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
