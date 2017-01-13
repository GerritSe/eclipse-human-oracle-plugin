package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

public interface ITreeBuilder {
	public IAdaptable buildTree(TreeInstance treeInstance, JavaSource source) throws ParseException;
}
