package tcg.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

public class PartListener implements IPartListener {
	private String currentFile;
	private IViewChangeListener listener;

	public PartListener(IViewChangeListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		triggerNotifications(part);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) { }

	@Override
	public void partClosed(IWorkbenchPart part) { }

	@Override
	public void partDeactivated(IWorkbenchPart part) { }

	@Override
	public void partOpened(IWorkbenchPart part) {
		triggerNotifications(part);
	}
	
	private void triggerNotifications(IWorkbenchPart part) {
		String activeFile = getActiveEditorFile(part);
		
		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			listener.change(activeFile);
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
