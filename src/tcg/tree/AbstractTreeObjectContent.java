package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;

public abstract class AbstractTreeObjectContent implements ITreeObjectContent {
	protected IAdaptable treeObject;
	protected String description;

	public AbstractTreeObjectContent() { }

	public AbstractTreeObjectContent(TreeObject treeObject) {
		this.treeObject = treeObject;
	}
	
	public String toString() {
		return description;
	}
	
	public IAdaptable getTreeObject() {
		return treeObject;
	}
	
	public void setTreeObject(IAdaptable treeObject) {
		if (this.treeObject != treeObject) {
			this.treeObject = treeObject;
			if (treeObject instanceof TreeObject)
				((TreeObject)treeObject).setContent(this);
		}
	}
}