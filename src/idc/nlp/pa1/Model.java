package idc.nlp.pa1;

public enum Model {
	BASELINE(1, "Baseline"), 
	BIGRAM(2,"Bi-Gram"),
	;

	private int number;
	private String nick;

	private Model(int number, String nick) {
		this.number = number;
		this.nick = nick;
	}

	public static Model fromNumber(int number) {
		for (Model m : Model.values()) {
			if (m.number == number)
				return m;
		}
		return null;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return nick;
	}

}
