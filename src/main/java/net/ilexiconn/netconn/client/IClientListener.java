package net.ilexiconn.netconn.client;

import java.net.Socket;

public interface IClientListener {
    void onConnected(Client client, Socket server);

    void onDisconnected(Client client);
}
