package tcg.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import tcg.tree.ITreeEventListener;

public class ViewContentProvider implements ITreeContentProvider, ITreeEventListener {
	protected TreeParent invisibleRoot;
	protected TreeViewer viewer;

	public ViewContentProvider() {
		invisibleRoot = new TreeParent("");
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer)viewer;
		if (newInput != null && newInput instanceof TreeObject)
			addListeners((TreeObject) newInput);
	}
	
	private void addListeners(TreeObject treeObject) {
		treeObject.setListener(this);
		if (treeObject instanceof TreeParent) {
			for (TreeObject treeChild: ((TreeParent) treeObject).getChildren())
				addListeners(treeChild);
		}
	}
	
	public Object[] getElements(Object parent) {
		if (parent.getClass().getSimpleName().equals("ViewSite"))
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

	@Override
	public void change(TreeObject treeObject) {
		viewer.refresh(treeObject.getParent(), true);
	}
	
	@Override
	public void reload(TreeParent invisibleRoot) {
		this.invisibleRoot = invisibleRoot;
		viewer.setInput(invisibleRoot);
	}
}
