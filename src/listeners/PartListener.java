package listeners;

import org.eclipse.ui.texteditor.ITextEditor;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;

public class PartListener implements IPartListener2 {
	// If this HashSet contains the IWorkbenchPartReference object,
	// there are already input listeners registered for the related editor.
	protected HashSet<IFile> inputListenerPool;
	protected IWorkspaceListener workspaceListener;
	protected IFile currentFile;
	protected IMethod currentMethodUnderCaret;

	public PartListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;
		inputListenerPool = new HashSet<>();
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
					currentFile = null;
					inputListenerPool.remove(file);
				}
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
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

	private void registerInputListeners(IWorkbenchPartReference partRef) {
		ITextEditor editor = (ITextEditor) partRef.getPage().getActiveEditor();

		if (editor == null)
			return;

		if (inputListenerPool.contains(currentFile))
			return;
		
		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				handleInputEvent(partRef);
			}

			@Override
			public void keyPressed(KeyEvent e) { }
		});

		((StyledText) editor.getAdapter(org.eclipse.swt.widgets.Control.class)).addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) { }

			@Override
			public void mouseDown(MouseEvent e) { }

			@Override
			public void mouseUp(MouseEvent e) {
				handleInputEvent(partRef);
			}
		});
		
		inputListenerPool.add(currentFile);
	}

	private void handleInputEvent(IWorkbenchPartReference partRef) {
		IMethod newMethodUnderCaret = getMethodUnderCaret(partRef);
		
		if (newMethodUnderCaret == null && currentMethodUnderCaret != null) {
			currentMethodUnderCaret = null;
			workspaceListener.onMethodUnderCaretChange(null);
		} else if (newMethodUnderCaret != null && !newMethodUnderCaret.equals(currentMethodUnderCaret)) {
			currentMethodUnderCaret = newMethodUnderCaret;
			workspaceListener.onMethodUnderCaretChange(currentMethodUnderCaret.getElementName());
		}
	}
	
	private IMethod getMethodUnderCaret(IWorkbenchPartReference partRef) {
		ITextEditor editor = (ITextEditor) partRef.getPage().getActiveEditor();
		ITextSelection selection;
		IJavaElement element, selected;

		if (editor == null)
			return null;

		element = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
		if (!(element instanceof ICompilationUnit))
			return null;

		selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		try {
			selected = ((ICompilationUnit) element).getElementAt(selection.getOffset());
			if (selected != null && selected.getElementType() == IJavaElement.METHOD)
				return (IMethod) selected;

		} catch (JavaModelException e) { }
		
		return null;
	}
}
