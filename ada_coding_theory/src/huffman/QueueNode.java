package huffman;

public class QueueNode implements Comparable<QueueNode> {
	public char value;
	public int frequency;
	public QueueNode left = null;
	public QueueNode right = null;
	public boolean visited = false;

	public QueueNode(char v, int fre) {
		this.value = v;
		this.frequency = fre;
	}

	public QueueNode() {
	}

	@Override
	public int compareTo(QueueNode o) {
		return frequency - o.frequency;
	}
}
