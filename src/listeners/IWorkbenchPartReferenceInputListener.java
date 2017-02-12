package listeners;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;

public interface IWorkbenchPartReferenceInputListener extends MouseListener, KeyListener {
	public void setEventHandler(Action handler);
}
