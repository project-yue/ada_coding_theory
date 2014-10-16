package huffman;

import java.io.File;

public class CompressionDemo {

	public static void compressFile(String infile, String outfile) {
		HuffmanTree t = new HuffmanTree();

		t.compressFile(new File(infile), new File(outfile));

	}

	public static void decompressFile(String infile, String outfile) {
		HuffmanTree t = new HuffmanTree();

		t.decompressFile(new File(infile), new File(outfile));
	}

	public static void main(String[] args) {
		compressFile("wordlist.txt", "compressed.txt");
		System.out.println("done compressing");
		decompressFile("compressed.txt", "decompressed.txt");
	}
}