package huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Huffman Code
 * 
 * @author go
 * @version 06-09-14 compression and decompression work
 */
public class HuffmanCode {
	// the frequency of all letters in the txt
	// the issue becomes counting frequency
	public static final int[] ALPHABET = { 61199, 16075, 22995, 25558, 73135,
			9831, 18504, 17526, 47007, 2486, 11371, 35601, 20106, 38477, 41239,
			19186, 1152, 44899, 53635, 36024, 26867, 6301, 8576, 2315, 14122,
			3386, 108236 };

	public BitSet bits;
	public PriorityQueue<QueueNode> pq;
	public QueueNode[] nodes = new QueueNode[27];
	public int[] huffmanCodeArrayForChar;
	public int codeLength = 0;
	public QueueNode temp;
	public StringBuilder huffmanCodeString;
	public String[] huffmanCodeForLetters;
	public StringBuilder allWords = new StringBuilder();
	// de
	public ArrayList<Byte> compressList;
	public ArrayList<String> decompressLst;

	public static void main(String[] args) {
		HuffmanCode huffman = new HuffmanCode();
		huffman.compress();
		huffman.decompress();
	}

	public HuffmanCode() {
		this.huffmanCodeForLetters = new String[27];
		this.compressList = new ArrayList<>();
		this.decompressLst = new ArrayList<String>();
		this.bits = new BitSet();
	}

	public void compress() {
		buildHuffmanTree();
		readRawContentFromDoc();
		writeBytesToFile();
	}

	public void decompress() {
		readBytesFromFile();
		decompressBytes();
	}

	/**
	 * compress the string to byte using huffman encoding
	 * 
	 */
	private byte[] compressWord(String test) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < test.length(); i++) {
			char c = test.charAt(i);
			if (c == ',') {
				s.append(huffmanCodeForLetters[26]);
			} else {
				// ascii arithmetic
				s.append(huffmanCodeForLetters[(int) (c - 'a')]);
			}
		}
		for (int i = 0; i < s.length(); i++) {
			int pos = Integer.parseInt(s.substring(i, i + 1));
			if (pos == 1) {
				bits.set(i);
			}
		}
		return toByteArray(bits);
	}

	/**
	 * Build a Huffman tree based on the pre-calculated frequency of each
	 * character. For easier decoding, put a ',' between each word
	 * 
	 * huffmanCodeForLetters : is and String array containing huffman
	 * representation for each character. For example, [1011,111,...]
	 * 
	 * @version 07-09 it currently has no counting frequency function.it reads a
	 *          preset array and all the symbol frequencies are pre-calculated
	 *          by other software
	 */
	private void buildHuffmanTree() {
		QueueNode node;
		for (int i = 0; i < 27; i++) {
			if (i == 26) {
				node = new QueueNode(',', ALPHABET[i]);
			} else {
				node = new QueueNode((char) ('a' + i), ALPHABET[i]);
			}
			nodes[i] = node;
		}
		pq = new PriorityQueue<QueueNode>(nodes);
		while (pq.size > 1) {
			QueueNode minFirst = pq.min();
			QueueNode minSecond = pq.min();
			QueueNode newNode = new QueueNode('-', minFirst.frequency
					+ minSecond.frequency);
			newNode.left = minFirst;
			newNode.right = minSecond;
			pq.insert(newNode);
		}
		temp = pq.peek();
		for (int i = 0; i < 26; i++) {
			lookup((char) ('a' + i));
			huffmanCodeForLetters[i] = huffmanCodeString.toString();
		}
		lookup(',');
		huffmanCodeForLetters[26] = huffmanCodeString.toString();
	}

	/**
	 * Convert a bit sets to the corresponding byte array
	 * 
	 * @param Bitset
	 */
	public byte[] toByteArray(BitSet bits) {
		byte[] bytes = new byte[(bits.length() + 7) / 8];
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i)) {
				bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
			}
		}
		return bytes;
	}

	/**
	 * look up the bit representation in a dfs manner
	 */
	private void lookup(char requiredChar) {
		huffmanCodeArrayForChar = new int[8];
		codeLength = 0;
		huffmanCodeString = new StringBuilder();
		Stack<QueueNode> stack = new Stack<QueueNode>();
		stack.push(temp);
		while (!stack.isEmpty()) {
			QueueNode node = (QueueNode) stack.peek();
			node.visited = true;
			QueueNode nextNode = getNextNode(node);
			if (nextNode == null) {
				stack.pop();
				codeLength--;
			} else {
				if (nextNode.value == requiredChar) {
					for (int i = 0; i < codeLength; i++) {
						huffmanCodeString.append(huffmanCodeArrayForChar[i]);
					}
					resetVisitedNodes(temp);
					return;
				}
				stack.push(nextNode);
				nextNode.visited = true;
			}
		}
		resetVisitedNodes(temp);
	}

	/**
	 * reset each node to unvisited
	 */
	private void resetVisitedNodes(QueueNode node) {
		if (node == null) {
			return;
		}
		resetVisitedNodes(node.left);
		node.visited = false;
		resetVisitedNodes(node.right);
	}

	/**
	 * get next available node during DFS and keep recording the look route
	 * record 1 if go right record 0 if go left
	 */
	private QueueNode getNextNode(QueueNode node) {
		QueueNode left = node.left;
		QueueNode right = node.right;
		if (left != null && !left.visited) {
			huffmanCodeArrayForChar[codeLength] = 0;
			codeLength++;
			return left;
		} else if (right != null && !right.visited) {
			huffmanCodeArrayForChar[codeLength] = 1;
			codeLength++;
			return right;
		}
		return null;
	}

	// /////////////////I/O///////////////////////////

	/**
	 * read the words from a txt file. The process constructs raw data in String
	 * representation
	 */
	private void readRawContentFromDoc() {
		File mFile = null;
		mFile = new File("wordlist.txt");
		// TODO Auto-generated catch block
		try {
			BufferedReader input = new BufferedReader(new FileReader(mFile));
			String line = null;
			while ((line = input.readLine()) != null) {
				allWords.append(line);
				allWords.append(',');
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * decompression can take a long time as so far it seems going polynomial
	 * time
	 */
	private void decompressBytes() {
		int n = compressList.size();
		StringBuilder letters = new StringBuilder();
		QueueNode head = pq.peek();
		for (int i = n - 1; i >= 0; i--) {
			byte b = compressList.get(i);
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
			decompressLst.add(elements.nextToken());
		}
		System.out.println(decompressLst.size());
		writeStringToFile();
	}

	/**
	 * read file to bytes
	 */
	private void readBytesFromFile() {
		FileInputStream in;
		int c;
		try {
			compressList = new ArrayList<Byte>();
			in = new FileInputStream("wordlist_compressed.txt");
			while ((c = in.read()) != -1) {
				compressList.add((byte) c);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * compression write bytes to file
	 */
	private void writeBytesToFile() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("wordlist_compressed.txt");
			byte[] bytes = compressWord(allWords.toString());
			for (int i = 0; i < bytes.length; i++) {
				out.write(bytes[i]);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * decompression, write string to file
	 */
	private void writeStringToFile() {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("wordlist_decompressed.txt");
			PrintStream ps = new PrintStream(out);
			for (String temp : this.decompressLst) {
				ps.print(temp + "\n");
			}
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}