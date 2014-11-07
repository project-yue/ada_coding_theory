package huffman;

import java.io.File;

/**
 * The test can take long time due to the size of the compressed file. The test
 * will run the compression and decompression several times to obtain different
 * results
 * 
 * @author yue
 *
 */
public class HuffmanCode {

	public static void compressFile(String inFile, boolean isAdaptive) {
		// add output file with .arc surfix
		String outFile = inFile.split("\\.")[0] + ".arc";
		HuffmanTree t = new HuffmanTree();
		t.isAdaptive = isAdaptive;
		long time = System.nanoTime();
		t.compressFile(new File(inFile), new File(outFile));
		time = System.nanoTime() - time;
		System.out.println("Compression took " + time + " nanoseconds");
	}

	public static void decompressFile(String infile, boolean isAdaptive) {
		String outFile = infile.split("\\.")[0] + "_de.txt";
		HuffmanTree tempTree = new HuffmanTree();
		long time = System.nanoTime();
		if (isAdaptive)
			tempTree.decompressFile(new File(infile), new File(outFile));
		else
			tempTree.readFileBytes(new File(infile));
		time = System.nanoTime() - time;
		System.out.println("Decompression took " + time + " nanoseconds");
	}

	public static void main(String[] args) {
		// await to add built system compression call
		// compressFile("efficiency_test");
	}
}
