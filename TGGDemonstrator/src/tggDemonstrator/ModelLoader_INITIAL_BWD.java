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


public class ModelLoader_INITIAL_BWD extends TGGDemonstrator{
	
	private Function<DataObject, INITIAL_BWD> bwd_Demonstrator;
	private INITIAL_BWD bwd;
	
	
	
	public ModelLoader_INITIAL_BWD (Function<DataObject, INITIAL_BWD> bwd, String pP, String wP) {
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_BWD");
		bwd_Demonstrator = bwd;
		
		
		startVisualisation(this);
	}

	@Override
	public void createResourcesFromPath(String pathLoc, String pathTrg) {
		// TODO Auto-generated method stub
		
		String pathLocTemp = pathLoc;
		
		if (!pathTrg.equals(" ") && !pathTrg.equals("")) {
			
			if (pathLocTemp.equals(" ") && pathLocTemp.equals("")) {
				pathLocTemp = projectPath + "/instances/";
			}
			
			DataObject data = new DataObject(pathLocTemp + "src.xmi", 
					pathTrg, 
					pathLocTemp + "corr.xmi",
					pathLocTemp + "protocol.xmi", 
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
		
		loadingOption = LoadingOption.Default;
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
		return false;
	}

	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return true;
	}

}
