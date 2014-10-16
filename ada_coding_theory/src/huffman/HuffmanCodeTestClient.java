package huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * The test can take long time due to the size of the compressed file. The test
 * will run the compression and decompression several times to obtain different
 * results
 * 
 * @author mk-19
 *
 */
public class HuffmanCodeTestClient {

	public static void main(String[] args) {
		String testString = new String();
		HuffmanTree t = new HuffmanTree();
		OutputStream out;
		double outChar = 0, compSize = 0;
		String outLine = new String();
		String output = new String();
		String toBeCompressedFile = "wordlist.txt";
		String toBeDecompressedFile = "wordlist.arc";
		try {
			BufferedReader ba = new BufferedReader(new FileReader(
					toBeCompressedFile));
			System.out.println("Compression commence");
			outLine = ba.readLine();
			while (outLine != null) {
				testString = testString + outLine;
				outLine = ba.readLine();
			}
			System.out.println("original file read");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		compressFile(toBeCompressedFile);
		decompressFile(toBeDecompressedFile);

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					toBeCompressedFile));

			outLine = br.readLine();
			while (outLine != null) {
				output = output + outLine;
				outLine = br.readLine();
			}
			br.close();
			br = new BufferedReader(new FileReader(toBeDecompressedFile));
			outChar = br.read();
			while (outChar != -1) {
				outChar = br.read();
				compSize++;
			}
			br.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		if (testString.compareTo(output) != 0) {
			System.out.println("Decompressed File "
					+ "and Original File do not match.");
		}
		double totalComp = (100 * (compSize / testString.length()));
		System.out.println("Compression Ratio: " + (int) totalComp + "% ("
				+ (int) compSize + "/" + testString.length() + ")");
		if (testString.length() < 100) {
			System.out.println("Original Text: [" + testString + "]");
			System.out.println("Output Text  : [" + output + "]");
		}

	}

	public static void compressFile(String inFile) {
		String outFile = inFile.split("\\.")[0] + ".arc";
		HuffmanTree t = new HuffmanTree();
		t.compressFile(new File(inFile), new File(outFile));
	}

	public static void decompressFile(String infile) {
		String outFile = infile.split("\\.")[0] + "_de.txt";
		HuffmanTree tempTree = new HuffmanTree();
		tempTree.decompressFile(new File(infile), new File(outFile));
	}
}
