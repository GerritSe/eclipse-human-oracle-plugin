package tcg.tree;

public interface ITreeEventListener {
	public void change(TreeObject treeObject);
	public void reload(TreeParent invisibleRoot);
}
