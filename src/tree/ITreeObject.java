package tree;

import org.eclipse.core.runtime.IAdaptable;

import listeners.ITreeObjectListener;

public interface ITreeObject extends IAdaptable {
	public ITreeObjectContent getContent();
	public void setContent(ITreeObjectContent content);
	public void onContentChange();
	public void setTreeObjectListener(ITreeObjectListener listener);
	public ITreeObject getParent();
}
