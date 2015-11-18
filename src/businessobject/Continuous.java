package businessobject;

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
	
}