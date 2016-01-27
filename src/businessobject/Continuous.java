package businessobject;

import sensorDeploymentLanguage.Atomic;
import sensorDeploymentLanguage.DataType;
import sensorDeploymentLanguage.SensorDeploymentLanguageFactory;

public class Continuous<T>extends Field {
	
	public Continuous(String name, T min, T max) {
		super(name);
		this.min = min;
		this.max = max;
	}
	
	public Continuous(String name, String observationPatternSource,
	String fieldSource, String function, T min, T max) {
		super(name, observationPatternSource, fieldSource, function);
		this.min = min;
		this.max = max;
	}
	
	private T min;
	private T max;
	
	public T getMin() {
		return min;
	}
	public T getMax() {
		return max;
	}

	@Override
	public void ConcretizeToEMF(Atomic key) {
		sensorDeploymentLanguage.Continuous f = SensorDeploymentLanguageFactory.eINSTANCE.createContinuous();
		DataType dtmin = null;
		switch(min.getClass().getSimpleName()){
		case "Integer":
			dtmin = SensorDeploymentLanguageFactory.eINSTANCE.createInteger();
			break;
		case "Float":
			dtmin = SensorDeploymentLanguageFactory.eINSTANCE.createFloat();
			break;
		default :
			dtmin = SensorDeploymentLanguageFactory.eINSTANCE.createString();
		}
		f.setMin(dtmin);
		
		DataType dtmax = null;
		switch(max.getClass().getSimpleName()){
		case "Integer":
			dtmax = SensorDeploymentLanguageFactory.eINSTANCE.createInteger();
			break;
		case "Float":
			dtmax = SensorDeploymentLanguageFactory.eINSTANCE.createFloat();
			break;
		default :
			dtmax = SensorDeploymentLanguageFactory.eINSTANCE.createString();
		}
		
		f.setMax(dtmax);
		key.setRange(f);
	}
	
}