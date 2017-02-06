package tcg.tree;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultDocletTag;

public class TreeMethodObjectContent extends AbstractTreeObjectContent {
	public static final String JAVA_DOCLET_TAG_NAME = "ExportToFinalMugglFile"; 
	protected Boolean export = true;
	protected JavaMethod method;

	public TreeMethodObjectContent(JavaMethod method) {
		this.method = method;
		setExport(readExportDocletTag());
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
			updateExportDocletTag(export);
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
	
	private Boolean readExportDocletTag() {
		DocletTag docletTag = method.getTagByName(JAVA_DOCLET_TAG_NAME);
		
		if (docletTag == null || !docletTag.getValue().equals("false"))
			return true;
		
		return false;
	}
	
	private void updateExportDocletTag(Boolean export) {
		DocletTag docletTag = method.getTagByName(JAVA_DOCLET_TAG_NAME);
		
		if (docletTag != null)
			method.getTags().remove(docletTag);
		
		if (export == false) {
			docletTag = new DefaultDocletTag(JAVA_DOCLET_TAG_NAME, "false");
			method.getTags().add(docletTag);
		}
	}
}