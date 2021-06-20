package eu.bebendorf.jtuntap.protocol.layer3;

import eu.bebendorf.jtuntap.protocol.Layer4Protocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPv6Packet extends IPPacket {

    int flowLabel;

    protected IPv6Packet(byte[] packet) {
        super((byte) 6);
        this.tos = ((packet[0] & 0xF) << 4) | ((packet[1] & 0xFF) >> 4);
        this.flowLabel = ((packet[1] & 0xF) << 16) | ((packet[2] & 0xFF) << 8) | (packet[3] & 0xFF);
        this.ttl = packet[7] & 0xFF;
        this.protocol = Layer4Protocol.fromValue(packet[6] & 0xFF);
        this.sourceAddress = new byte[16];
        System.arraycopy(packet, 8, this.sourceAddress, 0, 16);
        this.destinationAddress = new byte[16];
        System.arraycopy(packet, 24, this.destinationAddress, 0, 16);
        int len = (packet[4] << 8) | packet[5];
        this.payload = new byte[len];
        System.arraycopy(packet, 40, this.payload, 0, this.payload.length);
    }

    public byte[] build() {
        return new byte[0];
    }

    protected IPv6Packet(Layer4Protocol protocol) {
        super((byte) 6);
        this.ttl = 30;
        this.protocol = protocol;
        this.sourceAddress = new byte[16];
        this.destinationAddress = new byte[16];
    }

}
