package tcg.tree;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

import tcg.tree.objects.TreeFunctionObject;
import tcg.tree.objects.TreeFunctionPropertyObject;
import tcg.tree.objects.TreeParent;

public class TreeBuilder {
	JavaSource source;
	
	public TreeBuilder readFromJavaSourceFile(String file) throws IOException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		source = builder.addSource(new File(file));
		return this;
	}
	
	public TreeParent buildTree() {
		TreeParent root = new TreeParent("");
		
		if (source == null || source.getClasses().isEmpty())
			return root;
		
		for (JavaMethod method: source.getClasses().get(0).getMethods()) {
			TreeFunctionObject methodRoot = new TreeFunctionObject(method);
			TreeFunctionPropertyObject parameters = new TreeFunctionPropertyObject(method.getReturnType().getCanonicalName());
			methodRoot.addChild(parameters);
			root.addChild(methodRoot);
		}
		return root;
	}
}
