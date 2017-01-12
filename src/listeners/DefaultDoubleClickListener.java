package listeners;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class DefaultDoubleClickListener extends AbstractDoubleClickListener {
	public DefaultDoubleClickListener(TreeViewer treeViewer) {
		super(treeViewer);
	}

	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		System.out.println(obj.toString());
	}
}
