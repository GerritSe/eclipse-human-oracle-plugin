package tree;

import java.util.ArrayList;

import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultDocletTag;
import com.thoughtworks.qdox.model.impl.DefaultJavaAnnotation;
import com.thoughtworks.qdox.model.impl.DefaultJavaClass;

public class TreeMethodObjectContent extends AbstractTreeObjectContent {
	public static final String JAVA_DOCLET_TAG_NAME = "ExportToFinalMugglFile"; 
	protected Boolean export = true;
	protected JavaMethod method;

	public TreeMethodObjectContent(JavaMethod method) {
		this.method = method;
		setExport(!methodHasIgnoreAnnotation());
	}

	@Override
	public String toString() {
		String exportMessage = export ? "" : " - ungeeignet";
		return String.format("%s%s", getDescription(), exportMessage);
	}

	@Override
	public String getDescription() {
		return (method == null) ? "" : method.getName();
	}
	
	public void setExport(Boolean export) {
		if (this.export != export) {
			this.export = export;
			updateIgnoreAnnotation(!this.export);
			if (treeObject != null)
				treeObject.onContentChange();
		}
	}

	public void toggleExport() {
		setExport(!export);
	}
	
	public void addComment(String comment) {
		method.getTags().add(new DefaultDocletTag("mugglComment", comment));
		if (treeObject != null) {
			((TreeParent) treeObject).addChild(new TreeObject(new TreePropertyObjectContent("Kommentar: \t" + comment)));
			treeObject.onContentChange();
		}
	}
	
	private Boolean methodHasIgnoreAnnotation() {
		for (JavaAnnotation annotation : method.getAnnotations()) {
			if (annotation.getType().getName().equals("Ignore"))
				return true;
		}
		return false;
	}
	
	private void updateIgnoreAnnotation(Boolean ignore) {
		ArrayList<JavaAnnotation> annotationsToBeRemoved = new ArrayList<>();
	
		for (JavaAnnotation annotation : method.getAnnotations()) {
			if (annotation.getType().getName().equals("Ignore"))
				annotationsToBeRemoved.add(annotation);
		}
		
		// To prevent ConcurrentModificationException
		for (JavaAnnotation annotation : annotationsToBeRemoved)
			method.getAnnotations().remove(annotation);
		
		if (ignore)
			method.getAnnotations().add(new DefaultJavaAnnotation(new DefaultJavaClass("Ignore"), 0));
	}
}