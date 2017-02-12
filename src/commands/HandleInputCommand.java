package commands;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

import listeners.IWorkspaceListener;

public class HandleInputCommand extends Action {
	protected IWorkbenchPartReference partRef;
	protected IMethod currentMethodUnderCaret;
	protected IWorkspaceListener workspaceListener;
	
	@Override
	public void run() {
		IMethod newMethodUnderCaret = getMethodUnderCaret(partRef);
		
		if (newMethodUnderCaret == null && currentMethodUnderCaret != null) {
			currentMethodUnderCaret = null;
			workspaceListener.onMethodUnderCaretChange(null);
		} else if (newMethodUnderCaret != null && !newMethodUnderCaret.equals(currentMethodUnderCaret)) {
			currentMethodUnderCaret = newMethodUnderCaret;
			workspaceListener.onMethodUnderCaretChange(currentMethodUnderCaret.getElementName());
		}
	}
	
	public void setWorkbenchPartReference(IWorkbenchPartReference partRef) {
		this.partRef = partRef;
	}
	
	public void setWorkspaceListener(IWorkspaceListener workspaceListener) {
		this.workspaceListener = workspaceListener;
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
