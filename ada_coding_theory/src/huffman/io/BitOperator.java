package huffman.io;

public abstract class BitOperator {

	final static int BITS_PER_BYTE = 8;

	protected int buffer;
	protected int bufferPos;

	public BitOperator() {
		this.buffer = 0;
		this.bufferPos = 0;
	}

	public abstract void close();

}
