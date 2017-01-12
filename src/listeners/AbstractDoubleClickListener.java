package listeners;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;

public abstract class AbstractDoubleClickListener implements IDoubleClickListener {
	protected TreeViewer treeViewer;
	
	public AbstractDoubleClickListener(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
}
