package tggDemonstrator;

import java.util.function.Function;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;

import visualisation.TggVisualizer;

public class ModelLoader_SYNC extends TGGDemonstrator{
	
	private Function<DataObject, SYNC> sync_demonstrator;
	private SYNC sync;
	
	public ModelLoader_SYNC(Function<DataObject, SYNC> sync, String pP, String wP) {
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_SYNC");
		sync_demonstrator	= sync;
		
		
		startVisualisation(this);
		
	}
	
	@Override
	protected void initThread() {
		return;
	}
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg, String pathCorr, String pathProtocol, String pathProject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFromDefault() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void generateNewModel() {
		// TODO Auto-generated method stub
		
	}
	
	public void syncForward() {
		// TODO Auto-generated method stub
		return;
	}
	
	public void syncBackward() {
		// TODO Auto-generated method stub
		return;
	}
	
	@Override
	public void highlightGraph(TggVisualizer visSrc, TggVisualizer visTrg) {
		//highlightingGraphAlgorithm(visSrc, visTrg, "","");
	}


	@Override
	public String buttonTranslateTxt() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void buttonTranslateFunction() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Combo createComboBox(Group g) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isFrameSourceActive() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isFrameTargetActive() {
		// TODO Auto-generated method stub
		return false;
	}
}
