
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Writes the data to a csv file
 * 
 * @author Dhvani
 *
 */
public class CSVWriter {

	final static String COMMA_DELIMITER = ",";
	final static String NEW_LINE_SEPARATOR = "\n";
	
	/**
	 * If there are multiple scores for a word, it calculates the mean,
	 * and writes the word and it's corresponding neighborhood score 
	 * to a csv file
	 * @param csvFilePath   the path where the csv file is to be stored
	 * @param finalScoreMap input map which contains words and it's scores
	 *                      from all the input files
	 */
	static void writeOutputToCSVFile(String csvFilePath, Map<String, List<Double>> finalScoreMap) {
		FileWriter writer = null;
		String key;
        List<Double> value;
		try {
			writer = new FileWriter(csvFilePath);
			Double finalValue;
	        for(Map.Entry<String, List<Double>> entry : finalScoreMap.entrySet()) {
				key = entry.getKey();
				value = entry.getValue();
				finalValue = 0.00;
				if (value.size() > 1) {
					for (Double d : value) {
						finalValue = finalValue + d;
					}
					finalValue = finalValue/value.size();
				} else {
					finalValue = value.get(0);
				}
				writer.append(key);
				writer.append(COMMA_DELIMITER);
				writer.append(String.valueOf(finalValue));
				writer.append(NEW_LINE_SEPARATOR);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
	        	writer.flush();
	        	writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	/**
	 * Writes the execution time to csv file
	 * @param csvFilePath the path where the csv file is to be stored
	 * @param totalTime   total execution time
	 * @param inputInt    no of threads for parallel execution and iteration
	 *                    number for sequential
	 */
	static void writeTimeToCSVFile(String csvFilePath, String totalTime, int inputInt) {
	    FileWriter writer = null;
		try {
			writer = new FileWriter(csvFilePath, true);
			writer.append(String.valueOf(inputInt));
			writer.append(COMMA_DELIMITER);
			writer.append(totalTime);
			writer.append(NEW_LINE_SEPARATOR);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
	        try {
	        	writer.flush();
	        	writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
}
