package message;

import transfer.Message;

public class SearchSensorsByObservationPatternMsg extends Message {
	
	private String catalog;
	private String pattern;
	public SearchSensorsByObservationPatternMsg(String catalog, String pattern) {
		super();
		this.catalog = catalog;
		this.pattern = pattern;
	}
	public String getCatalog() {
		return catalog;
	}
	public String getPattern() {
		return pattern;
	}

}
