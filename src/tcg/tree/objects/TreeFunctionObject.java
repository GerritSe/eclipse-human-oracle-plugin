package tcg.tree.objects;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.impl.DefaultDocletTag;

public class TreeFunctionObject extends TreeParent {
	private final String DOCLET_TAG_NAME = "ExportForATCG";
	private boolean active;
	private JavaMethod method;
	
	public TreeFunctionObject(JavaMethod method) {
		super("");
		this.method = method;
		active = readExportDocletTag();
	}
	
	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("This class does not support setting a custom name");
	}
	
	@Override
	public String getName() {
		return method.getName();
	}
	
	public void toggleActive() {
		setActive(!active);
		setExportDocletTag(active);
	}
	
	public void setActive(boolean active) {
		this.active = active;
		listener.change(this);
	}
	
	public boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		String activeString = active || getName().isEmpty() ? "" : " - Not exported";
		return String.format("%s%s", getName(), activeString);
	}
	
	private boolean readExportDocletTag() {
		DocletTag tag = method.getTagByName(DOCLET_TAG_NAME);
		
		if (tag == null || tag.getValue() == "true")
			return true;
		
		return false;
	}
	
	private void setExportDocletTag(boolean active) {
		DocletTag tag = method.getTagByName(DOCLET_TAG_NAME);
		DocletTag newTag;
		
		if (tag != null)
			method.getTags().remove(tag);
		
		if (active == true) {
			System.out.println(method.getCodeBlock());
			return;
		}
		
		newTag = new DefaultDocletTag(DOCLET_TAG_NAME, "false");
		method.getTags().add(newTag);
		System.out.println(method.getCodeBlock());
	}
}
