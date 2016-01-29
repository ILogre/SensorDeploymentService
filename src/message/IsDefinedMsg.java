package message;

import transfer.Message;

public class IsDefinedMsg extends Message {
	
	private String catalog;
	private String sensor;
	
	public IsDefinedMsg(String catalog, String sensor) {
		super();
		this.catalog = catalog;
		this.sensor = sensor;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSensor() {
		return sensor;
	}
}
