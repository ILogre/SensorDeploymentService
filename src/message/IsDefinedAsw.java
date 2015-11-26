package message;

import transfer.Answer;

public class IsDefinedAsw extends Answer {
	
	private boolean defined;
	private String catalog;
	private String data;

	public IsDefinedAsw(String catalog, String data, boolean defined) {
		super();
		this.defined = defined;
		this.catalog = catalog;
		this.data = data;
	}

	public boolean isDefined() {
		return defined;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getData() {
		return data;
	}
	
	
}
