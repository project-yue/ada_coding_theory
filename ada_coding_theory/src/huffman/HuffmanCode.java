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
public class HuffmanCode {



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
