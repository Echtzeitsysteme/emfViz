package visualisation;


import java.awt.Frame;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import graphVisualization.InstanceDiagrammLoader;
import graphVisualization.VisContentAdapter;
import graphVisualization.Visualizer;
import tggDemonstrator.ModelLoader_INITIAL_BWD;
import tggDemonstrator.ModelLoader_INITIAL_FWD;
import tggDemonstrator.ModelLoader_MODELGEN;
import tggDemonstrator.ModelLoader_SYNC;
import tggDemonstrator.TGGDemonstrator;




public class DisplayHandler {

	
	private Display display;
	private Shell shell;
	
	private TGGDemonstrator modelLoader;
	
	private Frame frameSrc;
	private Frame frameTrg;
	
	private Rectangle rectangleSrc;
	private Rectangle rectangleTrg;
	
	private GraphManipulator manipSrc;
	private GraphManipulator manipTrg;
	
	private InstanceDiagrammLoader dataSrc;
	private InstanceDiagrammLoader dataTrg;
	
	private TggVisualizer visSrc;
	private TggVisualizer visTrg;
	
	private TggLoadModelDisplay loadModelDisplay;
	private TggVisualizerDisplay tggVisualizerDisplay;
	private TggSelectResourceDisplay tggSelectResourceDisplay;
	
	private CallbackHandler callbackHandler;
	
	private String windowNameExtension = "";

	
	/*
	 * Constructor - needs a ModelLoader instance
	 */
	public DisplayHandler (TGGDemonstrator modelLoader) {
		this.modelLoader = modelLoader;
		
		callbackHandler = CallbackHandler.getInstance();
		
		//init display and shell
		if (display == null || shell == null)
			InitUI();
		
		//open start window
		openTggLoadModelDisplay();
	}
	
	
	/*
	 * Init display and shell
	 */
	private void InitUI() {
		/* init main window */
		display = new Display();
		shell = new Shell(display);
		
		if(modelLoader instanceof ModelLoader_MODELGEN)
			windowNameExtension = "MODELGEN";
		else if(modelLoader instanceof ModelLoader_INITIAL_BWD)
			windowNameExtension = "INITIAL_BWD";
		else if(modelLoader instanceof ModelLoader_INITIAL_FWD)
			windowNameExtension = "INITIAL_FWD";
		else if(modelLoader instanceof ModelLoader_SYNC)
			windowNameExtension = "SYNC";
		
		shell.setText("TGG Demonstrator - " + windowNameExtension);
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {
				System.out.println("close");
		    }
		});
	}
	
	/*
	 * start the visualization and open the shell
	 */
	public void run() {		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}

	
	/*
	 * Removes all widgets from the shell.
	 * This method is called every time before a new window is opened.
	 */
	private void resetShell() {
		for (Control kid : shell.getChildren()) {
	          kid.dispose();
	    }
	}
	
	/*
	 * Return shell instance
	 */
	public Shell getShell() {
		return shell;
	}
	
	
	public GraphManipulator getSrcManipulator() {
		return manipSrc;
	}
	public GraphManipulator getTrgManipulator() {
		return manipTrg;
	}
	
	public TggVisualizer getSrcTggVisualizer() {
		return visSrc;
	}
	public TggVisualizer getTrgTggVisualizer() {
		return visTrg;
	}
	
	public void openTggLoadModelDisplay() {
		resetShell();
		
		TggLoadModelDisplay loadModelDisplay = new TggLoadModelDisplay(this, modelLoader, display, shell);
	}
	
	public void openTggVisualizerDisplay() {
		resetShell();
		
		tggVisualizerDisplay = new TggVisualizerDisplay(this, modelLoader, display, shell);
		
		frameSrc = tggVisualizerDisplay.getSrcFrame();
		frameTrg = tggVisualizerDisplay.getTrgFrame();
		
		rectangleSrc = tggVisualizerDisplay.getSrcRectangle();
		rectangleTrg = tggVisualizerDisplay.getTrgRectangle();
		
		initialiseRelevantInstances();
		
		tggVisualizerDisplay.setGraphManipulatorSrc(manipSrc);
		tggVisualizerDisplay.setGraphManipulatorTrg(manipTrg);
		
		tggVisualizerDisplay.setSrcInstanceDiagrammLoader(dataSrc);
		tggVisualizerDisplay.setTrgInstanceDiagrammLoader(dataTrg);
		
		tggVisualizerDisplay.setTggSrcVisualizer(visSrc);
		tggVisualizerDisplay.setTggTrgVisualizer(visTrg);
		
		
	}
	
	public void openTggResourceSelectionDisplay() {
		resetShell();
		
		tggSelectResourceDisplay = new TggSelectResourceDisplay(this, modelLoader, display, shell);
	}
	
	/*
	 * Initializes all necessary instances to start the graph visualization
	 */
	private void initialiseRelevantInstances() {
		Resource srcRs = modelLoader.getSource();
		Resource trgRs = modelLoader.getTarget();
		
		dataSrc = new InstanceDiagrammLoader(srcRs, true);
		dataTrg = new InstanceDiagrammLoader(trgRs, true);
		
		
		visSrc = new TggVisualizer(dataSrc, frameSrc, rectangleSrc);
		visTrg = new TggVisualizer(dataTrg, frameTrg, rectangleTrg);
		
		// connect resource with visualization to keep both in sync
		callbackHandler.registerSourceContentAdapter(srcRs, visSrc);
		callbackHandler.registerTargetContentAdapter(trgRs, visTrg);
		
		visSrc.init();
		visTrg.init();
		
		manipSrc = new GraphManipulator(visSrc, display, dataSrc, modelLoader, GraphManipulator.GraphType.SRC);
		manipTrg = new GraphManipulator(visTrg, display, dataTrg, modelLoader, GraphManipulator.GraphType.TRG);
		
		manipSrc.initialize();
		manipTrg.initialize();
	}
}


