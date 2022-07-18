package tggDemonstrator;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;

public class ModelLoader_INITIAL_FWD extends TGGDemonstrator{

	
	
	private Function<String, INITIAL_FWD> fwd_demonstrator;
	private INITIAL_FWD fwd;
	private IbexOptions options;
	private TGGResourceHandler resourceHandler;
	
	public ModelLoader_INITIAL_FWD(Function<String, INITIAL_FWD> fwd, String pP, String wP) {
		// TODO Auto-generated constructor stub
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_FWD");
		fwd_demonstrator = fwd;
		
		startVisualisation(this);
	}

	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
		
		if (pathSrc != " " && pathSrc != "") {
			
			fwd = fwd_demonstrator.apply(pathSrc);
			
			options = fwd.getOptions();
			resourceHandler = fwd.getResourceHandler();
			
			source = resourceHandler.getSourceResource();
			target = resourceHandler.getTargetResource();
			
		}else {
			System.out.println("Path was empty...");
		}
	}
	
	@Override
	public void loadFromDefault() {
		fwd = fwd_demonstrator.apply(projectPath + "/instances/src.xmi");
		
		options = fwd.getOptions();
		resourceHandler = fwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
	}
	
	@Override
	public void generateNewModel() {
		return;
	}
	
	
	/*
	 * forward translation of the source model
	 */
	public void forward() {
		try {
			fwd.forward();
			
			target = resourceHandler.getTargetResource();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
