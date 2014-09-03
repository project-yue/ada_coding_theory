package huffman;

public class Node implements Comparable<Node> {
	char value;
	int frequency;
	Node left = null;
	Node right = null;
	boolean visited = false;

	public Node(char v, int fre) {
		this.value = v;
		this.frequency = fre;
	}

	@Override
	public int compareTo(Node o) {
		return frequency - o.frequency;
	}
}
