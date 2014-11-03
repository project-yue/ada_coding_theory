package huffman;

import huffman.io.BitReader;
import huffman.io.BitWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	public Map<Integer, HuffmanNode> symbols;
	public List<Integer> data;

	public HuffmanTree() {
	}

	/**
	 * Compress a file, and output the result to another file
	 * 
	 * @param infile
	 * @param outfile
	 */
	public void compressFile(File infile, File outfile) {
		symbols = new HashMap<Integer, HuffmanNode>();
		data = new ArrayList<Integer>();

		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					outfile));
			readData(new FileInputStream(infile));
			createTree();
			writeEncodingInfo(out);
			BitWriter bout = new BitWriter(out);
			for (int ch : data)
				bout.writeBits(getCode(ch & 0xff));
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
		symbols = new HashMap<Integer, HuffmanNode>();
		data = new ArrayList<Integer>();
		try {
			DataInputStream in = new DataInputStream(
					new FileInputStream(infile));
			readEncodingInfo(in);
			createTree();
			BitReader bin = new BitReader(in);
			readCompressedData(bin);
			bin.close();

			FileOutputStream out = new FileOutputStream(outfile);
			for (int i : data)
				out.write(i);
		} catch (IOException e) {
			System.err.println(e);
		}
		System.out.println("Decompression is finished");
	}

	/**
	 * 
	 * @param in
	 * @throws IOException
	 */
	private void readData(InputStream in) throws IOException {
		int ch;
		// read characters until end of file
		while ((ch = in.read()) != -1) {
			// if character had not yet been seen, put it in the tree (weight =
			// 1)
			if (symbols.get(ch) == null)
				symbols.put(ch, new HuffmanNode(ch, 1));
			// if character has already been seen, increment weight
			else
				symbols.get(ch).weight++;
			// keep track of all characters (in order)
			data.add(ch);
		}
		symbols.put(EOF, new HuffmanNode(EOF, 1));
		data.add(EOF);
	}

	private void readCompressedData(BitReader in) throws IOException {
		String bits = "";
		int bit;
		int decode = 0;

		// read bits until end of file
		while ((bit = in.readBit()) != -1) {
			if (bit == 0)
				bits += "0";
			else
				bits += "1";
			decode = getChar(bits);

			if (decode == INCOMPLETE_CODE)
				continue;
			else if (decode == FAULT)
				throw new IOException("Decoding error");
			// completed
			else if (decode == EOF)
				return;
			data.add(decode);
			bits = "";
		}
	}

	private void writeEncodingInfo(DataOutputStream out) throws IOException {
		for (Map.Entry<Integer, HuffmanNode> e : symbols.entrySet()) {
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
			symbols.put(ch, new HuffmanNode(ch, num));
		}
	}

	// keep a record of tree nodes. may be used to draw out the nodes
	private void createTree() {
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<HuffmanNode>();
		pq.addAll(symbols.values());
		while (pq.size() > 1) {
			HuffmanNode left = pq.remove();
			HuffmanNode right = pq.remove();
			HuffmanNode combo = new HuffmanNode(left, right);
			left.parent = combo;
			right.parent = combo;
			combo.weight = left.weight + right.weight;
			pq.offer(combo);
		}
		root = pq.element();
		System.out.println(root);
	}

	private int[] getCode(int ch) {

		HuffmanNode currentNode = symbols.get((int) ch);
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
			output[i] = (int) bitCode.charAt(i) - 48;
		}
		return output;
	}

	private int getChar(String code) {
		HuffmanNode finger = root;

		for (int i = 0; finger != null && i < code.length(); i++)
			if (code.charAt(i) == '0')
				finger = finger.leftChild;
			else
				finger = finger.rightChild;

		if (finger == null)
			return FAULT;
		return finger.symbol;
	}

}
