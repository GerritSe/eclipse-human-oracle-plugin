package tcg.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.part.ViewPart;

public class ViewContentProvider implements ITreeContentProvider {
	private TreeParent invisibleRoot;
	private TreeModel model;

	public ViewContentProvider(TreeModel model) {
		this.model = model;
		invisibleRoot = new TreeParent("");
		updateView();
	}
	
	public Object[] getElements(Object parent) {
		if (parent == null)
			return getChildren(invisibleRoot);
		
		return getChildren(parent);
	}
	
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
	
	public Object [] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent)parent).getChildren();
		}
		return new Object[0];
	}
	
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}
	
	public void updateView() {
		
	}
}
