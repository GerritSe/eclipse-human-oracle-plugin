package commands;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import tree.ITreeObjectContent;
import tree.TreeMethodObjectContent;
import tree.TreeParent;

public class SetExportStateCommand extends Action {
	protected TreeViewer treeViewer;
	
	public SetExportStateCommand(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void run() {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		
		if (obj == null || !(obj instanceof TreeParent))
			return;
		
		ITreeObjectContent content = ((TreeParent) obj).getContent();
		((TreeMethodObjectContent) content).toggleExport();
	}
}
