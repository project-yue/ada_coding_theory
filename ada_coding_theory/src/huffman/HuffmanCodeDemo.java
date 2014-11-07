package huffman;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Demo class for showing the compression and decompression processes with
 * compression rate
 * 
 * @author yue
 *
 */
public class HuffmanCodeDemo {

	public static void main(String[] args) {
		simpleExample();
		// complexExample();
	}

	public static void simpleExample() {
		String testString = new String();
		double outChar = 0, compSize = 0;
		String result = "";
		String compressFileTarget = "simple_text";
		String decompressFileTarget = "simple_text.arc";
		try {
			BufferedReader ba = new BufferedReader(new FileReader(
					compressFileTarget));
			String tempLineFeed = ba.readLine();
			while (tempLineFeed != null) {
				testString = testString + tempLineFeed;
				tempLineFeed = ba.readLine();
			}
			System.out.println("The original file read");
			ba.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		HuffmanCode.compressFile(compressFileTarget, false);
		System.out.println("Orginal file read");

		HuffmanCode.decompressFile(decompressFileTarget, false);
		System.out.println("Compressed file read");

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
		double totalComp = (100 * (compSize / testString.length()));
		System.out.println("Compression Ratio: " + (int) totalComp + "% ("
				+ (int) compSize + "/" + testString.length() + ")");
		if (testString.length() < 100) {
			System.out.println("Original Text: [" + testString + "]");
			System.out.println("Output Text  : [" + result + "]");
		}
	}

	public static void complexExample() {
		String testString = new String();
		double outChar = 0, compSize = 0;
		String result = "";
		String compressFileTarget = "shell_scripting";
		String decompressFileTarget = "shell_scripting.arc";
		System.out.println("Reading the original file for later checks");
		try {
			BufferedReader ba = new BufferedReader(new FileReader(
					compressFileTarget));
			String tempLineFeed = ba.readLine();
			while (tempLineFeed != null) {
				testString = testString + tempLineFeed;
				tempLineFeed = ba.readLine();
			}
			System.out.println("The original file read");
			ba.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Compression commence");
		HuffmanCode.compressFile(compressFileTarget, true);
		System.out.println("Compression completed");

		System.out.println("Decompression commence");
		HuffmanCode.decompressFile(decompressFileTarget, true);
		System.out.println("Decompression completed");
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
		System.out.println("Test Correctness of "
				+ "decompressed and original files");
		if (testString.compareTo(result) != 0) {
			System.out.println("Decompressed file "
					+ "and Original file do not match.");
		} else {
			System.out.println("Decompressed file "
					+ "matches the original file");
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