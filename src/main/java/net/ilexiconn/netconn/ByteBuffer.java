package net.ilexiconn.netconn;

public class ByteBuffer {
    private byte[] bytes;
    private int index;

    public ByteBuffer() {
        this.bytes = new byte[16384];
        resetIndex();
    }

    public ByteBuffer(byte[] bytes) {
        this.bytes = bytes;
        resetIndex();
    }

    public void writeByte(byte b) {
        bytes[index] = b;
        index++;
    }

    public void writeBytes(byte[] b) {
        for (byte by : b) {
            writeByte(by);
        }
    }

    public void writeInteger(int i) {
        writeBytes(java.nio.ByteBuffer.allocate(4).putInt(i).array());
    }

    public void writeString(String s) {
        byte[] bytes = s.getBytes();

        writeInteger(bytes.length);
        writeBytes(bytes);
    }

    public byte readByte() {
        byte b = bytes[index];
        index++;
        return b;
    }

    public byte[] readBytes(int count) {
        byte[] bytes = new byte[count];

        for (int i = 0; i < count; i++) {
            bytes[i] = readByte();
        }

        return bytes;
    }

    public int readInteger() {
        return java.nio.ByteBuffer.wrap(readBytes(4)).getInt();
    }

    public String readString() {
        int length = readInteger();
        return new String(readBytes(length));
    }

    public void resetIndex() {
        index = 4; //First 4 bytes are id
    }

    public void incrementIndex(int amount) {
        index += amount;
    }

    public byte[] toBytes() {
        return bytes;
    }
}