package listeners;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class PartListener implements IPartListener2 {
	protected IWorkspaceListener workspaceListener;
	protected IFile currentFile;

	public PartListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;
	}



	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		IFile activeFile = getActiveEditorFile(partRef);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileOpen(activeFile);
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IFile activeFile = getActiveEditorFile(partRef);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileActivate(activeFile);
		}
	}


	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IEditorInput editorInput;
		
		if (partRef instanceof IEditorReference) {
			try {
				editorInput = ((IEditorReference)partRef).getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					IFile file = ((FileEditorInput)editorInput).getFile();
					workspaceListener.onFileClose(file, getActiveEditorFile(partRef));
					currentFile = null;
				}
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) { }

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) { }

	@Override
	public void partHidden(IWorkbenchPartReference partRef) { }

	@Override
	public void partVisible(IWorkbenchPartReference partRef) { }

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) { }

	private IFile getActiveEditorFile(IWorkbenchPartReference partRef) {
		IEditorPart editorPart;
		IEditorInput editorInput;
		IFile file;

		editorPart = partRef.getPart(false).getSite().getPage().getActiveEditor();

		if (editorPart == null)
			return null;

		editorInput = editorPart.getEditorInput();

		if (editorInput == null)
			return null;

		if (!(editorInput instanceof FileEditorInput))
			return null;

		file = ((FileEditorInput) editorInput).getFile();

		return file;
	}
}
