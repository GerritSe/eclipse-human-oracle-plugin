package tcg.tree;

import tcg.tree.objects.TreeObject;
import tcg.tree.objects.TreeParent;

public class NullEventListener implements ITreeEventListener {
	private static NullEventListener instance = new NullEventListener();

	public static NullEventListener getInstance() {
		return instance;
	}
	
	private NullEventListener() {}
	
	@Override
	public void change(TreeObject treeObject) {}

	@Override
	public void reload(TreeParent invisibleRoot) {}
}
