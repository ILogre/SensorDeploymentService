package message;

import transfer.Message;

public class DescribeObservationPatternMsg extends Message {

	private String catalogName;
	private String observationName;
	public DescribeObservationPatternMsg(String catalogName,
			String observationName) {
		super();
		this.catalogName = catalogName;
		this.observationName = observationName;
	}
	public String getCatalogName() {
		return catalogName;
	}
	public String getObservationName() {
		return observationName;
	}
}
