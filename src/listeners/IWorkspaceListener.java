package listeners;

public interface IWorkspaceListener {
	public void onFileClosed(String fileName);
	public void onFileOpened(String fileName);
	public void onFileActivated(String fileName);
}
