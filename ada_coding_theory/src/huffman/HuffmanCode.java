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

	public static void compressFile(String inFile) {
		// add output file with .arc surfix
		String outFile = inFile.split("\\.")[0] + ".arc";
		HuffmanTree t = new HuffmanTree();
		t.compressFile(new File(inFile), new File(outFile));
	}

	public static void decompressFile(String infile) {
		String outFile = infile.split("\\.")[0] + "_de.txt";
		HuffmanTree tempTree = new HuffmanTree();
		tempTree.decompressFile(new File(infile), new File(outFile));
	}

	public static void main(String[] args) {
		// await to add built system compression call
		// compressFile("efficiency_test");
	}
}
