package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	protected TreeParent parent;
	protected ITreeObjectContent content;

	public TreeObject(ITreeObjectContent content) {
		setContent(content);
	}
	
	public String toString() {
		return (content == null) ? "" : content.toString();
	}
	
	public void setContent(ITreeObjectContent content) {
		if (content == null)
			return;
		
		if (this.content != content) {
			this.content = content;
			content.setTreeObject(this);
		}
	}
	
	public TreeParent getParent() {
		return parent;
	}

	public <T> T getAdapter(Class<T> key) {
		return null;
	}
}