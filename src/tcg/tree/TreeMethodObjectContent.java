package tcg.tree;

import com.thoughtworks.qdox.model.JavaMethod;

public class TreeMethodObjectContent extends AbstractTreeObjectContent {
	protected Boolean export = true;
	protected JavaMethod method;

	public TreeMethodObjectContent(JavaMethod method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		return (method == null) ? "" : method.getName();
	}
	
	public void setExport(boolean export) {
		this.export = export;
		if (treeObject != null)
			treeObject.onContentChange();
	}
	
	public void toggleExport() {
		setExport(!export);
	}
}