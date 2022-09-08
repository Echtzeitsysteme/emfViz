package visualisation;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

public interface UserControlArea {
	
	/*
	 * Interface to define basic button titles and button functionalities at visualizer display
	 */
		
	public String buttonTranslateTxt();
	
	public void buttonTranslateFunction();
	
	public String returnButtonTitle();
	
	public Combo createComboBox(Group g);
	
	public boolean isFrameSourceActive();
	
	public boolean isFrameTargetActive();

}
