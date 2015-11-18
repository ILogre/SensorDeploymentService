package message;

import transfer.Message;

public class DeclareCatalogMsg extends Message {
	private String catalog;

	public DeclareCatalogMsg(String catalog) {
		super();
		this.catalog = catalog;
	}

	public String getCatalog() {
		return catalog;
	}
	
	
}
