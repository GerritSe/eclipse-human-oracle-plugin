package tcg.tree;

import java.util.HashMap;
import java.util.Map;

import listeners.ITreeInstanceListener;

public class TreeInstanceManager {
	protected Map<String, TreeInstance> treeInstancePool;
	protected ITreeInstanceListener listener;
	
	public TreeInstanceManager() {
		treeInstancePool = new HashMap<>();
	}

	public void addTreeInstance(TreeInstance treeInstance) {
		if (treeInstancePool.containsKey(treeInstance.getMuggleFileName()))
			return;
		
		treeInstancePool.put(treeInstance.getMuggleFileName(), treeInstance);
	}
	
	public void removeTreeInstanceByMuggleFileName(String muggleFileName) {
		treeInstancePool.remove(muggleFileName);
	}
	
	public TreeInstance findTreeInstanceByMuggleFileName(String muggleFileName) {
		return treeInstancePool.get(muggleFileName);
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