package message;

import transfer.Answer;

public class IsDefinedAsw extends Answer {
	
	private boolean defined;

	public IsDefinedAsw(boolean defined) {
		super();
		this.defined = defined;
	}

	public boolean isDefined() {
		return defined;
	}
}
