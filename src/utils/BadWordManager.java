package utils;

public class BadWordManager {
	private static final String[] badWords = {
		"fuck", "shit", "retard", "ass", "bitch", "cock", "cum", "fag", "dick", "gay"
	};
	
	public static boolean shouldCensor(String word) {
		for (String badWord : badWords) {
			if (word.toLowerCase().contains(badWord)) {
				return true;
			}
		}
		return false;
	}
	
	public static String censor(String word) {
		StringBuilder bleep = new StringBuilder();
		for (int i = 1; i < word.length() - 1; i++) {
			bleep.append("*");
		}
		return word.charAt(0) + bleep.toString() + word.charAt(word.length() - 1);
	}
}
