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
	protected String currentFile;

	public PartListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;
	}



	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		String activeFile = getActiveEditorFile(partRef);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileOpened(activeFile);
		}
	}
	
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		String activeFile = getActiveEditorFile(partRef);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileActivated(activeFile);
		}
	}


	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IEditorInput editorInput;
		
		if (partRef instanceof IEditorReference) {
			try {
				editorInput = ((IEditorReference)partRef).getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					String fileName = ((FileEditorInput)editorInput).getFile().getRawLocation().toOSString();
					workspaceListener.onFileClosed(fileName);
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

	private String getActiveEditorFile(IWorkbenchPartReference partRef) {
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

		if (file == null)
			return null;

		return file.getRawLocation().toOSString();
	}
}
