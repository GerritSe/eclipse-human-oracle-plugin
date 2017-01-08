package tcg.tree;

import tcg.tree.objects.TreeObject;
import tcg.tree.objects.TreeParent;

public interface ITreeEventListener {
	public void change(TreeObject treeObject);
	public void reload(TreeParent invisibleRoot);
}