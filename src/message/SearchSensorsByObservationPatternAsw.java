package message;

import java.util.List;

import transfer.Answer;

public class SearchSensorsByObservationPatternAsw extends Answer {
	private List<String> sensors;

	public SearchSensorsByObservationPatternAsw(List<String> sensors) {
		super();
		this.sensors = sensors;
	}

	public List<String> getSensors() {
		return sensors;
	}
}
