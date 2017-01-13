package tcg.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

import listeners.ITreeObjectListener;

public class TreeInstance implements ITreeObjectListener {
	protected TreeInstanceManager treeInstanceManager;
	protected ITreeObject treeInstanceRoot;
	protected String muggleFileName;
	protected JavaSource javaSource;
	
	public TreeInstance(String muggleFileName) {
		this.muggleFileName = muggleFileName;
	}
	
	public TreeInstance(TreeInstanceManager treeInstanceManger, String muggleFileName) {
		this(muggleFileName);
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
		javaSource = builder.addSource(new File(muggleFileName));
		return this;
	}
	
	public void saveToMuggleFile() throws IOException {
		File file = new File(muggleFileName);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(javaSource.getCodeBlock());
		fileWriter.close();
	}
	
	public String getMuggleFileName() {
		return muggleFileName;
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
