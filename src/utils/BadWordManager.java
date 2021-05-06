package utils;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.File;
import java.io.FileNotFoundException;

public class BadWordManager {
    static Set<String> badWords = new HashSet<String>();
    static boolean hasDone = false;
    
//	private static final String[] badWords = {
//		"fuck", "shit", "retard", "ass", "bitch", "cock", "cum", "fag", "dick", "gay"
//	};
	
	public static boolean shouldCensor(String word) {
	    if (!hasDone) {
	        try {
	            File obj = new File("./data/badwords.txt");
	            Scanner scan = new Scanner(obj);
	            while (scan.hasNextLine()) {
	                badWords.add(scan.nextLine());
	            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        hasDone = true;
	    }
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
