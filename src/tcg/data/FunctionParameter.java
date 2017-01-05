package tcg.data;

public class FunctionParameter {
	private String name, type, value;

	public FunctionParameter(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}	
}
