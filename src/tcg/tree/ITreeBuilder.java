package tcg.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.bindings.keys.ParseException;

import com.thoughtworks.qdox.model.JavaSource;

public interface ITreeBuilder {
	public IAdaptable buildTree(JavaSource source) throws ParseException;
}
