package listeners;

import org.eclipse.core.resources.IFile;

public interface IWorkspaceListener {
	public void onFileClose(IFile file, IFile activeFile);
	public void onFileOpen(IFile file);
	public void onFileActivate(IFile file);
}
