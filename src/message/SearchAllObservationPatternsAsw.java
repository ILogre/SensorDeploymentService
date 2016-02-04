package message;

import java.util.List;

import transfer.Answer;

public class SearchAllObservationPatternsAsw extends Answer {
	
	private List<String> patterns;

	public SearchAllObservationPatternsAsw(List<String> patterns) {
		super();
		this.patterns = patterns;
	}

	public List<String> getPatterns() {
		return patterns;
	}

	@Override
	public String toString() {
		return "SearchAllObservationPatternsAsw [patterns=" + patterns + "]";
	}
	
	
}
