package huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author yue
 *
 */
public class HuffmanCodeDemo {

	public static void main(String[] args) {
		String testString = new String();
		double outChar = 0, compSize = 0;
		String result = "";
		String compressFileTarget = "wordlist.txt";
		String decompressFileTarget = "wordlist.arc";
		try {
			BufferedReader ba = new BufferedReader(new FileReader(
					compressFileTarget));
			System.out.println("Compression commence");
			String tempLineFeed = ba.readLine();
			while (tempLineFeed != null) {
				testString = testString + tempLineFeed;
				tempLineFeed = ba.readLine();
			}
			System.out.println("original file read");
			ba.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		HuffmanCode.compressFile(compressFileTarget);

		HuffmanCode.decompressFile(decompressFileTarget);

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					compressFileTarget));
			String tempLineFeed = br.readLine();
			while (tempLineFeed != null) {
				result += tempLineFeed;
				tempLineFeed = br.readLine();
			}
			br.close();
			br = new BufferedReader(new FileReader(decompressFileTarget));
			outChar = br.read();
			while (outChar != -1) {
				outChar = br.read();
				compSize++;
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		if (testString.compareTo(result) != 0) {
			System.out.println("Decompressed File "
					+ "and Original File do not match.");
		}
		double totalComp = (100 * (compSize / testString.length()));
		System.out.println("Compression Ratio: " + (int) totalComp + "% ("
				+ (int) compSize + "/" + testString.length() + ")");
		if (testString.length() < 100) {
			System.out.println("Original Text: [" + testString + "]");
			System.out.println("Output Text  : [" + result + "]");
		}

	}
}