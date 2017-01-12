package listeners;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

public class PartListener implements IPartListener {
	protected IWorkspaceListener workspaceListener;
	protected String currentFile;
	
	public PartListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;	
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		triggerNotifications(part, "activated");
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) { }

	@Override
	public void partClosed(IWorkbenchPart part) {
		triggerNotifications(part, "closed");
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) { }

	@Override
	public void partOpened(IWorkbenchPart part) {
		triggerNotifications(part, "opened");
	}
	
	private void triggerNotifications(IWorkbenchPart part, String eventType) {
		String activeFile = getActiveEditorFile(part);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			if (workspaceListener != null) {
				switch (eventType) {
					case "opened":
						workspaceListener.onFileOpened(currentFile);
						break;
					case "closed":
						workspaceListener.onFileClosed(currentFile);
						break;
					case "activated":
						workspaceListener.onFileActivated(currentFile);
				}
			}
		}
	}
	
	private String getActiveEditorFile(IWorkbenchPart part) {
    	IEditorPart editorPart;
    	IEditorInput editorInput;
    	IFile file;

    	editorPart = part.getSite().getPage().getActiveEditor();
    	
    	if (editorPart == null)
    		return null;
    	
    	editorInput = editorPart.getEditorInput();
    	
    	if (editorInput == null)
    		return null;
    	
    	if (!(editorInput instanceof FileEditorInput))
    		return null;
    	
    	file = ((FileEditorInput)editorInput).getFile();
    	
    	if (file == null)
    		return null;
    	
    	return file.getRawLocation().toOSString();
	}
}
