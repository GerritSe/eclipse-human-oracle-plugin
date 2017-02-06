package tcg.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

public class DefaultTreeBuilder implements ITreeBuilder {
	protected HashMap<String, String> fieldMap;

	public DefaultTreeBuilder() {
		fieldMap = new HashMap<>();
	}
	
	@Override
	public ITreeObject buildTree(TreeInstance treeInstance, JavaSource source) throws IllegalArgumentException {
		JavaClass klass;
		TreeParent root = new TreeParent(null);
		
		if (source.getClasses().size() < 1)
			throw new IllegalArgumentException("Die Datei enthält keine Java-Klasse.");
		
		klass = source.getClasses().get(0);
		buildFieldMap(klass);
		
		for (JavaMethod method: klass.getMethods()) {
			if (!isTestMethod(method))
				continue;
			
			TreeParent parent = buildParent(method);
			
			String exceptionType;
			if ((exceptionType = getExceptionTypeFromMethod(method)) != null) {
				// The Method we currently build is expected to throw an exception
				parent.addChild(buildObject("Expected: \t" + exceptionType));
			} else {
				String[] args = getAssertionParameterNamesFromMethod(method);
				
				if (args == null)
					parent.addChild(buildObject("Die Methode konnte nicht geparst werden."));
				else { 
					parent.addChild(buildObject("Erwartet: \t" + fieldMap.get(args[0])));
					for (Integer i = 1; i < args.length; i++)
						parent.addChild(buildObject("Parameter " + i + ": \t" + fieldMap.get(args[i])));
				}
			}

			// Read developer comments from DocTags
			String[] comments = getCommentsFromMethod(method);
			for (String comment: comments)
				parent.addChild(buildObject("Kommentar: \t" + comment));
			
			parent.setTreeObjectListener(treeInstance);
			root.addChild(parent);
		}
		
		return root;
	}
	
	private TreeParent buildParent(JavaMethod method) {
		ITreeObjectContent content = new TreeMethodObjectContent(method);
		return new TreeParent(content);
	}
	
	private TreeObject buildObject(String description) {
		ITreeObjectContent content = new TreePropertyObjectContent(description);
		return new TreeObject(content);
	}
	
	private Boolean isTestMethod(JavaMethod method) {
		for (JavaAnnotation annotation: method.getAnnotations()) {
			if ("Test".equals(annotation.getType().getValue()))
				return true;
		}
		return false;
	}
	
	private String getExceptionTypeFromMethod(JavaMethod method) {
		Pattern pattern = Pattern.compile("(?s)try\\s*\\{.*\\}\\s*catch\\s*\\((\\S*).*\\)\\s*\\{.*\\}");
		Matcher matcher = pattern.matcher(method.getSourceCode());
		// We found a try catch pattern
		if (matcher.find())
			return matcher.group(1);
		return null;
	}
	
	private String[] getAssertionParameterNamesFromMethod(JavaMethod method) {
		String[] result = new String[1];
		Pattern pattern = Pattern.compile("assertEquals\\(this\\.(\\w*),\\s*((.*))\\);");
		Matcher matcher = pattern.matcher(method.getSourceCode());
		
		if (matcher.find()) {
			// This is what the assertions expects to get
			String expectedResult = matcher.group(1);
			// Call to method under test is in group 2
			String[] methodCallParameters = getParametersNamesFromMethodCall(matcher.group(2));
			
			if (methodCallParameters != null) {
				result = new String[methodCallParameters.length + 1];
				
				for (Integer i = 0; i < methodCallParameters.length; i++)
					result[i + 1] = methodCallParameters[i];
			}
			
			result[0] = expectedResult;
			return result;
		}
		return null;		
	}
	
	private String[] getParametersNamesFromMethodCall(String methodCall) {
		String result[] = null;
		
		Pattern pattern = Pattern.compile(".*\\((.*)\\)");
		Matcher matcher = pattern.matcher(methodCall);
		System.out.println("Call: " + methodCall);
		if (matcher.find()) {
			String parameters[] = matcher.group(1).split(",");
			result = new String[parameters.length];

			try {
				for (Integer i = 0; i < parameters.length; i++)
					result[i] = parameters[i].split("\\.")[1].trim();
			} catch (ArrayIndexOutOfBoundsException e) {
				// Parameter had no proper this.name format
			}
		}
		
		return result;
	}
	
	private void buildFieldMap(JavaClass klass) {
		/**
		 *  Read fields and put them into the fieldMap as "name => intitial value"
 		 *  if they have an initial value assigned
		 */
		for (JavaField field : klass.getFields()) {
			if (field.getInitializationExpression().isEmpty())
				continue;
			fieldMap.put(field.getName(), field.getInitializationExpression());
		}
		
		/**
		 * Read initial values for fields that are initialized in the setup method.
		 * We assume about the setup method, that it always looks like this:
		 *   this.fieldName  = fieldValue; <NEW LINE>
		 *   this.fieldName2 = fieldValue2; <NEW LINE>
		 *   etc.
		 */
		JavaMethod beforeMethod = findBeforeMethod(klass);
		if (beforeMethod == null)
			return;
		
		String[] expressions = beforeMethod.getSourceCode().split("\n");
		for (String expression : expressions) {
			String[] tokens = expression.trim().split("^this\\.|=|;$");
			if (tokens.length != 3)
				continue;

			fieldMap.put(tokens[1].trim(), tokens[2].trim());
		}
	}
	
	private JavaMethod findBeforeMethod(JavaClass klass) {
		for (JavaMethod method : klass.getMethods()) {
			if (methodHasAnnotation(method, "Before"))
				return method;
		}
		return null;
	}
	
	private Boolean methodHasAnnotation(JavaMethod method, String name) {
		for (JavaAnnotation annotation : method.getAnnotations()) {
			if (name.equals(annotation.getType().getName()))
				return true;
		}
		return false;
	}
	
	private String[] getCommentsFromMethod(JavaMethod method) {
		ArrayList<String> comments = new ArrayList<>();
		
		for (DocletTag tag: method.getTags()) {
			if ("mugglComment".equals(tag.getName()))
				comments.add(tag.getValue());
		}
		
		return (String[]) comments.toArray(new String[comments.size()]);
	}
}
