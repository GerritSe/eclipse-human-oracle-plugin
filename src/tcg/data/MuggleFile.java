package tcg.data;

import java.util.ArrayList;

public class MuggleFile {
	private String filename;
	private ArrayList<Function> functions;
	
	public MuggleFile(String filename, ArrayList<Function> functions) {
		this.filename = filename;
		this.functions = functions;
	}

	public String getFilename() {
		return filename;
	}

	public ArrayList<Function> getFunctions() {
		return functions;
	}
}
