package commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import file.ClassFile;
import parser.CustomModelWriter;
import tcg.tree.TreeInstanceManager;

public class ExportCommand extends Action {
	protected TreeInstanceManager treeInstanceManager;
	protected TreeViewer treeViewer;
	
	public ExportCommand(TreeInstanceManager treeInstanceManager, TreeViewer treeViewer) {
		this.treeInstanceManager = treeInstanceManager;
		this.treeViewer = treeViewer;
	}
	
	@Override
	public void run() {
		ClassFile classFile = new ClassFile(treeInstanceManager.getActiveTreeInstance().getFile());
		// As we reached this point, we can safely assume that the next line will throw no error
		JavaClass javaClass = treeInstanceManager.getActiveTreeInstance().getJavaSource().getClasses().get(0);
		ArrayList<JavaMethod> methodBackup = new ArrayList<>();
		
		// Make a backup of all test case methods
		for (JavaMethod method : javaClass.getMethods())
			methodBackup.add(method);
		
		// Remove all methods from actual JavaClass model
		javaClass.getMethods().clear();
		// Add methods again that are marked for export
		prepareMethodsForExport(javaClass.getMethods(), methodBackup);

		try {
			File outFile = new File(classFile.mugglExportFileName());
			System.out.println("Writing to " + classFile.mugglExportFileName());
			FileWriter fileWriter = new FileWriter(outFile);
			CustomModelWriter writer = new CustomModelWriter();
			fileWriter.write(writer.writeSource(javaClass.getSource()).toString());
			fileWriter.close();
		} catch (IOException e) {
			MessageDialog.openError(treeViewer.getControl().getShell(), "I/O Fehler",
					"Testfälle konnten nicht exportiert werden.");
		}
		
		// Restore back to initial state
		javaClass.getMethods().clear();
		for (JavaMethod method : methodBackup)
			javaClass.getMethods().add(method);
	}
	
	private void prepareMethodsForExport(List<JavaMethod> destination, List<JavaMethod> source) {
		for (JavaMethod method : source) {
			if (!methodHasAnnotation(method, "Ignore")) {
				removeCommentsFromMethod(method);
				destination.add(method);
			}
		}
	}
	
	// TODO: This is redundant with a method in DefaultTreeBuilder
	private Boolean methodHasAnnotation(JavaMethod method, String name) {
		for (JavaAnnotation annotation : method.getAnnotations()) {
			if (name.equals(annotation.getType().getName()))
				return true;
		}
		return false;
	}
	
	private void removeCommentsFromMethod(JavaMethod method) {
		// Avoid ConcurrentModificationException
		List<DocletTag> tagsToBeRemoved = new ArrayList<>();

		for (DocletTag tag : method.getTags()) {
			if ("mugglComment".equals(tag.getName()))
				tagsToBeRemoved.add(tag);
		}
		
		for (DocletTag tag : tagsToBeRemoved)
			method.getTags().remove(tag);
	}
}
