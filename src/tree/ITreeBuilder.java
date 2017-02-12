package tree;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

public interface ITreeBuilder {
	public ITreeObject buildTree(TreeInstance treeInstance, JavaSource source) throws ParseException;
}
