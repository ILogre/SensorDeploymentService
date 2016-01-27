package message;

import java.util.LinkedHashMap;

import businessobject.ContainerType;
import transfer.Message;

public class BuildSensorHostingHierarchyMsg extends Message {
	private String catalog;
	private LinkedHashMap<String, ContainerType> container;

	public BuildSensorHostingHierarchyMsg(String catalog, LinkedHashMap<String, ContainerType> container) {
		super();
		this.catalog = catalog;
		this.container = container;
	}

	public String getCatalogName() {
		return catalog;
	}

	public LinkedHashMap<String, ContainerType> getContainer() {
		return container;
	}
	
	
	
}
