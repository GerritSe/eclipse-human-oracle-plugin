package tree;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;

import listeners.ITreeInstanceListener;

public class TreeInstanceManager {
	protected Map<IFile, TreeInstance> treeInstancePool;
	protected ITreeInstanceListener listener;
	protected TreeInstance activeTreeInstance;
	
	public TreeInstanceManager() {
		treeInstancePool = new HashMap<>();
	}

	public void addTreeInstance(TreeInstance treeInstance) {
		if (treeInstancePool.containsKey(treeInstance.getFile()))
			return;
		
		treeInstancePool.put(treeInstance.getFile(), treeInstance);
		treeInstance.setTreeInstanceManager(this);
	}
	
	public void removeTreeInstanceByFile(IFile file) {
		TreeInstance treeInstance = treeInstancePool.get(file);
		
		if (treeInstance != null) {
			treeInstancePool.remove(file);
			if (treeInstance == activeTreeInstance)
				activeTreeInstance = null;
		}
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
	
	public void setActiveTreeInstance(TreeInstance treeInstance) {
		if (treeInstancePool.containsValue(treeInstance) || treeInstance == null)
			activeTreeInstance = treeInstance;
	}
	
	public TreeInstance getActiveTreeInstance() {
		return activeTreeInstance;
	}
}