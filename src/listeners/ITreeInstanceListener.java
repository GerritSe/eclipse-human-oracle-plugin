package listeners;

import tree.ITreeObject;
import tree.TreeInstance;

public interface ITreeInstanceListener {
	public void onTreeObjectContentChange(TreeInstance treeInstance, ITreeObject treeObject);
}
