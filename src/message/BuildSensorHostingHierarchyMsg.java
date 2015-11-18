package message;

import transfer.Message;

public class BuildSensorHostingHierarchyMsg extends Message {
	private String catalog;
	private String[] container;

	public BuildSensorHostingHierarchyMsg(String catalog, String ... container) {
		super();
		this.catalog = catalog;
		this.container = container;
	}

	public String getCatalogName() {
		return catalog;
	}

	public String[] getContainer() {
		return container;
	}
	
	
	
}
