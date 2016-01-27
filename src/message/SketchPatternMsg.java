package message;

import businessobject.Field;
import transfer.Message;

public class SketchPatternMsg extends Message {

	private String catalog;
	private String name;
	private Field key;
	private Field[] values;
	
	public SketchPatternMsg(String catalog, String name, Field key, Field... values) {
		super();
		this.catalog = catalog;
		this.name = name;
		this.key = key;
		this.values = values;
	}

	public String getCatalog() {
		return catalog;
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
