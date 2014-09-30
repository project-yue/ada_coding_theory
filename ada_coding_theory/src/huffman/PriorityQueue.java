package huffman;

import java.util.Comparator;

/**
 * A min priority queue with linked structure.
 */
public class PriorityQueue<E> {
	public E[] nodesPQ;
	public int size;
	public Comparator<E> comparator;

	public PriorityQueue(E[] nodes) {
		size = nodes.length;
		nodesPQ = (E[]) new Object[size + 1];
		for (int i = 0; i < size; i++) {
			nodesPQ[i + 1] = nodes[i];
		}
		for (int i = size / 2; i >= 1; i--) {
			sink(i);
		}
	}

	public E min() {
		if (size == 0) {
			return null;
		}
		E node = nodesPQ[1];
		swap(1, size);
		size--;
		sink(1);
		nodesPQ[size + 1] = null;
		return node;
	}

	public E peek() {
		if (size == 0) {
			return null;
		}
		E node = nodesPQ[1];
		return node;
	}

	public void insert(E node) {
		if (size == nodesPQ.length - 1) {
			resize(2 * size + 1);
		}
		size++;
		nodesPQ[size] = node;
		swim(size);
	}

	private void resize(int i) {
		E[] temp = (E[]) new Object[i];
		for (int j = 1; j < size + 1; j++) {
			temp[j] = nodesPQ[j];
		}
		nodesPQ = temp;
	}

	/**
	 * sink node down to the leave
	 * 
	 * @param index
	 */
	private void sink(int index) {
		while (index * 2 <= size) {
			int childIndex = 2 * index;
			if (childIndex < size && isLess(childIndex + 1, childIndex)) {
				childIndex++;
			}
			if (isLess(index, childIndex)) {
				break;
			}
			swap(index, childIndex);
			index = childIndex;
		}
	}

	/**
	 * bubble node up
	 * 
	 * @param index
	 */
	private void swim(int index) {
		while (index > 1 && (isLess(index, index / 2))) {
			swap(index / 2, index);
			index /= 2;
		}
	}

	private void swap(int index, int childIndex) {
		E tmp = nodesPQ[index];
		nodesPQ[index] = nodesPQ[childIndex];
		nodesPQ[childIndex] = tmp;
	}

	private boolean isLess(int index, int childIndex) {
		if (comparator == null) {
			return ((Comparable<E>) nodesPQ[index])
					.compareTo(nodesPQ[childIndex]) < 0;
		} else {
			return comparator.compare(nodesPQ[index], nodesPQ[childIndex]) < 0;
		}
	}
}