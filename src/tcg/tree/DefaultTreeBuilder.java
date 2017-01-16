package tcg.tree;

import java.util.ArrayList;

import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class DefaultTreeBuilder implements ITreeBuilder {
	@Override
	public ITreeObject buildTree(TreeInstance treeInstance, JavaSource source) throws IllegalArgumentException {
		TreeParent root = new TreeParent(null);
		
		if (source.getClasses().size() < 1)
			throw new IllegalArgumentException("The provided Java source appears to have no classes");
		
		for (JavaMethod method: source.getClasses().get(0).getMethods()) {
			TreeParent parent = buildParent(method);
			parent.addChild(buildObject("Return Type: \t\t" + method.getReturnType().getGenericValue()));
			parent.addChild(buildObject("Parameter Types: \t" + buildParameters(method)));
			parent.setTreeObjectListener(treeInstance);
			root.addChild(parent);
		}
		
		return root;
	}
	
	private TreeParent buildParent(JavaMethod method) {
		ITreeObjectContent content = new TreeMethodObjectContent(method);
		return new TreeParent(content);
	}
	
	private TreeObject buildObject(String description) {
		ITreeObjectContent content = new TreePropertyObjectContent(description);
		return new TreeObject(content);
	}
	
	private String buildParameters(JavaMethod method) {
		ArrayList<String> parameterList = new ArrayList<>();

		for (JavaParameter parameter: method.getParameters())
			parameterList.add(parameter.getType().getValue());

		return String.join(", ", parameterList);
	}
}
