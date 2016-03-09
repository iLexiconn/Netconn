package net.ilexiconn.netconn.test;

import net.ilexiconn.netconn.NetconnRegistry;
import net.ilexiconn.netconn.client.Client;

import java.io.IOException;
import java.util.Scanner;

public class ChatClient {
    public static String username;
    public static String host;
    public static int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 2) {
            username = args[0];
            host = args[1];
            port = Integer.parseInt(args[2]);

            NetconnRegistry.registerPacket(0, PacketSendMessage.class);

            final Client client = new Client(host, port);

            System.out.println("Connected to server " + host + ":" + port);

            Thread clientListen = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        client.listen();
                    }
                }
            });

            clientListen.start();

            Scanner in = new Scanner(System.in);

            while (in.hasNextLine()) {
                client.sendPacketToServer(new PacketSendMessage(username, in.nextLine()));
            }
        } else {
            System.err.println("Please specify a username, host and port in the args!");
        }
    }
}