package tcg.tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import listeners.ITreeInstanceListener;

public class TreeInstanceManager {
	protected Map<IFile, TreeInstance> treeInstancePool;
	protected ITreeInstanceListener listener;
	
	public TreeInstanceManager() {
		treeInstancePool = new HashMap<>();
	}

	public void addTreeInstance(TreeInstance treeInstance) {
		if (treeInstancePool.containsKey(treeInstance.getFile()))
			return;
		
		treeInstancePool.put(treeInstance.getFile(), treeInstance);
	}
	
	public void removeTreeInstanceByFile(IFile file) {
		treeInstancePool.remove(file);
	}
	
	public TreeInstance findTreeInstanceByFile(IFile file) {
		return treeInstancePool.get(file);
	}
	
	public void setTreeInstanceListener(ITreeInstanceListener listener) {
		this.listener = listener;
	}
	
	public void notifyAbout(String event, TreeInstance treeInstance, Object attribute) {
		if (listener == null)
			return;

		switch (event) {
		case "contentChange":
			listener.onTreeObjectContentChange(treeInstance, (ITreeObject) attribute);
		}
	}
}