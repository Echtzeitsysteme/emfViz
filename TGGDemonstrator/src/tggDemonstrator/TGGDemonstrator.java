package tggDemonstrator;

import org.eclipse.emf.ecore.resource.Resource;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.modules.IbexExecutable;
import org.emoflon.ibex.tgg.operational.strategies.modules.TGGResourceHandler;

import visualisation.DisplayHandler;

public abstract class TGGDemonstrator {
	
	public enum LoadingOption {Default, NewModel, SelectedResource};
	protected LoadingOption loadingOption;
		
	protected IbexOptions options;
	protected TGGResourceHandler resourceHandler;
	protected Resource source;
	protected Resource target;
	
	protected String projectPath;
	protected String workspacePath;
	
	protected DisplayHandler graphVisualizer;
	
	/*
	 * Class constructor
	 * @param	pP	directory of the project
	 * @param	wP	directory of the workspace
	 */
	public TGGDemonstrator (String pP, String wP) {
		projectPath = pP;
		workspacePath = wP;
	}
	
	/*
	 * Start the visualization and initialize the UI
	 */
	public void startVisualisation(TGGDemonstrator modelLoader) {
		graphVisualizer = new DisplayHandler(modelLoader);
		graphVisualizer.run();
	}	
	

	//--------------------- Abstract Methods --------------------------- 
	
	/*
	 * Generates a new Model
	 * this method should works only if the executable is from type MODELGEN 
	 */
	public abstract void generateNewModel();
	
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
	 * Returns target resource
	 */
	public Resource getTarget() {
		return target;
	}
	/*
	 * Returns IbexExecutable
	 */
	public IbexExecutable getExectuable() {
		return options.executable();
	}
	
	/*
	 * Returns IbexOptions
	 */
	public IbexOptions getOptions() {
		return options;
	}
	
	/*
	 * Returns TGGResourceHandler instance
	 */
	public TGGResourceHandler getResourceHandler() {
		return resourceHandler;
	}
	
	/*
	 * Returns value of loadingOption
	 */
	public LoadingOption getLoadingOption() {
		return loadingOption;
	}

}


