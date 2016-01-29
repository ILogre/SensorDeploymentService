package businessobject;

import sensorDeploymentLanguage.Atomic;




public abstract class Field {
	
	public Field(String name) {
		super();
		this.name = name;
	}

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
	

	// Optional parameters //
	
	private String observationPatternSource;
	private String fieldSource;
	private String function;

	public String getObservationPatternSource() {
		return observationPatternSource;
	}

	public String getFieldSource() {
		return fieldSource;
	}

	public String getFunction() {
		return function;
	}

	public Field(String name, String observationPatternSource,
			String fieldSource, String function) {
		super();
		this.name = name;
		this.observationPatternSource = observationPatternSource;
		this.fieldSource = fieldSource;
		this.function = function;
	}
	
	public abstract void ConcretizeToEMF(Atomic key);
	

}
