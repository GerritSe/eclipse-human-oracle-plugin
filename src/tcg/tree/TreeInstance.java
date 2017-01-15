package tcg.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

import listeners.ITreeObjectListener;
import parser.CustomModelWriter;

public class TreeInstance implements ITreeObjectListener {
	protected TreeInstanceManager treeInstanceManager;
	protected ITreeObject treeInstanceRoot;
	protected IFile file;
	protected JavaSource javaSource;
	
	public TreeInstance(IFile file) {
		this.file = file;
	}
	
	public TreeInstance(TreeInstanceManager treeInstanceManger, IFile file) {
		this(file);
		setTreeInstanceManager(treeInstanceManger);
	}
	
	public TreeInstance setTreeInstanceManager(TreeInstanceManager treeInstanceManager) {
		if (this.treeInstanceManager != treeInstanceManager) {
			this.treeInstanceManager = treeInstanceManager;
			treeInstanceManager.addTreeInstance(this);
		}
		return this;
	}
	
	public TreeInstance loadFromMuggleFile() throws IOException, ParseException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		javaSource = builder.addSource(new File(file.getRawLocation().toOSString()));
		return this;
	}
	
	public void saveToMuggleFile() throws IOException {
		File outFile = new File(file.getRawLocation().toOSString());
		FileWriter fileWriter = new FileWriter(outFile);
		CustomModelWriter writer = new CustomModelWriter();

		fileWriter.write(writer.writeSource(javaSource).toString());
		fileWriter.close();
	}
	
	public IFile getFile() {
		return file;
	}
	
	public TreeInstance buildTree() throws IllegalArgumentException {
		if (treeInstanceRoot == null)
			treeInstanceRoot = (TreeParent) new DefaultTreeBuilder().buildTree(this, javaSource);
		return this;
	}
	
	public ITreeObject getTreeInstanceRoot() {
		return treeInstanceRoot;
	}

	@Override
	public void onContentChange(ITreeObject treeObject) {
		treeInstanceManager.notifyAbout("contentChange", this, treeObject);
	}
}
