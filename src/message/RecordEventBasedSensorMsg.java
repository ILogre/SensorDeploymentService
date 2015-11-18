package message;

import transfer.Message;

public class RecordEventBasedSensorMsg extends Message {

	private String catalog;
	private String container;
	private String name;
	private String observationPattern;
	private String trigger;
	
	public RecordEventBasedSensorMsg(String catalog, String container,
			String name, String observationPattern, String trigger) {
		super();
		this.catalog = catalog;
		this.container = container;
		this.name = name;
		this.observationPattern = observationPattern;
		this.trigger = trigger;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getContainer() {
		return container;
	}

	public String getName() {
		return name;
	}

	public String getObservationPattern() {
		return observationPattern;
	}

	public String getTrigger() {
		return trigger;
	}

}
