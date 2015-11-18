package message;

import transfer.Message;

public class IsDefinedMsg extends Message {
	
	private String catalog;
	private String data;
	
	public IsDefinedMsg(String catalog, String data) {
		super();
		this.catalog = catalog;
		this.data = data;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getData() {
		return data;
	}
}
