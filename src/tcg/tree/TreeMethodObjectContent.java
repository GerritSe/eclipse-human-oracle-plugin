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
		String methodName = (method == null) ? "(No method name)" : method.getName();
		String exportMessage = export ? "" : " - not exported";
		return String.format("%s%s", methodName, exportMessage);
	}

	public void setExport(boolean export) {
		if (this.export != export) {
			this.export = export;
			if (treeObject != null)
				treeObject.onContentChange();
		}
	}

	public void toggleExport() {
		setExport(!export);
	}
}