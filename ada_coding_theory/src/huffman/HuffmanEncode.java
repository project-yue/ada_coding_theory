package huffman;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.StringTokenizer;

public class HuffmanEncode {
	static BitSet bitForA = new BitSet();
	static MinPQ<Node> pq;
	static Node[] nodes = new Node[27];
	static int[] huffmanCodeArrayForChar;
	static int codeLength = 0;
	static Node temp;
	static StringBuilder huffmanCodeString;
	static String[] huffmanCodeForLetters = new String[27];
	static StringBuilder allWords = new StringBuilder();

	public static void main(String[] args) {
		HuffmanEncode huffman = new HuffmanEncode();
		huffman.buildHuffmanTree();
		huffman.readWords();
		writeBytesToFile();
	}

	/*
	 * write the compressed word list (in bytes) to file
	 */
	private static void writeBytesToFile() {
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

	/*
	 * compress the string to byte using huffman encoding
	 */
	private static byte[] compressWord(String test) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < test.length(); i++) {
			char c = test.charAt(i);
			if (c == ',') {
				s.append(huffmanCodeForLetters[26]);
			} else {
				s.append(huffmanCodeForLetters[(int) (c - 'a')]);
			}
		}
		for (int i = 0; i < s.length(); i++) {
			int pos = Integer.parseInt(s.substring(i, i + 1));
			if (pos == 1) {
				bitForA.set(i);
			}
		}
		return toByteArray(bitForA);
	}

	/*
	 * Build a Huffman tree based on the pre-calculated frequency of each
	 * character. For easier decoding, put a ',' between each word
	 * 
	 * huffmanCodeForLetters : is and String array containing huffman
	 * representation for each character. For example, [1011,111,...]
	 */
	private void buildHuffmanTree() {
		Node node;
		for (int i = 0; i < 27; i++) {
			if (i == 26) {
				node = new Node(',', FrequencyList.fre[i]);
			} else {
				node = new Node((char) ('a' + i), FrequencyList.fre[i]);
			}
			nodes[i] = node;
		}
		pq = new MinPQ<Node>(nodes);
		while (pq.size() > 1) {
			Node minFirst = pq.min();
			Node minSecond = pq.min();
			Node newNode = new Node('-', minFirst.frequency
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

	/*
	 * Convert a bit sets to the corresponding byte array
	 * 
	 * @param Bitset bits
	 */
	public static byte[] toByteArray(BitSet bits) {
		byte[] bytes = new byte[(bits.length() + 7) / 8];
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i)) {
				bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
			}
		}
		return bytes;
	}

	/*
	 * look up the bit representation of each character In fact, do a DFS
	 */
	private static void lookup(char requiredChar) {
		huffmanCodeArrayForChar = new int[8];
		codeLength = 0;
		huffmanCodeString = new StringBuilder();
		Stack<Node> stack = new Stack<Node>();
		stack.push(temp);
		while (!stack.isEmpty()) {
			Node node = (Node) stack.peek();
			node.visited = true;
			Node nextNode = getNextNode(node);
			if (nextNode == null) {
				stack.pop();
				codeLength--;
			} else {
				if (nextNode.value == requiredChar) {
					for (int i = 0; i < codeLength; i++) {
						huffmanCodeString.append(huffmanCodeArrayForChar[i]);
					}
					resetNodes(temp);
					return;
				}
				stack.push(nextNode);
				nextNode.visited = true;
			}
		}
		resetNodes(temp);
	}

	/*
	 * reset each node to unvisited
	 */
	private static void resetNodes(Node node) {
		if (node == null) {
			return;
		}
		resetNodes(node.left);
		node.visited = false;
		resetNodes(node.right);
	}

	/*
	 * get next available node during DFS and keep recording the look route
	 * record 1 if go right record 0 if go left
	 */
	private static Node getNextNode(Node node) {
		Node left = node.left;
		Node right = node.right;
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

	/*
	 * read the words from a txt file
	 */
	private void readWords() {
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
}