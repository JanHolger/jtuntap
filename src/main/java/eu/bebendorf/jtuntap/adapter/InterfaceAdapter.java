package eu.bebendorf.jtuntap.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface InterfaceAdapter {

    Type getType();
    String getName();
    void setAddress(String address, String netmask, String broadcast) throws IOException;
    void up() throws IOException;
    void down() throws IOException;
    InputStream getInputStream();
    OutputStream getOutputStream();
    void close() throws IOException;

    enum Type {
        TUN,
        TAP
    }

}
