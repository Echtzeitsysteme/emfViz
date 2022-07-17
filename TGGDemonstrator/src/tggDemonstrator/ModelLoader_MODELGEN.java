package tggDemonstrator;


import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<String[], MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	private String[] paths	= new String[2];
	
	
	public ModelLoader_MODELGEN (Function<String[], MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		modelgen_demonstrator = modelgen;
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
		// TODO Auto-generated method stub
		paths[0] = pathSrc;
		paths[1] = pathTrg;
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
	}

	@Override
	public void loadFromDefault() {
		// TODO Auto-generated method stub
		
		paths[0] = projectPath + "/instances/src.xmi";
		paths[1] = projectPath + "/instances/trg.xmi";
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		source = resourceHandler.getSourceResource();
		target = resourceHandler.getTargetResource();
		
	}

}
