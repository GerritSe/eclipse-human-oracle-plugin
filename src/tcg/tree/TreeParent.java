package tcg.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeParent extends TreeObject {
	private List<TreeObject> children;

	public TreeParent(ITreeObjectContent content) {
		super(content);
		children = new ArrayList<TreeObject>();
	}
	
	public void addChild(TreeObject child) {
		children.add(child);
		child.parent = this;
	}
	
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.parent = null;
	}

	public TreeObject [] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	@Override
	public void onContentChange() {
		if (listener != null)
			listener.onContentChange(this);
	}
}