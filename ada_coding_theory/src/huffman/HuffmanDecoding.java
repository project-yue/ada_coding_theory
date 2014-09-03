package huffman;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class HuffmanDecoding {
	static ArrayList<Byte> byteList;
	static MinPQ<Node> pq;
	static ArrayList<String> dicArray = new ArrayList<String>();

	public static void main(String[] args) {
		readBytes();
		decompressBytes();
	}

	/*
	 * read all bytes from file and add them to a byte list
	 */
	private static void readBytes() {
		FileInputStream in;
		int c;
		try {
			byteList = new ArrayList<Byte>();
			in = new FileInputStream("wordlist_compressed.txt");
			while ((c = in.read()) != -1) {
				byteList.add((byte) c);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Decoding the bytes to the original word list separated by ','
	 */
	private static void decompressBytes() {
		int n = byteList.size();
		StringBuilder letters = new StringBuilder();
		Node head = pq.peek();
		for (int i = n - 1; i >= 0; i--) {
			byte b = byteList.get(i);
			for (int j = 0; j < 8; j++) {
				// if least significant bit is one
				if ((b & 1) == 1) {
					head = head.right;
				} else {
					head = head.left;
				}
				b = (byte) (b >> 1);
				if (head.left == null && head.right == null) {
					letters.append(head.value);
					head = pq.peek();
					continue;
				}
			}
		}
		StringTokenizer elements = new StringTokenizer(letters.toString(), ",");
		while (elements.hasMoreTokens()) {
			dicArray.add(elements.nextToken());
		}
		System.out.println(dicArray.size());
	}
}
