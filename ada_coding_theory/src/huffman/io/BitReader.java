package huffman.io;

import java.io.IOException;
import java.io.InputStream;

public class BitReader extends BitOperator {

	private InputStream in;

	public BitReader(InputStream is) {
		this.in = is;
		super.bufferPos = BITS_PER_BYTE;
	}

	public int readBit() throws IOException {
		if (super.bufferPos == BITS_PER_BYTE) {
			super.buffer = this.in.read();
			if (super.buffer == -1) {
				return -1;
			}
			super.bufferPos = 0;
		}

		return getBit(super.buffer, super.bufferPos++);
	}

	@Override
	public void close() {
		try {
			this.in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getBit(int pack, int pos) {
		// shift 1 left with bits of the postion
		// bitwise AND operation between 2 binary
		if ((pack & (1 << pos)) != 0) {
			return 1;
		} else {
			return 0;
		}
	}
}