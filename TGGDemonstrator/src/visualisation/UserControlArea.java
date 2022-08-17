package visualisation;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;

public interface UserControlArea {
	
	public String buttonTranslateTxt();
	
	public void buttonTranslateFunction();
	
	public Combo createComboBox(Group g);
	
	public boolean isFrameSourceActive();
	
	public boolean isFrameTargetActive();

}
