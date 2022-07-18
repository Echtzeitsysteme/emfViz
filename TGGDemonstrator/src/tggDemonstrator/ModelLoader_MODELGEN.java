package tggDemonstrator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import tggDemonstrator.TGGDemonstrator.NewModelGenerationThread;


public class ModelLoader_MODELGEN extends TGGDemonstrator {

	
	private Function<String[], MODELGEN> modelgen_demonstrator;
	private MODELGEN modelgen;
	private String[] paths	= new String[2];
	
	
	public ModelLoader_MODELGEN (Function<String[], MODELGEN> modelgen, String pP, String wP) {	
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_MODELGEN");
		modelgen_demonstrator = modelgen;
		
		
		startVisualisation(this);
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

	@Override
	public void generateNewModel() {
		// TODO Auto-generated method stub
		
		paths[0] = projectPath + "/instances/src.xmi";
		paths[1] = projectPath + "/instances/trg.xmi";
		
		modelgen = modelgen_demonstrator.apply(paths);
		
		options = modelgen.getOptions();
		resourceHandler = modelgen.getResourceHandler();
		
		
		// create empty models
		source = resourceHandler.createResource(paths[0]);
		
		target = resourceHandler.createResource(paths[1]);
		
		
			
			
				
				
		/*try {
			//define stop criterions
			MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
	    	stop.setMaxRuleCount("HospitaltoAdministrationRule", 1);
			stop.setMaxElementCount(10);
			modelgen.setStopCriterion(stop);
			modelgen.run();
			
			source = generator.getSourceResource();
			target = generator.getTargetResource();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		System.out.println("Start NewModelGenerationThread in thread with ID:" + Thread.currentThread().getId());
		
		NewModelGenerationThread thread = new NewModelGenerationThread();
		thread.start();
		
		
		
		
		MODELGENStopCriterion stop = new MODELGENStopCriterion(modelgen.getTGG());
		modelgen.setStopCriterion(stop);
		
		modelgen.setUpdatePolicy((IUpdatePolicy) new IUpdatePolicy(){

			@Override
			public ITGGMatch chooseOneMatch(ImmutableMatchContainer matchContainer) {
				
				ArrayList <ITGGMatch> rules = new ArrayList <ITGGMatch>();
				
				for(ITGGMatch m : matchContainer.getMatches()) {
					//m.getRuleName();
					//return m;
					rules.add(m);
				}
				
				thread.interrupt();
				
				return rules.get(0);
			}
			
		});
		
		try {
			modelgen.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}

}
