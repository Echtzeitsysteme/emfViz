package Main;




import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.ibex.tgg.operational.defaults.IbexOptions;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;

import userInterface.MainWindow;

public class Demonstrator {
	
	MainWindow graphVisualizer;
	ModelLoader modelLoader;
	IbexOptions options;
	
	public Demonstrator (MODELGEN generator, IbexOptions options) {
		
		this.options = options;
		modelLoader = new ModelLoader(generator);
		
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
	}
	
	public Demonstrator (SYNC generator, IbexOptions options) {
		
		this.options = options;
		modelLoader = new ModelLoader(generator);
		
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
		
	}
	
	public Demonstrator (INITIAL_FWD generator, IbexOptions options) {

		this.options = options;
		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
		
	}
	
	public Demonstrator (INITIAL_BWD generator, IbexOptions options) {

		this.options = options;
		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
	}
}
