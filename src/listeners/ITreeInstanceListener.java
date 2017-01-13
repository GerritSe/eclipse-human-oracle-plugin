package listeners;

import tcg.tree.ITreeObject;
import tcg.tree.TreeInstance;

public interface ITreeInstanceListener {
	public void onTreeObjectContentChange(TreeInstance treeInstance, ITreeObject treeObject);
}
