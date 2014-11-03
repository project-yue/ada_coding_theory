package huffman.io;

import java.io.IOException;
import java.io.InputStream;

public class BitReader extends BitOperator {

	private InputStream in;

	public BitReader(InputStream is) {
		this.in = is;
		this.bufferPos = BITS_PER_BYTE;
	}

	public int readBit() throws IOException {
		if (this.bufferPos == BITS_PER_BYTE) {
			this.buffer = this.in.read();
			if (this.buffer == -1) {
				return -1;
			}
			this.bufferPos = 0;
		}

		return getBit(this.buffer, this.bufferPos++);
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
		// if the result is not 0 returns 1
		return (pack & (1 << pos)) != 0 ? 1 : 0;
	}
}