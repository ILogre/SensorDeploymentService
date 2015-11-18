package message;

import transfer.Message;

public class RecordPeriodicSensorMsg extends Message {

	private String catalog;
	private String container;
	private String name;
	private String observationPattern;
	private int period;
	
	public RecordPeriodicSensorMsg(String catalog, String container,
			String name, String observationPattern, int period) {
		super();
		this.catalog = catalog;
		this.container = container;
		this.name = name;
		this.observationPattern = observationPattern;
		this.period = period;
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

	public int getPeriod() {
		return period;
	}

}
