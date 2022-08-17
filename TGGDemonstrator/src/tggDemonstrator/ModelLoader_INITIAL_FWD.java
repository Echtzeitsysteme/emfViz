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

public class ModelLoader_INITIAL_FWD extends TGGDemonstrator{

	
	
	private Function<DataObject, INITIAL_FWD> fwd_demonstrator;
	private INITIAL_FWD fwd;
	//private IbexOptions options;
	private TGGResourceHandler resourceHandler;
	
	public ModelLoader_INITIAL_FWD(Function<DataObject, INITIAL_FWD> fwd, String pP, String wP) {
		// TODO Auto-generated constructor stub
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_FWD");
		fwd_demonstrator = fwd;
		
		startVisualisation(this);
	}

	@Override
	public void createResourcesFromPath(String pathSrc, String pathLoc) {
		
		String pathLocTemp = pathLoc;
		
		if (!pathSrc.equals(" ") && !pathSrc.equals("")) {
			
			if (pathLocTemp.equals(" ") && pathLocTemp.equals("")) {
				pathLocTemp = projectPath + "/instances/";
			}
			
			DataObject data = new DataObject(pathSrc, 
					pathLocTemp + "trg.xmi", 
					pathLocTemp + "corr.xmi",
					pathLocTemp + "protocol.xmi",
					Modelgeneration.DEFAULT);
			
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
				Modelgeneration.DEFAULT);
		
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
		
		loadingOption = LoadingOption.Default;
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
