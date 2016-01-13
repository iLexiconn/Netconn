package net.ilexiconn.packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    public static byte[] toByteArray(InputStream in) throws IOException {
        byte[] bytes = new byte[16384];
        DataInputStream dataIs = new DataInputStream(in);
        dataIs.readFully(bytes);

        return bytes;
    }
}
