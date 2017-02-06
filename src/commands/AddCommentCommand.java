package commands;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;

import tcg.tree.TreeMethodObjectContent;
import tcg.tree.TreeParent;

public class AddCommentCommand extends Action {
	protected TreeViewer treeViewer;
	
	public AddCommentCommand(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void run() {
		ISelection selection = treeViewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		TreeParent treeObject;
		TreeMethodObjectContent content;
		
		if (obj == null || !(obj instanceof TreeParent))
			return;
		
		treeObject = (TreeParent) obj;
		if (treeObject.getContent() == null || !(treeObject.getContent() instanceof TreeMethodObjectContent))
			return;
		
		content = (TreeMethodObjectContent) treeObject.getContent();
		InputDialog id = new InputDialog(treeViewer.getControl().getShell(), "Kommentar eingeben",
	            "Kommentar eingeben", "", null);
		if (id.open() == Window.OK && !id.getValue().trim().isEmpty())
			content.addComment(id.getValue());
	}
}
