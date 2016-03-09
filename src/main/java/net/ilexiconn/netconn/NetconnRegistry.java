package net.ilexiconn.netconn;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class NetconnRegistry {
    private static Map<Integer, Class<? extends IPacket>> packetHandlerMap = new HashMap<Integer, Class<? extends IPacket>>();

    public static void registerPacket(int id, Class<? extends IPacket> packet) {
        packetHandlerMap.put(id, packet);
    }

    public static IPacket constructFromID(int id) {
        try {
            return packetHandlerMap.get(id).newInstance();
        } catch (Exception e) {
            System.err.println("Failed to initiate packet with ID " + id);
            e.printStackTrace();
            return null;
        }
    }

    public static int getIDFromBytes(byte[] data) {
        return ByteBuffer.wrap(new byte[]{data[0], data[1], data[2], data[3]}).getInt();
    }

    public static int getIDFromPacket(Class<? extends IPacket> packet) {
        for (Map.Entry<Integer, Class<? extends IPacket>> entry : packetHandlerMap.entrySet()) {
            if (entry.getValue().equals(packet)) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
