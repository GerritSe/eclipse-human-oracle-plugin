package commands;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import tcg.tree.ITreeObjectContent;
import tcg.tree.TreeMethodObjectContent;
import tcg.tree.TreeParent;

public class SetExportStateCommand implements ICommand<Void> {
	protected TreeViewer treeViewer;
	
	public SetExportStateCommand(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	@Override
	public Void call() {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		
		if (obj != null && obj instanceof TreeParent) {
			ITreeObjectContent content = ((TreeParent) obj).getContent();
			((TreeMethodObjectContent) content).toggleExport();
		}
		return null;
	}
}
