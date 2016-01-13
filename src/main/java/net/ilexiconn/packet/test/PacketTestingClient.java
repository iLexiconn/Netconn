package net.ilexiconn.packet.test;

import net.ilexiconn.packet.NetworkRegistry;
import net.ilexiconn.packet.client.Client;

import java.io.IOException;

public class PacketTestingClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        NetworkRegistry.registerPacket(EnumTest.TEST);

        final Client client = new Client("localhost", 25565);

        Thread clientListen = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    client.listen();
                }
            }
        });

        clientListen.start();

        Thread.sleep(2000);

        client.sendPacketToServer(new PacketTestData((byte) 4, 54352, "Hello World"));
    }
}
