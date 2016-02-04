package message;

import transfer.Message;

public class SearchAllObservationPatternsMsg extends Message {
	
	private String catalog;

	public SearchAllObservationPatternsMsg(String catalog) {
		super();
		this.catalog = catalog;
	}

	public String getCatalog() {
		return catalog;
	}

}
