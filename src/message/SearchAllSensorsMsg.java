package message;

import transfer.Message;

public class SearchAllSensorsMsg extends Message {

	private String catalogName;
	private String container;

	public String getContainer() {
		return container;
	}

	public SearchAllSensorsMsg(String catalogName, String container) {
		super();
		this.catalogName = catalogName;
		this.container = container;
	}

	public String getCatalogName() {
		return catalogName;
	}


}
