package huffman.io;

import java.io.IOException;
import java.io.OutputStream;

public class BitWriter extends BitOperator {

	private OutputStream out;

	public BitWriter(OutputStream os) {
		super();
		this.out = os;
	}

	public void writeBits(int[] val) {
		for (int i = 0; i < val.length; i++)
			writeBit(val[i]);
	}

	/**
	 * write the buffer to file and clear the buffer
	 */
	public void flush() {
		if (super.bufferPos == 0)
			return;
		try {
			System.out.println(super.buffer);
			this.out.write(super.buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.bufferPos = 0;
		super.buffer = 0;
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

	private void writeBit(int val) {
		super.buffer = arrangeBitToBits(super.buffer, super.bufferPos++, val);
		if (super.bufferPos == BITS_PER_BYTE)
			flush();
	}

	private int arrangeBitToBits(int buffer, int position, int val) {
		if (val == 1) {
			// bitwise OR
			buffer |= (val << position);
			System.out.println(Integer.toBinaryString(buffer) + " "
					+ Integer.toBinaryString(position) + " "
					+ Integer.toBinaryString(val));
		}
		return buffer;
	}
}