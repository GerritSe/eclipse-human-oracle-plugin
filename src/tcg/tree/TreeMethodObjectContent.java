package tcg.tree;

import com.thoughtworks.qdox.model.JavaMethod;

public class TreeMethodObjectContent extends AbstractTreeObjectContent {
	protected JavaMethod method;

	
	public TreeMethodObjectContent(JavaMethod method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		return (method == null) ? "" : method.getName();
	}
}