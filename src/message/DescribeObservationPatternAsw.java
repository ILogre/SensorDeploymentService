package message;

import java.util.Map;

import transfer.Answer;

public class DescribeObservationPatternAsw extends Answer {
	private String keyName;
	private boolean continuous;
	private Map<String,Boolean> values;
	public DescribeObservationPatternAsw(String keyName, boolean continuous,
			Map<String, Boolean> values) {
		super();
		this.keyName = keyName;
		this.continuous = continuous;
		this.values = values;
	}
	public String getKeyName() {
		return keyName;
	}
	public boolean isContinuous() {
		return continuous;
	}
	public Map<String, Boolean> getValues() {
		return values;
	}
	@Override
	public String toString() {
		return "DescribeObservationPatternAsw [keyName=" + keyName
				+ ", continuous=" + continuous + ", values=" + values + "]";
	}
	
}
