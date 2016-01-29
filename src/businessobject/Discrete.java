package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sensorDeploymentLanguage.Atomic;
import sensorDeploymentLanguage.DataType;
import sensorDeploymentLanguage.SensorDeploymentLanguageFactory;

public class Discrete extends Field {

	public Discrete(String name, String... values) {
		super(name);
		this.values= new ArrayList<String>(Arrays.asList(values));
	}
	
	public Discrete(String name, String observationPatternSource,
	String fieldSource, String function, String... values) {
		super(name, observationPatternSource, fieldSource, function);
		this.values= new ArrayList<String>(Arrays.asList(values));
	}

	private List<String> values;

	public List<String> getValues() {
		return values;
	}

	@Override
	public void ConcretizeToEMF(Atomic key) {
		sensorDeploymentLanguage.Discrete f = SensorDeploymentLanguageFactory.eINSTANCE.createDiscrete();
		for(String s : this.getValues()){
			DataType dt = null;
			switch(s){
			case "Integer":
				dt = SensorDeploymentLanguageFactory.eINSTANCE.createInteger();
				break;
			case "Float":
				dt = SensorDeploymentLanguageFactory.eINSTANCE.createFloat();
				break;
			default :
				dt = SensorDeploymentLanguageFactory.eINSTANCE.createString();
			}
			f.getValues().add(dt);
			key.setRange(f);
		}
	}

	@Override
	public String toString() {
		return "Discrete [name=" + name + ",  values=" + values + "]";
	}
	
	
}
