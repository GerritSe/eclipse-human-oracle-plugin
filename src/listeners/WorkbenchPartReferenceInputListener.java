package listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;

public class WorkbenchPartReferenceInputListener implements IWorkbenchPartReferenceInputListener {
	protected Action handler;
	
	@Override
	public void setEventHandler(Action handler) {
		this.handler = handler;
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {
		handler.run();
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {}


	@Override
	public void mouseDown(MouseEvent e) {
		handler.run();
	}


	@Override
	public void mouseUp(MouseEvent e) {}
}
