package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;

public class TreeObject implements IAdaptable {
	protected String name;
	protected TreeParent parent;
	private ITreeEventListener listener = NullEventListener.getInstance();
	
	public TreeObject(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
		listener.change(this);
	}
	
	public String getName() {
		return name;
	}

	public TreeParent getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public <T> T getAdapter(Class<T> key) {
		return null;
	}
	
	public void setListener(ITreeEventListener listener) {
		this.listener = listener;
	}
}