
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The KNeighborhood program implements two functions for
 * getting neighborhood and combining neighborhood scores
 * 
 * @author Dhvani
 * 
 */
public class KNeighborhood {
	final static String REGEX_APOSTROPHE = "'";
	final static String REGEX_SPACE = " ";
	final static String REGEX_SPACES = "[\\s+]";
	final static String REGEX_TWO_OR_MORE_SPACES = "\\s{2,}";
	final static String REGEX_NO_ALPHA_NO_SPACE = "[^a-z\\s]";
	
	/**
	 * Returns a map with every word in the input text and it's mean 
	 * neighborhood score
	 * @param s         	   the input text file     
	 * @param noOfNeighborhood the number of neighbors to consider for each word
	 * @param charScoreMap     the map with each character's score
	 * @return                 a map with word and it's mean neighborhood score
	 */
	static Map<String, List<Double>> calculateKNeighborhood(String s, 
		int noOfNeighborhood, Map<Character, Integer> charScoreMap) {
		// map to store the score of each word
		Map<String, Integer> wordScoreMap = new HashMap<>();
		// map to store the neighborhoods for each word
		Map<String, List<String>> neighborhoodMap = new HashMap<>();
		int wordScore;
		List<String> neighborhoodList;
		// split words based on space(s)
		String[] words = s.split(REGEX_SPACES);
		for (int i = 0; i < words.length; i++) {
			if (wordScoreMap.get(words[i]) == null) {
				// calculate the score of each word and put it in wordScoreMap
				wordScore = 0;
				for (char c : words[i].toCharArray()) {
					wordScore = wordScore + charScoreMap.get(c);
				}
				wordScoreMap.put(words[i], wordScore);
			}
			
			// get neighborhood of each word and put it in neighborhoodMap.
			// neighborhood list stores the neighbors for each word.
			// If the word does not exist in the neighborhoodMap, create a list, 
			// and add the neighborhood words to the list. Put it in map.
			// If the word exists in the map, get the list, add these 
			// neighborhood words in the list and put it in map
			if (neighborhoodMap.get(words[i]) != null) {
				neighborhoodList = neighborhoodMap.get(words[i]);
			} else {
				neighborhoodList = new ArrayList<>();
			}
			
			for (int j = 1; j <= noOfNeighborhood; j++) {
				if (i - j >= 0) {
					neighborhoodList.add(words[i-j]);
				}
				if (i + j < words.length) {
					neighborhoodList.add(words[i+j]);
				}
			}
			neighborhoodMap.put(words[i], neighborhoodList);
		}

		Map<String, List<Double>> neighborhoodScoreMap = new TreeMap<>();
		double totalScore;
		double meanNeighborhoodScore;
		List<String> wordsList;
		List<Double> scoreList;
		// calculate the mean neighborhood score for each word
		for (Map.Entry<String, List<String>> entry : neighborhoodMap.entrySet()) {
			wordsList = entry.getValue();
			totalScore = 0;
			for (String string : wordsList) {
				totalScore = totalScore + wordScoreMap.get(string);
			}
			meanNeighborhoodScore = totalScore/wordsList.size();
			scoreList = new ArrayList<>();
			scoreList.add(meanNeighborhoodScore);
			neighborhoodScoreMap.put(entry.getKey(), scoreList);
		}
		return neighborhoodScoreMap;
	}
	
	/**
	 * Adds/updates the map containing word and it's neighborhood score
	 * @param neighborhoodScoreForFile the map which has the words and it's corresponding score 
	 *                                 for a text file
	 * @param finalScoreMap            the map containing all the words and it's corresponding 
	 *                                 score from all files
	 */
	static void combineNeighborhoodScore(Map<String, List<Double>> neighborhoodScoreForFile, 
		Map<String, List<Double>> finalScoreMap) {
		String key;
    	List<Double> value;
    	List<Double> scoreList;
		for(Map.Entry<String, List<Double>> entry : neighborhoodScoreForFile.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			scoreList = new ArrayList<>();
			if (finalScoreMap.containsKey(key)) {
				scoreList = finalScoreMap.get(key);
			}
			scoreList.add(value.get(0));
			finalScoreMap.put(key, scoreList);
		}
	}
}
