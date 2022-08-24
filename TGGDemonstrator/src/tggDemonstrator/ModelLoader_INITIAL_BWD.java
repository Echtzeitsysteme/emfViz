package tggDemonstrator;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;

import tggDemonstrator.DataObject.Modelgeneration;
import tggDemonstrator.TGGDemonstrator.LoadingOption;
import visualisation.CallbackHandler;


public class ModelLoader_INITIAL_BWD extends TGGDemonstrator{
	
	private Function<DataObject, INITIAL_BWD> bwd_Demonstrator;
	private INITIAL_BWD bwd;
	private CallbackHandler callbackHandler;
	
	
	
	public ModelLoader_INITIAL_BWD (Function<DataObject, INITIAL_BWD> bwd, String pP, String wP) {
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_BWD");
		bwd_Demonstrator = bwd;
		
		callbackHandler = CallbackHandler.getInstance();
		
		
		startVisualisation(this);
	}

	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		// TODO Auto-generated method stub
		
		String pathProjectTemp = pathProject;
		
		if (!pathTrg.equals(" ") && !pathTrg.equals("")) {
			
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
		}else {
			System.out.println("Path is empty...");
		}
		
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
		
		bwd = bwd_Demonstrator.apply(data);
				
		options = bwd.getOptions();
		resourceHandler = bwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
		loadingOption = LoadingOption.NewModel;
	}
	

	@Override
	public String buttonTranslateTxt() {
		// TODO Auto-generated method stub
		return "Translate Backward";
	}

	
	/*
	 * backward translation of the target model
	 */
	@Override
	public void buttonTranslateFunction() {
		try {
			bwd.backward();
			
			source = resourceHandler.getSourceResource();
			
			callbackHandler.updateGraph(CallbackHandler.UpdateGraphType.SRC);
			
		} catch (IOException e) {
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
		return false;
	}

	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return true;
	}

}
