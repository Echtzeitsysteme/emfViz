package Main;

import java.awt.Panel;
import java.io.File;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.swt.SWT;
//Imports require org.eclipse.swt

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.emoflon.smartemf.persistence.SmartEMFResourceFactoryImpl;
//import CompanyLanguage.CompanyLanguagePackage;

import Hospital2Administration.Hospital2AdministrationPackage;
import Main.ModelLoader.ResourceType;
import graphVisualization.InstanceDiagrammLoader;
// First add emfViz project to your's project build path via 'Properties' -> 'Java Build Path' -> 'Projects' -> 'Classpath' -> 'Add' 
import graphVisualization.Visualizer;
import userInterface.MainWindow;

public class Main_withURILoader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
		
		Display display = new Display();
       
        MainWindow graphVisualizer = new MainWindow(display, null);
  		graphVisualizer.createMainWindow();
  		
  		Panel panelSrc = graphVisualizer.panelSrc;
		Panel panelTrg = graphVisualizer.panelTrg;               
       
        // first define workingDirectory of the model
    	String wokingDirectory = "/Users/jordanlischka/Documents/runtime-Test_Workspace_2022-05-26/git/emoflon-ibex-tutorial/Hospital2Administration/";
		
        
    	Shell shell = graphVisualizer.getShell();
    	
		//Create an instace of the InstaceDiagrammLoader class with the loaded resource
		InstanceDiagrammLoader dataSrc = new InstanceDiagrammLoader(ModelLoader.loadModelWithURI(ResourceType.Source, wokingDirectory), true);
		InstanceDiagrammLoader dataTarget = new InstanceDiagrammLoader(ModelLoader.loadModelWithURI(ResourceType.Target, wokingDirectory), true);
		

		Visualizer visSrc = new Visualizer(dataSrc, panelSrc);
		Visualizer visTrg = new Visualizer(dataTarget, panelTrg);
        
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();		
	}

}
