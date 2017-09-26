import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class is for generating output for benchmarking
 * results for the k neighborhood score program
 * @author Dhvani
 *
 */
public class Benchmark {
	public static void main(String[] args) {
		final String COMMA_DELIMITER = ",";
		final String NEW_LINE_SEPARATOR = "\n";
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the number of neighborhood to process");  
		int neighborhood = sc.nextInt();
		System.out.println("Enter the number of iterations");  
		int iterations = sc.nextInt();
		sc.close();
		FileWriter writer = null;
		try {
			// flush the previous file if any
			// write headers
			writer = new FileWriter("./sequentialTime.csv", false);
			writer.append("Iteration");
			writer.append(COMMA_DELIMITER);
			writer.append("Time");
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
		try {
			// flush the previous file if any
			// write headers
			writer = new FileWriter("./parallelTime.csv", false);
			writer.append("ThreadNumber");
			writer.append(COMMA_DELIMITER);
			writer.append("Time");
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

		// run sequential program for calculating neighborhood
		NeighborhoodScoreSequential sequentialObj;
		for (int i = 1; i <= iterations; i++) {
			sequentialObj = new NeighborhoodScoreSequential();
			sequentialObj.sequential(neighborhood, i);
		}
		// run parallel version for calculating neighborhood
		NeighborhoodScoreParallel parallelObj;
		for (int i = 2; i <= 16; i++) {
			for (int j = 1; j <= iterations; j++) {
				parallelObj = new NeighborhoodScoreParallel(neighborhood);
				parallelObj.parallel(neighborhood, i);
			}
		}
	}
}
