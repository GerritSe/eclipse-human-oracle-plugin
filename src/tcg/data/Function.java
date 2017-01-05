package tcg.data;

import java.util.ArrayList;

public class Function {
	private String name, visibility;
	private ArrayList<FunctionParameter> parameters;
	private boolean checkedIn = true;
	
	public Function(String name, String visibility, ArrayList<FunctionParameter> parameters) {
		this.name = name;
		this.visibility = visibility;
		this.parameters = parameters == null ? new ArrayList<FunctionParameter>() : parameters;
	}

	public String getName() {
		return name;
	}

	public String getVisibility() {
		return visibility;
	}

	public ArrayList<FunctionParameter> getParameters() {
		return parameters;
	}
	
	public boolean isCheckedIn() {
		return checkedIn;
	}
}