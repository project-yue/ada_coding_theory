package huffman;

/**
 * 
 * @author yue
 *
 */
public class HuffmanNode implements Comparable<HuffmanNode> {

	public HuffmanNode leftChild, rightChild, parent;
	public int symbol;
	public int weight;

	/**
	 * Constructs a leaf node.
	 */
	public HuffmanNode(int symbol, int weight) {
		this.symbol = symbol;
		this.weight = weight;
		leftChild = rightChild = parent = null;
	}

	/**
	 * Create internal node
	 * 
	 * @param left
	 * @param right
	 */
	public HuffmanNode(HuffmanNode left, HuffmanNode right) {
		symbol = HuffmanTree.INCOMPLETE_CODE;
		weight = left.weight + right.weight;
		leftChild = left;
		rightChild = right;
		parent = null;
	}

	public String constructTreeInfo(int l) {
		int level = l;
		String atNode = Character.toString((char) symbol);
		if (symbol == HuffmanTree.INCOMPLETE_CODE)
			atNode = "incomplete  " + "  " + weight;
		else if (symbol == HuffmanTree.EOF)
			atNode = "EOF           " + weight;
		else if (symbol == '\n')
			atNode = "newline       " + weight;
		else if (symbol == '\t')
			atNode = "tab           " + weight;
		else if (symbol == ' ')
			atNode = "space         " + weight;
		else
			atNode += "             " + weight;
		String ret = atNode + " ";
		if (parent == null) {
			int temp = level;
			ret += ":root:" + symbol + ":level " + temp + ":\n"
					+ leftChild.constructTreeInfo(temp + 1) + "\n"
					+ rightChild.constructTreeInfo(temp + 1);
		} else if (leftChild != null && rightChild != null) {
			int temp = level;
			ret += ":left child:" + leftChild.symbol + " :level " + level
					+ ":right child:" + " " + rightChild.symbol + " :level "
					+ level + ":\n";
			ret += leftChild.constructTreeInfo(temp + 1) + "\n";
			ret += rightChild.constructTreeInfo(temp + 1);
		} else if (leftChild != null) {
			int temp = level;
			ret += ":left child:" + leftChild.symbol + ":level " + level
					+ ":\n" + leftChild.constructTreeInfo(++temp);
		} else if (rightChild != null) {
			int temp = level;
			ret += ":right child:" + rightChild.symbol + ":level " + level
					+ ":\n" + rightChild.constructTreeInfo(++temp);
		} else if (parent != null) {
			ret += ":p weight: " + parent.weight + " :level " + level + ":";
		}
		return ret;
	}

	@Override
	public String toString() {
		return constructTreeInfo(0);
	}

	public int compareTo(HuffmanNode rhs) {
		if (this.weight > rhs.weight)
			return 1;
		if (this.weight < rhs.weight)
			return -1;
		if (this.symbol > rhs.symbol)
			return 1;
		return -1;
	}
}