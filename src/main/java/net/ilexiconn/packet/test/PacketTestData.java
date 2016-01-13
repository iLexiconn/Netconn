package net.ilexiconn.packet.test;

import net.ilexiconn.packet.ByteHelper;
import net.ilexiconn.packet.INetworkManager;
import net.ilexiconn.packet.IPacket;
import net.ilexiconn.packet.Side;

import java.net.Socket;

public class PacketTestData implements IPacket {
    private byte data;
    private int dataInt;
    private String dataString;

    public PacketTestData() {

    }

    public PacketTestData(byte data, int dataInt, String dataString) {
        this.data = data;
        this.dataInt = dataInt;
        this.dataString = dataString;
    }

    @Override
    public byte[] encode() {
        ByteHelper byteHelper = new ByteHelper();
        byteHelper.writeByte(data);
        byteHelper.writeInteger(dataInt);
        byteHelper.writeString(dataString);

        return byteHelper.toBytes();
    }

    @Override
    public void decode(byte[] data) {
        ByteHelper helper = new ByteHelper(data);
        this.data = helper.readByte();
        this.dataInt = helper.readInteger();
        this.dataString = helper.readString();
    }

    @Override
    public void handle(ByteHelper byteHelper, Side side, Socket from, INetworkManager networkManager) {
        System.out.println("Received data: " + data + ", " + dataInt + ", " + dataString + " on " + side + " side");

        if (side.isServer()) {
            networkManager.sendPacketToClient(this, from);
        }
    }
}
