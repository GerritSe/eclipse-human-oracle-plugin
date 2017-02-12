package parser;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaConstructor;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.expression.Expression;
import com.thoughtworks.qdox.writer.ModelWriter;
import com.thoughtworks.qdox.writer.impl.DefaultModelWriter;
import com.thoughtworks.qdox.writer.impl.IndentBuffer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * This CustomModelWriter is needed because the DefaultModelWriter
 * adds way too many white lines and expands Types to their full
 * canonical name (String => java.lang.String), which will then be
 * displayed this way the next time the saved test file is parsed.
 * 
 * Slightly modified from QDox
 * https://github.com/paul-hammant/qdox
 */
public class CustomModelWriter extends DefaultModelWriter {
	/** {@inheritDoc} */
	@Override
	public ModelWriter writeMethod(JavaMethod method) {
		IndentBuffer buffer = getBuffer();

		commentHeader(method);
		writeAccessibilityModifier(method.getModifiers());
		writeNonAccessibilityModifiers(method.getModifiers());
		buffer.write(method.getReturnType().getValue());
		buffer.write(' ');
		buffer.write(method.getName());
		buffer.write('(');
		for (ListIterator<JavaParameter> iter = method.getParameters().listIterator(); iter.hasNext();) {
			writeParameter(iter.next());
			if (iter.hasNext()) {
				buffer.write(", ");
			}

		}
		buffer.write(')');
		if (method.getExceptions().size() > 0) {
			buffer.write(" throws ");
			for (Iterator<JavaClass> excIter = method.getExceptions().iterator(); excIter.hasNext();) {
				buffer.write(excIter.next().getValue());
				if (excIter.hasNext()) {
					buffer.write(", ");
				}
			}
		}
		if (method.getSourceCode() != null && method.getSourceCode().length() > 0) {
			buffer.write(" {");
			buffer.write(method.getSourceCode());
			buffer.write('}');
			buffer.newline();
		} else {
			buffer.write(';');
			buffer.newline();
		}
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ModelWriter writeParameter(JavaParameter parameter) {
		IndentBuffer buffer = getBuffer();

		commentHeader(parameter);
		buffer.write(parameter.getValue());
		if (parameter.isVarArgs()) {
			buffer.write("...");
		}
		buffer.write(' ');
		buffer.write(parameter.getName());
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public ModelWriter writeField(JavaField field) {
		IndentBuffer buffer = getBuffer();

		commentHeader(field);
		writeAllModifiers(field.getModifiers());
		if (!field.isEnumConstant()) {			
			buffer.write(field.getType().getValue());
			buffer.write(' ');
		}
		buffer.write(field.getName());

		if (field.isEnumConstant()) {
			if (field.getEnumConstantArguments() != null && !field.getEnumConstantArguments().isEmpty()) {
				buffer.write("( ");
				for (Iterator<Expression> iter = field.getEnumConstantArguments().listIterator(); iter.hasNext();) {
					buffer.write(iter.next().getParameterValue().toString());
					if (iter.hasNext()) {
						buffer.write(", ");
					}
				}
				buffer.write(" )");
			}
			if (field.getEnumConstantClass() != null) {
				writeClassBody(field.getEnumConstantClass());
			}
		} else {
			if (field.getInitializationExpression() != null && field.getInitializationExpression().length() > 0) {
				{
					buffer.write(" = ");
				}
				buffer.write(field.getInitializationExpression());
			}
		}
		buffer.write(';');
		return this;
	}

	private ModelWriter writeClassBody(JavaClass cls) {
		IndentBuffer buffer = getBuffer();

		buffer.write(" {");
		buffer.newline();
		buffer.indent();

		// fields
		for (JavaField javaField : cls.getFields()) {
			buffer.newline();
			writeField(javaField);
		}

		// constructors
		for (JavaConstructor javaConstructor : cls.getConstructors()) {
			buffer.newline();
			writeConstructor(javaConstructor);
		}

		// methods
		for (JavaMethod javaMethod : cls.getMethods()) {
			buffer.newline();
			writeMethod(javaMethod);
		}

		// inner-classes
		for (JavaClass innerCls : cls.getNestedClasses()) {
			buffer.newline();
			writeClass(innerCls);
		}

		buffer.deindent();
		buffer.newline();
		buffer.write('}');
		buffer.newline();
		return this;
	}

	private void writeNonAccessibilityModifiers(Collection<String> modifiers) {
		IndentBuffer buffer = getBuffer();

		for (String modifier : modifiers) {
			if (!modifier.startsWith("p")) {
				buffer.write(modifier);
				buffer.write(' ');
			}
		}
	}

	private void writeAccessibilityModifier(Collection<String> modifiers) {
		IndentBuffer buffer = getBuffer();

		for (String modifier : modifiers) {
			if (modifier.startsWith("p")) {
				buffer.write(modifier);
				buffer.write(' ');
			}
		}
	}

	private void writeAllModifiers(List<String> modifiers) {
		IndentBuffer buffer = getBuffer();

		for (String modifier : modifiers) {
			buffer.write(modifier);
			buffer.write(' ');
		}
	}
}
