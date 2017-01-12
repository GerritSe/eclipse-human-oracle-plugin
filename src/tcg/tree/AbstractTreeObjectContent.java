package tcg.tree;

public abstract class AbstractTreeObjectContent implements ITreeObjectContent {
	protected ITreeObject treeObject;
	protected String description;

	public AbstractTreeObjectContent() { }

	public AbstractTreeObjectContent(TreeObject treeObject) {
		this.treeObject = treeObject;
	}
	
	public String toString() {
		return description;
	}
	
	public ITreeObject getTreeObject() {
		return treeObject;
	}
	
	public void setTreeObject(ITreeObject treeObject) {
		if (this.treeObject != treeObject) {
			this.treeObject = treeObject;
			if (treeObject instanceof TreeObject)
				((TreeObject)treeObject).setContent(this);
		}
	}
}