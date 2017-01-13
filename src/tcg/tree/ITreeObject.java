package tcg.tree;

import listeners.ITreeObjectListener;

public interface ITreeObject {
	public ITreeObjectContent getContent();
	public void setContent(ITreeObjectContent content);
	public void onContentChange();
	public void setTreeObjectListener(ITreeObjectListener listener);
	public ITreeObject getParent();
}
