package message;

import java.util.List;

import transfer.Answer;

public class SearchAllSensorsAsw extends Answer {
	
	private List<String> sensorsNames;

	public SearchAllSensorsAsw(List<String> sensorsNames) {
		super();
		this.sensorsNames = sensorsNames;
	}

	public List<String> getSensorsNames() {
		return sensorsNames;
	}

	@Override
	public String toString() {
		return "SearchAllSensorsAsw [sensorsNames=" + sensorsNames + "]";
	}
	
}
