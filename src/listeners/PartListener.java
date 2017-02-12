package listeners;

import org.eclipse.ui.texteditor.ITextEditor;

import commands.HandleInputCommand;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class PartListener implements IPartListener2 {
	protected IFile currentFile;
	protected WorkbenchPartReferenceInputListener inputListener;
	protected HandleInputCommand inputHandler;
	protected IWorkspaceListener workspaceListener;

	public PartListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;
		inputHandler = new HandleInputCommand();
		inputHandler.setWorkspaceListener(workspaceListener);
		inputListener = new WorkbenchPartReferenceInputListener();
		inputListener.setEventHandler(inputHandler);
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		IFile activeFile = getActiveEditorFile(partRef);

		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileOpen(activeFile);
			registerInputListeners(partRef);
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		IFile activeFile = getActiveEditorFile(partRef);

		if (activeFile != null && !activeFile.equals(currentFile)) {
			currentFile = activeFile;
			workspaceListener.onFileActivate(activeFile);
			registerInputListeners(partRef);
		}
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IEditorInput editorInput;

		if (partRef instanceof IEditorReference) {
			try {
				editorInput = ((IEditorReference) partRef).getEditorInput();
				if (editorInput instanceof FileEditorInput) {
					IFile file = ((FileEditorInput) editorInput).getFile();
					workspaceListener.onFileClose(file, getActiveEditorFile(partRef));
				}
			} catch (PartInitException e) {
				
			} finally {
				currentFile = null;
			}
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		if (!(partRef instanceof IEditorReference))
			return;
		
		ITextEditor editor = (ITextEditor) ((IEditorReference) partRef).getEditor(true);

		if (editor == null)
			return;
		
		if (currentFile != null && currentFile.equals(getActiveEditorFile(partRef)))
			return;
		
		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).removeKeyListener(inputListener);
		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).removeMouseListener(inputListener);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	private IFile getActiveEditorFile(IWorkbenchPartReference partRef) {
		IEditorPart editorPart;
		IEditorInput editorInput;
		IFile file;

		editorPart = partRef.getPage().getActiveEditor();

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

	/**
	 * Taken from https://www.eclipse.org/forums/index.php/t/385945/
	 */
	private void registerInputListeners(IWorkbenchPartReference partRef) {
		ITextEditor editor = (ITextEditor) partRef.getPage().getActiveEditor();

		if (editor == null)
			return;

		inputHandler.setWorkbenchPartReference(partRef);
		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(inputListener);
		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addMouseListener(inputListener);
	}
}
