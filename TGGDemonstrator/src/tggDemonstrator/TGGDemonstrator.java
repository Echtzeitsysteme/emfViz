package tggDemonstrator;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Function;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGENStopCriterion;
import org.emoflon.ibex.tgg.operational.strategies.modules.IbexExecutable;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.operational.updatepolicy.IUpdatePolicy;

import userInterface.MainWindow;

import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.matches.ITGGMatch;
import org.emoflon.ibex.tgg.operational.matches.ImmutableMatchContainer;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;

public abstract class TGGDemonstrator {
	
	private MODELGEN modelgen;
		
	protected IbexOptions options;
	protected TGGResourceHandler resourceHandler;
	protected Resource source;
	protected Resource target;
	
	protected String projectPath;
	protected String workspacePath;
	
	MainWindow graphVisualizer;
	
	public TGGDemonstrator (String pP, String wP) {
		projectPath = pP;
		workspacePath = wP;
	}
	
	/*
	 * Start the visualization and initialize the UI
	*/
	public void startVisualisation(TGGDemonstrator modelLoader) {
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
	}	
	

	/*
	 * generate a new Model
	 * this method works only if executable is from type MODELGEN 
	 */
	public abstract void generateNewModel();
	
	
	//--------------------- Abstract Methods --------------------- 
	
	
	/*
	 * Perform loadModels operation from default locations: /instances/src.xmi  /instances/trg.xmi
	 */
	public abstract void loadFromDefault();
	
	/*
	 * Load source and target model from a given path
	 */
	public abstract void createResourcesFromPath(String pathSrc, String pathTrg);
		
	
	//--------------------- Setter & Getter Methods --------------------- 
	
	
	/*
	 * Set source resource
	 */	
	public void setSource(Resource source) {
		this.source = source;
	}
	
	/*
	 * Return source resource
	 */
	public Resource getSource(){
		return source;
	}
	
	/*
	 * Set target resource
	 */
	public void setTarget(Resource target) {
		this.target = target;
	}
	/*
	 * Return target resource
	 */
	public Resource getTarget() {
		return target;
	}
	/*
	 * Return IbexExecutable
	 */
	public IbexExecutable getExectuable() {
		return options.executable();
	}
	
	/*
	 * Return TGGResourceHandler instance
	 */
	public TGGResourceHandler getResourceHandler() {
		return resourceHandler;
	}
	
	
	//--------------------- Multi-Threading --------------------- 
	
	class NewModelGenerationThread extends Thread {
        @Override public void run() {
            while (true) {
                if (isInterrupted()) {
                	System.out.println("Thread with ID: " + Thread.currentThread().getId() + " is interrupted!");
                }
                
                //Thread is not interrupted
            }

        }
    }

}


