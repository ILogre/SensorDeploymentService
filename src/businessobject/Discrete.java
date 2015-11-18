package businessobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

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
}
