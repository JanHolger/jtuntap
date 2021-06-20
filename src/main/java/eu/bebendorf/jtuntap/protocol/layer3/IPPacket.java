package eu.bebendorf.jtuntap.protocol.layer3;

import eu.bebendorf.jtuntap.protocol.Layer4Protocol;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class IPPacket {

    byte version;
    int tos;
    Layer4Protocol protocol;
    int ttl;
    byte[] sourceAddress;
    byte[] destinationAddress;
    byte[] payload;

    protected IPPacket(byte version) {
        this.version = version;
    }

    public abstract byte[] build();

    public String getSourceAddressString() {
        try {
            return InetAddress.getByAddress(sourceAddress).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public String getDestinationAddressString() {
        try {
            return InetAddress.getByAddress(destinationAddress).getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static IPPacket from(byte[] data) {
        byte version = (byte) (data[0] >> 4);
        switch (version) {
            case 4:
                return new IPv4Packet(data);
            case 6:
                return new IPv6Packet(data);
        }
        return null;
    }

}
