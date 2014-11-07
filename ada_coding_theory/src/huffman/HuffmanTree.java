package huffman;

import huffman.io.BitReader;
import huffman.io.BitWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * 
 * @author yue
 *
 */
public class HuffmanTree {

	public final static int FAULT = -3;
	public final static int INCOMPLETE_CODE = -2;
	public final static int EOF = 0;

	public HuffmanNode root;
	// mapping chars to nodes
	public Map<Integer, HuffmanNode> symbolMap;
	public List<Integer> data;
	public boolean isAdaptive;

	public HuffmanTree() {
	}

	/**
	 * Compress a file, and output the result to another file
	 * 
	 * @param infile
	 * @param outfile
	 */
	public void compressFile(File infile, File outfile) {
		symbolMap = new HashMap<Integer, HuffmanNode>();
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					outfile));
			data = readData(new FileInputStream(infile));
			createTreeNodes();
			if (isAdaptive) {
				//
				writeEncodingInfo(out);
			}
			BitWriter bout = new BitWriter(out);
			for (int ch : data) {
				bout.writeBits(getCode(ch & 255));
			}
			// bitwise AND operation
			bout.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		System.out.println("Compression is finished");
	}

	/**
	 * Decompress a file, and output the result to another file
	 * 
	 * @param infile
	 * @param outfile
	 */
	public void decompressFile(File infile, File outfile) {
		symbolMap = new HashMap<Integer, HuffmanNode>();
		try {
			DataInputStream in = new DataInputStream(
					new FileInputStream(infile));
			readEncodingInfo(in);
			createTreeNodes();
			BitReader bin = new BitReader(in);
			data = readCompressedData(bin);
			bin.close();
			FileOutputStream out = new FileOutputStream(outfile);
			for (int i : data)
				out.write(i);
			out.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		System.out.println("Decompression is finished");
	}

	public int readFileBytes(File infile) {
		int result = 0;
		try {
			DataInputStream in = new DataInputStream(
					new FileInputStream(infile));
			while (in.read() != -1)
				result++;
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param in
	 * @throws IOException
	 */
	private ArrayList<Integer> readData(InputStream in) {
		ArrayList<Integer> result = new ArrayList<>();
		int ch;
		// read characters until end of file
		try {
			while ((ch = in.read()) != -1) {
				// if character had not yet been seen, put it in the tree
				// (weight = 1)
				if (symbolMap.get(ch) == null)
					symbolMap.put(ch, new HuffmanNode(ch, 1));
				// if character has already been seen, increment weight
				else
					symbolMap.get(ch).weight++;
				// keep track of all characters (in order)
				result.add(ch);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		symbolMap.put(EOF, new HuffmanNode(EOF, 1));
		result.add(EOF);
		return result;
	}

	private ArrayList<Integer> readCompressedData(BitReader in)
			throws IOException {
		ArrayList<Integer> result = new ArrayList<>();
		String codeWord = "";
		int bit;
		int decodedCharValue = 0;

		// read bits until end of file
		while ((bit = in.readBit()) != -1) {
			if (bit == 0)
				codeWord += "0";
			else
				codeWord += "1";
			decodedCharValue = getChar(codeWord);
			// incomplete code are internal nodes without symbol representations
			if (decodedCharValue == INCOMPLETE_CODE)
				continue;
			else if (decodedCharValue == FAULT)
				throw new IOException("Decoding error");
			// completed
			else if (decodedCharValue == EOF)
				return result;
			result.add(decodedCharValue);
			codeWord = "";
		}
		return result;
	}

	private void writeEncodingInfo(DataOutputStream out) throws IOException {
		for (Map.Entry<Integer, HuffmanNode> e : symbolMap.entrySet()) {
			out.writeByte(e.getKey());
			out.writeInt(e.getValue().weight);
		}
		// special code to indicate end of file
		out.writeByte(0);
		out.writeInt(0);
	}

	private void readEncodingInfo(DataInputStream in) throws IOException {
		int ch;
		int num;
		while (true) {
			ch = in.readByte();
			num = in.readInt();
			if (num == 0) // indicates the EOF
				return;
			symbolMap.put(ch, new HuffmanNode(ch, num));
		}
	}

	// keep a record of tree nodes. may be used to draw out the nodes
	private void createTreeNodes() {
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>();
		pq.addAll(symbolMap.values());
		while (pq.size() > 1) {
			// empty queue remove will throw exception
			HuffmanNode left = pq.remove();
			HuffmanNode right = pq.remove();
			HuffmanNode combo = new HuffmanNode(left, right);
			left.parent = combo;
			right.parent = combo;
			combo.weight = left.weight + right.weight;
			pq.offer(combo);
		}
		root = pq.element();
		// System.out.println(root);
	}

	private int[] getCode(int ch) {

		HuffmanNode currentNode = symbolMap.get((int) ch);
		String bitCode = new String("");
		if (currentNode == null)
			return null;
		while (currentNode.parent != null) {
			if (currentNode.parent.leftChild == currentNode)
				bitCode = "0" + bitCode;
			else
				bitCode = "1" + bitCode;
			currentNode = currentNode.parent;

		}
		int[] output = new int[bitCode.length()];
		for (int i = 0; i < bitCode.length(); i++) {
			// to binary
			output[i] = (int) bitCode.charAt(i) - 48;
		}
		return output;
	}

	private int getChar(String codeWord) {
		HuffmanNode finger = root;

		for (int i = 0; finger != null && i < codeWord.length(); i++)
			if (codeWord.charAt(i) == '0')
				finger = finger.leftChild;
			else
				finger = finger.rightChild;

		if (finger == null)
			return FAULT;
		return finger.symbol;
	}

}
