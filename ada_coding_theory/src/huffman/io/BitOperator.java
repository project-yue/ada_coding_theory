package huffman.io;

/**
 * 
 * this class handles bits, so that data conversion occurs in bits. and example
 * of this: if we have 3 'a's and 1 'b'. a will have the least bit 01, when
 * occurrence of a will make 011 and b 100 or 1000, that dont matter. In Huffman
 * Code, 011, will make a byte and 1000 will be another byte. eventually, we get
 * 2 bytes. Which is not quite efficient.
 * 
 * 
 * @author yue
 *
 */
public abstract class BitOperator {

	// controls the read buffer size
	final static int BITS_PER_BYTE = 8;

	protected int buffer;
	protected int bufferPos;

	public BitOperator() {
		this.buffer = 0;
		this.bufferPos = 0;
	}

	public abstract void close();

}
