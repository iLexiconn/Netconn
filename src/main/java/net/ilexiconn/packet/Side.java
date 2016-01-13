package net.ilexiconn.packet;

public enum Side {
    CLIENT(true),
    SERVER(false);

    private boolean isRemote;

    Side(boolean isRemote) {
        this.isRemote = isRemote;
    }

    public boolean isClient() {
        return isRemote;
    }

    public boolean isServer() {
        return !isClient();
    }
}
