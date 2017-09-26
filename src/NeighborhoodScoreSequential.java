
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


/**
 * This class runs the neighborhood scoring program sequentially
 * i.e. one text file at a time
 * 
 * @author Dhvani
 *
 */
public class NeighborhoodScoreSequential {
	
	final static String COMMA_DELIMITER = ",";
	final static String NEW_LINE_SEPARATOR = "\n";
	static List<Map<String, List<Double>>> neighborhoodScoresForEachFile;
	static Map<String, Integer> wordScoreMap;
	static Map<Character, Integer> finalCharCountMap;
	
	NeighborhoodScoreSequential() {
		neighborhoodScoresForEachFile = new ArrayList<>();
		wordScoreMap = new HashMap<>();
		finalCharCountMap = new HashMap<>();
	}
	
	void sequential(int neighborhood, int iteration) {
		final long startTime = System.currentTimeMillis();
        File dirName = new File("./input/");
        File[] listOfFiles = dirName.listFiles();
		String[] parsedInput = new String[listOfFiles.length];
		float totalCharactersCorpus = 0;
		
		// call the function to compute the character count and score. It also updates
		// parsedInput array with the text of each file without any characters apart from
		// a to z and space
		Map<Character, Integer> charScoreMap = CharCountAndScore.calculateCharCount(listOfFiles, parsedInput, totalCharactersCorpus);
        for (int j = 0; j < parsedInput.length; j++) {
        	String input = parsedInput[j];
        	// get the neighborhood score of each word for a file
            neighborhoodScoresForEachFile.add(KNeighborhood.calculateKNeighborhood(
        		input, neighborhood, charScoreMap));
        }
        
        // combine the score of every word from all files
        Map<String, List<Double>> finalScoreMap = new TreeMap<>(neighborhoodScoresForEachFile.get(0));
        for (int j = 1; j < neighborhoodScoresForEachFile.size(); j++) {
			 KNeighborhood.combineNeighborhoodScore(neighborhoodScoresForEachFile.get(j), finalScoreMap);
		}
        // write the output to csv file
        CSVWriter.writeOutputToCSVFile("./sequential.csv", finalScoreMap);
        final long endTime = System.currentTimeMillis();
        final double totalTime = (endTime - startTime)/1000.00;
	    System.out.println("Total execution time: " + totalTime);
	    CSVWriter.writeTimeToCSVFile("./sequentialTime.csv", Double.toString(totalTime), iteration);
	}
	
	public static void main(String args[]) {
		if (args.length >= 1) {
			NeighborhoodScoreSequential sequentialObj = new NeighborhoodScoreSequential();
			sequentialObj.sequential(Integer.parseInt(args[0]), 1);
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the number of neighborhood to process");  
			int neighborhood = sc.nextInt();
			sc.close();
			NeighborhoodScoreSequential sequentialObj = new NeighborhoodScoreSequential();
			sequentialObj.sequential(neighborhood, 1);
		}
	}
}
