package file;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;

public class ClassFile {
	protected IFile file;

	public ClassFile(IFile file) {
		this.file = file;
	}
	
	public String mugglTestFileName() throws FileNotFoundException {
		String[] segments = replaceLastFolderName("src", "test");
		Integer length = segments.length;
		
		segments[length - 1] = addSuffixToFileName(segments[length - 1], "Test");
		return "/".concat(String.join("/", segments));		
	}
	
	public String mugglExportFileName() throws FileNotFoundException {
		String[] segments = replaceLastFolderName("src", "test");
		Integer length = segments.length;
		
		segments[length - 1] = addSuffixToFileName(segments[length - 1], "Final");
		return "/".concat(String.join("/", segments));
	}
	
	private String[] replaceLastFolderName(String replace, String replaceBy) throws FileNotFoundException {
		String[] segments = file.getRawLocation().segments();
		Integer length = segments.length;
		
		for (int i = length - 1; i >= 0; i--) {
			if (segments[i].equals(replace)) {
				segments[i] = replaceBy;
				return segments;
			}
		}
		
		throw new FileNotFoundException(replace + "-Verzeichnis nicht gefunden.");
	}
	
	// Input as i.e. SomeClass.java is expected
	private String addSuffixToFileName(String fileName, String suffix) {
		Pattern pattern = Pattern.compile("(.*)\\.java");
		Matcher matcher = pattern.matcher(fileName);
		
		if (!matcher.find())
			return null;
		
		return matcher.group(1).concat(suffix).concat(".java");
	}
}
