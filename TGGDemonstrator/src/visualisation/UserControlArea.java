package visualisation;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

public interface UserControlArea {
	
	/*
	 * Interface to define basic button titles and button functionalities for the visualizer display
	 */
		
	public String buttonTranslateTxt();
	
	public void buttonTranslateFunction();
	
	public Combo createComboBox(Group g);
	
	public boolean isFrameSourceActive();
	
	public boolean isFrameTargetActive();

}
