package tcg.tree;

import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

public class DefaultTreeBuilder implements ITreeBuilder {
	@Override
	public IAdaptable buildTree(TreeInstance treeInstance, JavaSource source) throws IllegalArgumentException {
		TreeParent root = new TreeParent(null);
		JavaClass javaClass = source.getClasses().get(0);
		
		if (javaClass == null)
			throw new IllegalArgumentException("Java input file has no classes");
		
		for (JavaMethod method: javaClass.getMethods()) {
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
