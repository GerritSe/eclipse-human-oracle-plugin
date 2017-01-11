package tcg.tree;

import java.io.File;
import java.io.IOException;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;

public class TreeInstance {
	protected TreeInstanceManager treeInstanceManager;
	protected String muggleFileName;
	protected JavaSource javaSource;
	
	public TreeInstance(TreeInstanceManager treeInstanceManger, String muggleFileName) {
		this.muggleFileName = muggleFileName;
		setTreeInstanceManager(treeInstanceManger);
	}
	
	public void setTreeInstanceManager(TreeInstanceManager treeInstanceManager) {
		if (this.treeInstanceManager != treeInstanceManager) {
			this.treeInstanceManager = treeInstanceManager;
			treeInstanceManager.addTreeInstance(this);
		}
	}
	
	public void loadFromMuggleFile() throws IOException {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		javaSource = builder.addSource(new File(muggleFileName));
	}
	
	public String getMuggleFileName() {
		return muggleFileName;
	}
}
