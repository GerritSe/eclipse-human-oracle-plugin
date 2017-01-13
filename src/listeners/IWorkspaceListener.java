package listeners;

public interface IWorkspaceListener {
	public void onFileClose(String fileName, String activeFileName);
	public void onFileOpen(String fileName);
	public void onFileActivate(String fileName);
}
