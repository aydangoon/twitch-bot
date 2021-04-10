//
//public class Word {
//
//	// the literal word
//	private String value;
//	
//	// The number of times this word does not end a sentence.
//	private int wordsProceeded;
//	
//	public Word(String value) {
//		this.value = value;
//		this.wordsProceeded = 0;
//	}
//	
//	public String getValue() {
//		return value;
//	}
//	
//	public void incrementWordsProceeded() {
//		this.wordsProceeded++;
//	}
//	
//	public int getWordsProceeded() {
//		return this.wordsProceeded;
//	}
//	
//	@Override
//	public boolean equals(Object o) {
//		if (o == this) return true;
//		
//		if (!(o instanceof Word)) return false;
//		System.out.println("here??");
//		return ((Word) o).getValue().equals(this.value);
//	}
//	
//	public String toString() {
//		return this.value;
//	}
//
//}
