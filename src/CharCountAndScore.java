import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Calculate character count and char score. Also clean the input file i.e. 
 * remove all characters except a to z and space
 * @author Dhvani
 *
 */
public class CharCountAndScore {
	final static String REGEX_APOSTROPHE = "'";
	final static String REGEX_SPACE = " ";
	final static String REGEX_SPACES = "[\\s+]";
	final static String REGEX_TWO_OR_MORE_SPACES = "\\s{2,}";
	final static String REGEX_NO_ALPHA_NO_SPACE = "[^a-z\\s]";
	
	/**
	 * 
	 * @param listOfFiles           all the input files
	 * @param parsedInput           after the file is cleaned, add it to this
	 * @param totalCharactersCorpus count of total number of characters in the corpus
	 * @return                      map containing each character and it's count
	 */
	static Map<Character, Integer> calculateCharCount(File[] listOfFiles, String[] parsedInput, float totalCharactersCorpus) {
		String s;
		String inputCharacters;
		int totalCharactersFile = 0;
		// map to store the count of each character
		Map<Character, Integer> charCountMap = new HashMap<>();
		// map to store the score of each character
		Map<Character, Integer> charScoreMap = new HashMap<>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			s = cleanInput(listOfFiles[i]);
    		parsedInput[i] = s;

    		// replace all spaces to get the total number of characters
    		inputCharacters = s.replaceAll(REGEX_SPACE, "");
    		totalCharactersFile = 0;
    		totalCharactersFile = inputCharacters.length();
    		totalCharactersCorpus = totalCharactersCorpus + totalCharactersFile;
    		
    		// count the number of occurrences of each letter
    		calculateCharCount(inputCharacters, charCountMap);
		}
    	
		// calculate the score of each letter
		charScoreMap = calculateCharScore(charCountMap, totalCharactersCorpus);
		return charScoreMap;
	}
	
	/**
	 * 
	 * @param s            input text for a file
	 * @param charCountMap map containing each character and it's count
	 * @return             map containing each character and it's count
	 */
	static Map<Character, Integer> calculateCharCount(String s, Map<Character, Integer> charCountMap) {
		// count the number of occurrences of each letter
		for (char c : s.toCharArray()) {
			if (charCountMap.get(c) != null) {
				charCountMap.put(c, charCountMap.get(c) + 1);
			} else {
				charCountMap.put(c, 1);
			}
		}
		return charCountMap;
	}
	
	/**
	 * 
	 * @param charCountMap          map containing each character and it's count
	 * @param totalCharactersCorpus count of total number of characters in the corpus
	 * @return                      map containing each character and it's score
	 */
	static Map<Character, Integer> calculateCharScore(Map<Character, Integer> charCountMap, float totalCharactersCorpus) {
		// calculate the score of each letter
		double charPerc;
		int charScore;
		Map<Character, Integer> charScoreMap = new HashMap<>();
		for (Map.Entry<Character, Integer> entry : charCountMap.entrySet()) {
			charPerc = entry.getValue()/totalCharactersCorpus;
			charScore = 0;
			if (charPerc > 0.1) {
				charScore = 0;
			} else if (0.08 <= charPerc && charPerc < 0.1) {
				charScore = 1;
			} else if (0.06 <= charPerc && charPerc < 0.08) {
				charScore = 2;
			} else if (0.04 <= charPerc && charPerc < 0.06) {
				charScore = 4;
			} else if (0.02 <= charPerc && charPerc < 0.04) {
				charScore = 8;
			} else if (0.01 <= charPerc && charPerc < 0.02) {
				charScore = 16;
			} else {
				charScore = 32;
			}
			charScoreMap.put(entry.getKey(), new Integer(charScore));
		}
		return charScoreMap;
	}
	
	
	/**
	 * 
	 * @param fileName input file
	 * @return         file in string format with no characters other
	 *                 than a to z and space
	 */
	static String cleanInput(File fileName) {
		String s = null;
		try {
			// read from input file
			s = new String(Files.readAllBytes(fileName.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// change to lower case
		s = s.toLowerCase();
		// replace everything in the input except letters a-z and space
		s = s.replaceAll(REGEX_APOSTROPHE, "");
		s = s.replaceAll(REGEX_NO_ALPHA_NO_SPACE, REGEX_SPACE);
		s = s.replaceAll(REGEX_TWO_OR_MORE_SPACES, REGEX_SPACE).trim();
		return s;
	}
}
