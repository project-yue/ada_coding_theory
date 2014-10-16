package huffman.io;

import java.io.IOException;
import java.io.OutputStream;

public class BitWriter extends BitOperator {

	private OutputStream out;

	public BitWriter(OutputStream os) {
		super();
		this.out = os;
	}

	public void writeBit(int val) throws IOException {
		this.buffer = setBit(this.buffer, this.bufferPos++, val);
		if (this.bufferPos == BITS_PER_BYTE)
			flush();
	}

	public void writeBits(int[] val) throws IOException {
		for (int i = 0; i < val.length; i++)
			writeBit(val[i]);
	}

	public void flush() throws IOException {
		if (this.bufferPos == 0)
			return;

		this.out.write(this.buffer);
		this.bufferPos = 0;
		this.buffer = 0;
	}

	@Override
	public void close() {
		try {
			flush();
			this.out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int setBit(int pack, int pos, int val) {
		if (val == 1)
			pack |= (val << pos);
		return pack;
	}
}