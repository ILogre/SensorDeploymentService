package message;

import java.util.List;

import businessobject.Field;
import transfer.Answer;

public class DescribeSensorAsw extends Answer {
	
	private String sensorName;
	private String observationPattern;
	private boolean isPeriodic;
	private boolean isEventBased;
	private int period;
	private String trigger;
	private List<businessobject.Field> fields;
	
	public String getSensorName() {
		return sensorName;
	}
	public String getObservationPattern() {
		return observationPattern;
	}
	public boolean isPeriodic() {
		return isPeriodic;
	}
	public boolean isEventBased() {
		return isEventBased;
	}
	public int getPeriod() {
		return period;
	}
	public String getTrigger() {
		return trigger;
	}
	public List<businessobject.Field> getFields() {
		return fields;
	}
	public DescribeSensorAsw(String sensorName, String observationPattern,
			boolean isPeriodic, boolean isEventBased, int period,
			 List<Field> fields) {
		super();
		this.sensorName = sensorName;
		this.observationPattern = observationPattern;
		this.isPeriodic = isPeriodic;
		this.isEventBased = isEventBased;
		this.period = period;
		this.fields = fields;
	}
	public DescribeSensorAsw(String sensorName, String observationPattern,
			boolean isPeriodic, boolean isEventBased, String trigger,
			List<Field> fields) {
		super();
		this.sensorName = sensorName;
		this.observationPattern = observationPattern;
		this.isPeriodic = isPeriodic;
		this.isEventBased = isEventBased;
		this.trigger = trigger;
		this.fields = fields;
	}
	@Override
	public String toString() {
		return "DescribeSensorAsw [sensorName=" + sensorName
				+ ", observationPattern=" + observationPattern
				+ ", isPeriodic=" + isPeriodic + ", isEventBased="
				+ isEventBased + ", period=" + period + ", trigger=" + trigger
				+ ", fields=" + fields + "]";
	}

}
