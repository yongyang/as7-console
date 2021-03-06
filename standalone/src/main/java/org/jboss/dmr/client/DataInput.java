/*
 * JBoss, Home of Professional Open Source
 * Copyright <YEAR> Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.dmr.client;

import java.io.IOException;

public class DataInput {
	private int pos = 0;
	private byte[] bytes;
	
	public DataInput(byte[] bytes) {
		this.bytes = bytes;
	}

	public int skipBytes(int n) throws IOException {
		return pos += n;
	}

	public boolean readBoolean() throws IOException {
		 return readByte() != 0;
	}

	public byte readByte() throws IOException {
		return bytes[pos++];
	}

	public int readUnsignedByte() throws IOException {
		return readByte() & 0xFF;
	}

	public short readShort() throws IOException {
		return (short)(bytes[pos++] << 8 | bytes[pos++]);
	}

	public int readUnsignedShort() throws IOException {
		return bytes[pos++] << 8 | bytes[pos++];
	}

	public char readChar() throws IOException {
		return (char)(bytes[pos++] << 8 | bytes[pos++]);
	}

	public int readInt() throws IOException {
        int a = readUnsignedByte();
        int b = readUnsignedByte();
        int c = readUnsignedByte();
        int d = readUnsignedByte();
        return (a << 24) | (b << 16) | (c << 8) | d;

	}

	public long readLong() throws IOException {
        long a = readInt();
        long b = readInt() & 0x0ffffffff;
        return (a << 32) | b;
		              
	}

	public float readFloat() throws IOException {
		// TODO Auto-generated method stub
		readInt();
		return 0;
	}

	public double readDouble() throws IOException {
		// TODO Auto-generated method stub
		readLong();
		return 0;
	}

	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

    public String readUTF() throws IOException {
        int bytes = readUnsignedShort();
        StringBuilder sb = new StringBuilder();

        while (bytes > 0) {
            bytes -= readUtfChar(sb);
        }

        return sb.toString();
    }

    private int readUtfChar(StringBuilder sb) throws IOException {
        int a = readUnsignedByte();
        if ((a & 0x80) == 0) {
          sb.append((char) a);
          return 1;
        }
        if ((a & 0xe0) == 0xb0) {
          int b = readUnsignedByte();
          sb.append((char)(((a& 0x1F) << 6) | (b & 0x3F)));
          return 2;
        }
        if ((a & 0xf0) == 0xe0) {
          int b = bytes[pos++];
          int c = readUnsignedByte();
          sb.append((char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F)));
          return 3;
        }
        throw new RuntimeException();
      }


	public void readFully(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes[pos++];
		}
	}

}
