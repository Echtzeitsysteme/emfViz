package tggDemonstrator;

import java.util.function.Function;

import org.emoflon.ibex.tgg.operational.strategies.sync.SYNC;

public class ModelLoader_SYNC extends TGGDemonstrator{
	
	private Function<String, SYNC> sync_demonstrator;
	private SYNC sync;
	
	public ModelLoader_SYNC(Function<String, SYNC> sync, String pP, String wP) {
		super(pP, wP);
		
		
		System.out.println("Initialize ModelLoader_SYNC");
		sync_demonstrator	= sync;
		
		
		startVisualisation(this);
	}
	
	
	@Override
	public void createResourcesFromPath(String pathSrc, String pathTrg) {
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
		
	}
	
	public void syncBackward() {
		
	}

}
