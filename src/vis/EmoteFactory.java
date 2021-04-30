package vis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import utils.Constants;

public class EmoteFactory {

	public static Map<String, String> getEmoteMap() {
		
		Map<String, String> map = new HashMap<String, String>();
		File file = new File(Constants.EMOTES_FILENAME);
		Scanner sc;
		try {
			sc = new Scanner(file);
			String[] row;
			while (sc.hasNext()) {
				row = sc.nextLine().split(", ");
				map.put(row[0], row[2] + "." + row[1]);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
