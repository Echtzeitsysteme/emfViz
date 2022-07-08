package Main;




import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.ibex.tgg.operational.strategies.gen.MODELGEN;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_FWD;
import org.emoflon.ibex.tgg.operational.strategies.sync.INITIAL_BWD;

import userInterface.MainWindow;

public class Demonstrator {
	
	MainWindow graphVisualizer;
	ModelLoader modelLoader;
	
	public Demonstrator (MODELGEN generator) {
		
		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
	}
	
	public Demonstrator (SYNC generator) {
		
		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
		
	}
	
	public Demonstrator (INITIAL_FWD generator) {

		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
		
	}
	
	public Demonstrator (INITIAL_BWD generator) {

		modelLoader = new ModelLoader(generator);
		
		graphVisualizer = new MainWindow(modelLoader);
		graphVisualizer.run();
	}
	
	

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
  		MainWindow graphVisualizer = new MainWindow(null);
  		
  		//Panel panelSrc = graphVisualizer.panelSrc;
		//Panel panelTrg = graphVisualizer.panelTrg;
        
		
		/*Visualizer visSrc = new Visualizer(dataSrc, panelSrc);
		Visualizer visTrg = new Visualizer(dataTarget, panelTrg);*/
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
	}
}
