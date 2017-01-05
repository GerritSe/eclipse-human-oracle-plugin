package tcg.data;

public class MuggleFileParser {
	private String filename;
	
	public MuggleFileParser(String filename) {
		this.filename = filename;
	}
	
	public MuggleFile parse() {
		return new MuggleFile(filename, null);
	}
}
