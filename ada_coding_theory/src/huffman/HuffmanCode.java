package huffman;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Huffman Code
 * 
 * @author Yue
 * @version 06-09-14 compression and decompression work
 */
public class HuffmanCode {
	// the frequency of all letters in the txt
	// the issue becomes counting frequency

	public PriorityQueue<QueueNode> pq;
	public int codeLength = 0;
	public QueueNode temp;
	public StringBuilder huffmanCodeString;
	public String[] symbolArray;
	public StringBuilder allWordsBuffer;
	// hold all tokens in byte
	public ArrayList<Byte> compressList;
	// hold all tokens in String
	public ArrayList<String> decompressLst;
	// hold all nodes
	public ArrayList<QueueNode> nodeLst;

	public static void main(String[] args) {
		HuffmanCode huffman = new HuffmanCode();
		huffman.compress();
		huffman.decompress();
	}

	public HuffmanCode() {
		// symbols currently is fixed
		this.symbolArray = new String[27];
		this.compressList = new ArrayList<>();
		this.decompressLst = new ArrayList<>();
		this.allWordsBuffer = new StringBuilder();
	}

	public void compress() {
		buildHuffmanTree();
		readRawContentFromFile();
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
		BitSet bits = new BitSet();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < test.length(); i++) {
			char c = test.charAt(i);
			if (c == ',') {
				s.append(symbolArray[26]);
			} else {
				// ascii arithmetic
				s.append(symbolArray[(int) (c - 'a')]);
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
	 * character. For convenience, use ',' as delimiter
	 * 
	 * symbolArray : is and String array containing huffman representation for
	 * each character. For example, [1011,111,...]
	 * 
	 * @version 07-09 it currently has no counting frequency function.it reads a
	 *          preset array and all the symbol frequencies are pre-calculated
	 *          by other software
	 */
	private void buildHuffmanTree() {
		QueueNode node;
		this.nodeLst = countFrequency();
		QueueNode[] nodes = new QueueNode[nodeLst.size()];
		// indices are from a-z and the last one for ,
		// should calculate the frequency
		for (int i = 0; i < nodeLst.size(); i++) {
			if (i == 26) {
				node = new QueueNode(',', nodeLst.get(i).frequency);
			} else {
				node = new QueueNode((char) ('a' + i), nodeLst.get(i).frequency);
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
			symbolArray[i] = huffmanCodeString.toString();
		}
		lookup(',');
		symbolArray[26] = huffmanCodeString.toString();
	}

	private ArrayList<QueueNode> countFrequency() {
		File mFile = new File("wordlist.txt");
		ArrayList<QueueNode> nodes = new ArrayList<>();
		for (int i = 0; i < 27; i++) {
			QueueNode temp = new QueueNode();
			nodes.add(temp);
		}
		try {
			BufferedReader input = new BufferedReader(new FileReader(mFile));
			String line = null;
			int counter = 0;
			while ((line = input.readLine()) != null) {
				for (char temp : line.toCharArray()) {
					int tempIndex = temp - 'a';
					if (tempIndex < 26)
						nodes.get(tempIndex).frequency++;
				}
				counter++;
			}
			nodes.get(26).frequency = counter;
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nodes;
	}

	/**
	 * Convert a bit set to the corresponding byte array
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
		int[] huffmanCodeArrayForChar = new int[8];
		codeLength = 0;
		huffmanCodeString = new StringBuilder();
		Stack<QueueNode> stack = new Stack<QueueNode>();
		stack.push(temp);
		while (!stack.isEmpty()) {
			QueueNode node = (QueueNode) stack.peek();
			node.visited = true;
			QueueNode nextNode = getNextNode(node, huffmanCodeArrayForChar);
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
	private QueueNode getNextNode(QueueNode node, int[] charArray) {
		QueueNode left = node.left;
		QueueNode right = node.right;
		if (left != null && !left.visited) {
			charArray[codeLength] = 0;
			codeLength++;
			return left;
		} else if (right != null && !right.visited) {
			charArray[codeLength] = 1;
			codeLength++;
			return right;
		}
		return null;
	}

	// /////////////////I/O///////////////////////////

	/**
	 * read the words from a file. The process constructs raw data in String
	 * representation
	 */
	private void readRawContentFromFile() {
		File tempFile = new File("wordlist.txt");
		try {
			BufferedReader inputReader = new BufferedReader(new FileReader(
					tempFile));
			String line = null;
			while ((line = inputReader.readLine()) != null) {
				allWordsBuffer.append(line);
				allWordsBuffer.append(',');
			}
			inputReader.close();
		} catch (Exception e) {
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
		for (int i = n - 1; i > 0; i--) {
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
		writeStringToFile();
	}

	/**
	 * read file to bytes
	 */
	private void readBytesFromFile() {
		FileInputStream in;
		int c;
		try {
			in = new FileInputStream("wordlist_compressed.txt");
			compressList = new ArrayList<Byte>();
			// skip the header files
			// this is the length of the header
			System.out.println(in.read());
			System.out.println(in.read());
			System.out.println(in.read());
			in.skip(155);
			// read header ends
			while ((c = in.read()) != -1) {
				compressList.add((byte) c);
			}
		} catch (Exception e) {
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
			// for generate header
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < this.nodeLst.size(); i++) {
				sb.append(this.nodeLst.get(i).frequency);
				if (i < this.nodeLst.size() - 1)
					sb.append(",");
			}
			sb.insert(0, sb.toString().getBytes().length);
			System.out.println("skip byte length "
					+ sb.toString().getBytes().length);
			byte[] header = sb.toString().getBytes();
			for (int i = 0; i < header.length; i++)
				out.write(header[i]);
			// header ends
			byte[] bytes = compressWord(allWordsBuffer.toString());
			for (int i = 0; i < bytes.length; i++) {
				out.write(bytes[i]);
			}
			out.close();
		} catch (Exception e) {
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