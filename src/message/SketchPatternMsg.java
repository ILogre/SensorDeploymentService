package message;

import businessobject.Field;
import transfer.Message;

public class SketchPatternMsg extends Message {

	private String name;
	private Field key;
	private Field[] values;
	
	public SketchPatternMsg(String name, Field key, Field... values) {
		super();
		this.name = name;
		this.key = key;
		this.values = values;
	}

	public String getName() {
		return name;
	}

	public Field getKey() {
		return key;
	}

	public Field[] getValues() {
		return values;
	}
	
}
