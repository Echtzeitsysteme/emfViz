package tggDemonstrator;

import java.io.IOException;
import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;


public class ModelLoader_INITIAL_BWD extends TGGDemonstrator{
	
	private Function<String, INITIAL_BWD> bwd_Demonstrator;
	private INITIAL_BWD bwd;
	
	
	
	public ModelLoader_INITIAL_BWD (Function<String, INITIAL_BWD> bwd, String pP, String wP) {
		super(pP, wP);
		
		System.out.println("Initialize ModelLoader_INITIAL_BWD");
		bwd_Demonstrator = bwd;
		
		
		startVisualisation(this);
	}

	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
		// TODO Auto-generated method stub
		
		if (!pathTrg.equals(" ") && !pathTrg.equals("")) {
			bwd = bwd_Demonstrator.apply(pathTrg);
			
			options = bwd.getOptions();
			resourceHandler = bwd.getResourceHandler();
			
			source = resourceHandler.getSourceResource();
			target = resourceHandler.getTargetResource();
		}else {
			System.out.println("Path is empty...");
		}
		
	}

	@Override
	public void loadFromDefault() {
		// TODO Auto-generated method stub
		bwd = bwd_Demonstrator.apply(projectPath + "/instances/trg.xmi");
		
		options = bwd.getOptions();
		resourceHandler = bwd.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
	}
	
	@Override
	public void generateNewModel() {
		return;
	}
	
	
	/*
	 * backward translation of the target model
	 */	
	public void backward() {
		try {
			bwd.backward();
			
			source = resourceHandler.getSourceResource();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}