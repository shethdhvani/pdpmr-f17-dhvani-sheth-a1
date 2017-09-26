
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class runs the neighborhood scoring program parallelly
 * i.e. multiple input text files are processed at a time.
 * 
 * @author Dhvani
 *
 */
public class NeighborhoodScoreParallel {
	
	static int noOfNeighborhood;
	final static String REGEX_APOSTROPHE = "'";
	final static String REGEX_SPACE = " ";
	final static String REGEX_SPACES = "[\\s+]";
	final static String REGEX_TWO_OR_MORE_SPACES = "\\s{2,}";
	final static String REGEX_NO_ALPHA_NO_SPACE = "[^a-z\\s]";

	static List<String> inputList;
	static Map<Character, Integer> finalCharCountMap;
	static float totalCount;
	static Map<Character, Integer> charScoreMap;
	
	NeighborhoodScoreParallel(int neighborhood) {
		noOfNeighborhood = neighborhood;
		inputList = new ArrayList<>();
		finalCharCountMap = new TreeMap<>();
		totalCount = 0;
		charScoreMap = new TreeMap<>();
	}

	void parallel(int neighborhood, int noOfThreads) {
		final long startTime = System.currentTimeMillis();
        File dirName = new File("./input/");
        File[] listOfFiles = dirName.listFiles();
        ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
        List<AppendFiles> appendTasks = new ArrayList<AppendFiles>();
		File fileName;
		
		// process each file parallelly to get the text and add each file's
		// text to the inputList
		AppendFiles appendWorker;
		for (int i = 0; i < listOfFiles.length; i++) {
        	fileName = listOfFiles[i];
        	appendWorker = new AppendFiles(fileName);
            executor.execute(appendWorker);
            appendTasks.add(appendWorker);
        }
        executor.shutdown();
        try {
        	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        
        // process each file and get character count
        List<CharCount> countTasks = new ArrayList<CharCount>();
        CharCount countWorker;
        String input;
        executor = Executors.newFixedThreadPool(noOfThreads);
        for (int i = 0; i < inputList.size(); i++) {
        	input = inputList.get(i);
        	countWorker = new CharCount(input);
            executor.execute(countWorker);
            countTasks.add(countWorker);
        }
        executor.shutdown();
        try {
        	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        
        // calculate the total characters in the corpus. calculate the count
        // of each character
        finalCharCountMap = new TreeMap<>(countTasks.get(0).charCountMap);
        totalCount = totalCount + countTasks.get(0).count;
        Map<Character, Integer> tempMap;
        Character charKey;
        int charCountVal;
        for (int i = 1; i < countTasks.size(); i++) {
        	totalCount = totalCount + countTasks.get(i).count;
        	tempMap = countTasks.get(i).charCountMap;
        	for (Map.Entry<Character, Integer> entry : tempMap.entrySet()) {
        		charKey = entry.getKey();
        		charCountVal = entry.getValue();
        		if (finalCharCountMap.containsKey(charKey)) {
        			finalCharCountMap.put(charKey, finalCharCountMap.get(charKey) + charCountVal);
        		} else {
        			finalCharCountMap.put(charKey, charCountVal);
        		}
        	}
		}
        
        // calculate the score of each letter
        charScoreMap = CharCountAndScore.calculateCharScore(finalCharCountMap, totalCount);
	    
        // get the neighborhood of each word for a file parallelly
 		executor = Executors.newFixedThreadPool(noOfThreads);
		List<NeighborHoodScore> neighborhoodTasks = new ArrayList<NeighborHoodScore>();
		NeighborHoodScore neighborhoodWorker;
		for (int i = 0; i < inputList.size(); i++) {
        	input = inputList.get(i);
        	neighborhoodWorker = new NeighborHoodScore(input);
            executor.execute(neighborhoodWorker);
            neighborhoodTasks.add(neighborhoodWorker);
        }
        executor.shutdown();
        try {
        	executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
        
        // combine the neighborhood score for each word from all files
        Map<String, List<Double>> finalScoreMap = new TreeMap<>(neighborhoodTasks.get(0).neighborhoodScoreMap);
        for (int i = 1; i < neighborhoodTasks.size(); i++) {
        	KNeighborhood.combineNeighborhoodScore(neighborhoodTasks.get(i).neighborhoodScoreMap, finalScoreMap);
		}
        CSVWriter.writeOutputToCSVFile("./parallel.csv", finalScoreMap);
        final long endTime = System.currentTimeMillis();
        final Double totalTime = (endTime - startTime)/1000.00;
	    System.out.println("Total execution time: " + totalTime);
	    CSVWriter.writeTimeToCSVFile("./parallelTime.csv", Double.toString(totalTime), noOfThreads);
	}
	
	static class NeighborHoodScore implements Runnable {
		Map<String, List<Double>> neighborhoodScoreMap = new TreeMap<>();
		String input;
	    
		NeighborHoodScore(String input) {
	    	this.input = input;
	    }

	    @Override
	    public void run() {
	    	neighborhoodScoreMap = KNeighborhood.calculateKNeighborhood(input,
    			noOfNeighborhood, charScoreMap);
	    }
	}
	
	static class AppendFiles implements Runnable {
		File fileName;
		String s;
	    
		AppendFiles(File fileName) {
	    	this.fileName = fileName;
	    }

	    @Override
	    public void run() {
	    	s = CharCountAndScore.cleanInput(fileName);
	    	synchronized(inputList) {
	    		inputList.add(s);
	    	}
	    }
	}
	
	static class CharCount implements Runnable {
		String s;
		Map<Character, Integer> charCountMap;
		int count;
	    
		CharCount(String input) {
	    	this.s = input;
	    	count = 0;
	    	charCountMap = new TreeMap<>();
	    }

	    @Override
	    public void run() {
	    	// replace all spaces to get the total number of characters
    		s = s.replaceAll(REGEX_SPACE, "");
    		count = s.length();
    		charCountMap = CharCountAndScore.calculateCharCount(s, charCountMap);
	    }
	}
	
	public static void main(String args[]) {
		if (args.length >= 1) {
			NeighborhoodScoreParallel parallelObj = new NeighborhoodScoreParallel(Integer.parseInt(args[0]));
			parallelObj.parallel(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter the number of neighborhood to process");  
			int neighborhood = sc.nextInt();
			System.out.println("Enter the number of threads");  
			int noOfThreads = sc.nextInt();
			sc.close();
			NeighborhoodScoreParallel parallelObj = new NeighborhoodScoreParallel(neighborhood);
			parallelObj.parallel(neighborhood, noOfThreads);
		}
	}
}
