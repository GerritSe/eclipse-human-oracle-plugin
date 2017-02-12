package tree;

import listeners.ITreeObjectListener;

public class TreeObject implements ITreeObject {
	protected TreeParent parent;
	protected ITreeObjectContent content;
	protected ITreeObjectListener listener;

	public TreeObject(ITreeObjectContent content) {
		setContent(content);
	}
	
	public String toString() {
		return (content == null) ? "" : content.toString();
	}
	
	public ITreeObjectContent getContent() {
		return content;
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

	@Override
	public void onContentChange() {
		if (listener != null)
			listener.onContentChange(this);
	}

	@Override
	public void setTreeObjectListener(ITreeObjectListener listener) {
		this.listener = listener;
	}
}