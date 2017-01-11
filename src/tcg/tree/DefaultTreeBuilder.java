package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.bindings.keys.ParseException;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

public class DefaultTreeBuilder implements ITreeBuilder {
	@Override
	public IAdaptable buildTree(JavaSource source) throws ParseException {
		TreeParent root = new TreeParent(null);
		JavaClass javaClass = source.getClasses().get(0);
		
		if (javaClass == null)
			throw new ParseException("Illegal Java input file");
		
		for (JavaMethod method: javaClass.getMethods()) {
			TreeParent parent = buildParent(method);
			parent.addChild(buildObject(method.getReturnType().toString()));
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
}
