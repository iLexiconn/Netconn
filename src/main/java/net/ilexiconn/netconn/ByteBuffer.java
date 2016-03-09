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

    public void writeShort(short s) {
        writeBytes(java.nio.ByteBuffer.allocate(2).putShort(s).array());
    }

    public void writeDouble(double d) {
        writeBytes(java.nio.ByteBuffer.allocate(8).putDouble(d).array());
    }

    public void writeFloat(float f) {
        writeBytes(java.nio.ByteBuffer.allocate(4).putFloat(f).array());
    }

    public void writeLong(long l) {
        writeBytes(java.nio.ByteBuffer.allocate(8).putLong(l).array());
    }

    public void writeStringByte(String s) {
        for (char c : s.toCharArray()) {
            writeByte((byte) c);
        }

        writeByte((byte) 0);
    }

    public void writeStringShort(String s) {
        for (char c : s.toCharArray()) {
            writeShort((short) c);
        }

        writeShort((short) 0);
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

    public short readShort() {
        return java.nio.ByteBuffer.wrap(readBytes(2)).getShort();
    }

    public double readDouble() {
        return java.nio.ByteBuffer.wrap(readBytes(8)).getDouble();
    }

    public float readFloat() {
        return java.nio.ByteBuffer.wrap(readBytes(4)).getFloat();
    }

    public long readLong() {
        return java.nio.ByteBuffer.wrap(readBytes(8)).getLong();
    }

    public String readStringByte() {
        byte c;
        String str = "";
        while ((c = readByte()) != 0) {
            str += (char) c;
        }
        return str;
    }

    public String readStringShort() {
        short c;
        String str = "";
        while ((c = readShort()) != 0) {
            str += (char) c;
        }
        return str;
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
