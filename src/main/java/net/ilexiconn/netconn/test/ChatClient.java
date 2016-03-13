package net.ilexiconn.netconn.test;

import net.ilexiconn.netconn.NetconnRegistry;
import net.ilexiconn.netconn.client.Client;
import net.ilexiconn.netconn.client.IClientListener;

import java.io.IOException;
import java.net.Socket;
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

            client.addListener(new IClientListener() {
                @Override
                public void onConnected(Client client, Socket server) {

                }

                @Override
                public void onDisconnected(Client client) {
                    System.out.println("Disconnected!");
                }
            });

            System.out.println("Connected to server " + host + ":" + port);

            Thread clientListen = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (client.isRunning()) {
                        client.listen();
                    }
                }
            });

            clientListen.start();

            Scanner in = new Scanner(System.in);

            while (in.hasNextLine()) {
                String message = in.nextLine();
                if (message.equals("/disconnect")) {
                    client.disconnect();
                    System.gc();
                    System.exit(0);
                }
                client.sendPacketToServer(new PacketSendMessage(username, message));
            }
        } else {
            System.err.println("Please specify a username, host and port in the args!");
        }
    }
}
