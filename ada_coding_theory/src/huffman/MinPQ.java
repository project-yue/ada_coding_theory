package huffman;

import java.util.Comparator;

/*
 * A min priority queue
 */
public class MinPQ<Node> {
	private Node[] nodesPQ;
	private int n;
	private Comparator<Node> comparator;

	public MinPQ(int capacity) {
		this.n = 0;
		nodesPQ = (Node[]) new Object[n + 1];
	}

	public MinPQ(int capacity, Comparator<Node> com) {
		this.n = 0;
		this.comparator = comparator;
		nodesPQ = (Node[]) new Object[n + 1];
	}

	public MinPQ(Node[] nodes) {
		n = nodes.length;
		nodesPQ = (Node[]) new Object[n + 1];
		for (int i = 0; i < n; i++) {
			nodesPQ[i + 1] = nodes[i];
		}
		for (int i = n / 2; i >= 1; i--) {
			sink(i);
		}
	}

	public boolean isEmpty() {
		return n == 0;
	}

	public int size() {
		return n;
	}

	public Node min() {
		if (isEmpty()) {
			return null;
		}
		Node node = nodesPQ[1];
		swap(1, n);
		n--;
		sink(1);
		nodesPQ[n + 1] = null;
		return node;
	}

	public Node peek() {
		if (isEmpty()) {
			return null;
		}
		Node node = nodesPQ[1];
		return node;
	}

	public void insert(Node node) {
		if (n == nodesPQ.length - 1) {
			resize(2 * n + 1);
		}
		n++;
		nodesPQ[n] = node;
		swim(n);
	}

	private void resize(int i) {
		Node[] temp = (Node[]) new Object[i];
		for (int j = 1; j < n + 1; j++) {
			temp[j] = nodesPQ[j];
		}
		nodesPQ = temp;
	}

	private void sink(int index) {
		while (index * 2 <= n) {
			int childIndex = 2 * index;
			if (childIndex < n && less(childIndex + 1, childIndex)) {
				childIndex++;
			}
			if (less(index, childIndex)) {
				break;
			}
			swap(index, childIndex);
			index = childIndex;
		}
	}

	private void swim(int index) {
		while (index > 1 && (less(index, index / 2))) {
			swap(index / 2, index);
			index /= 2;
		}
	}

	private void swap(int index, int childIndex) {
		Node tmp = nodesPQ[index];
		nodesPQ[index] = nodesPQ[childIndex];
		nodesPQ[childIndex] = tmp;
	}

	private boolean less(int index, int childIndex) {
		if (comparator == null) {
			return ((Comparable<Node>) nodesPQ[index])
					.compareTo(nodesPQ[childIndex]) < 0;
		} else {
			return comparator.compare(nodesPQ[index], nodesPQ[childIndex]) < 0;
		}
	}
}