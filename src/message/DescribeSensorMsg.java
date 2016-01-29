package message;

import transfer.Message;

public class DescribeSensorMsg extends Message {

	private String catalogName;
	private String sensorName;

	public DescribeSensorMsg(String catalogName, String sensorName) {
		super();
		this.catalogName = catalogName;
		this.sensorName = sensorName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public String getSensorName() {
		return sensorName;
	}

}
