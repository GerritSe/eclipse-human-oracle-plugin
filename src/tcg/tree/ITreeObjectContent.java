package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;

public interface ITreeObjectContent {
	public String toString();
	public IAdaptable getTreeObject();
	public void setTreeObject(IAdaptable content);
}
