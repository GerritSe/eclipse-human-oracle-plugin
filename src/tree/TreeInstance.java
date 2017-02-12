package tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.parser.ParseException;

import file.ClassFile;
import listeners.ITreeObjectListener;
import parser.CustomModelWriter;

public class TreeInstance implements ITreeObjectListener {
	protected TreeInstanceManager treeInstanceManager;
	protected ITreeObject treeInstanceRoot;
	protected IFile file;
	protected JavaSource javaSource;
	protected ClassFile classFile;
	
	public TreeInstance(IFile file) {
		this.file = file;
		classFile = new ClassFile(file);
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
	
	public TreeInstance loadFromMugglFile() throws IOException, ParseException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		javaSource = builder.addSource(new File(classFile.mugglTestFileName()));
		return this;
	}
	
	public void saveToMugglFile() throws IOException {
		File outFile = new File(classFile.mugglTestFileName());
		FileWriter fileWriter = new FileWriter(outFile);
		CustomModelWriter writer = new CustomModelWriter();

		fileWriter.write(writer.writeSource(javaSource).toString());
		fileWriter.close();
	}
	
	public IFile getFile() {
		return file;
	}
	
	public JavaSource getJavaSource() {
		return javaSource;
	}
	
	public TreeInstance buildTree() throws IllegalArgumentException {
		if (treeInstanceRoot == null)
			treeInstanceRoot = (TreeParent) new DefaultTreeBuilder().buildTree(this, javaSource);
		return this;
	}
	
	public ITreeObject getTreeInstanceRoot() {
		return treeInstanceRoot;
	}
	
	public ArrayList<ITreeObject> findRootLevelTreeObjectsByContentDescription(String description) {
		if (treeInstanceRoot == null)
			return null;
		
		ArrayList<ITreeObject> objects = new ArrayList<>();
		
		for (ITreeObject treeObject: ((TreeParent) treeInstanceRoot).getChildren()) {
			String testMethodName = treeObject.getContent().getDescription();

			if (testMethodName.matches(description + "Test\\d+"))
				objects.add(treeObject);
		}
		return objects;
	}

	@Override
	public void onContentChange(ITreeObject treeObject) {
		treeInstanceManager.notifyAbout("contentChange", this, treeObject);
	}
}
