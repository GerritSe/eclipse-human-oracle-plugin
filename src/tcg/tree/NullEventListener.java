package tcg.tree;

public class NullEventListener implements ITreeEventListener {
	private static NullEventListener instance = new NullEventListener();

	public static NullEventListener getInstance() {
		return instance;
	}
	
	private NullEventListener() {}
	
	@Override
	public void change(TreeObject treeObject) {}
}
