package eu.bebendorf.jtuntap.protocol.layer4;

import eu.bebendorf.jtuntap.util.ChecksumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ICMPPacket {

    Type type;
    int code;
    int checksum;
    byte[] data;

    public ICMPPacket(byte[] packet) {
        this.type = Type.fromValue(packet[0] & 0xFF);
        this.code = packet[1] & 0xFF;
        this.checksum = ((packet[2] & 0xFF) << 8) | (packet[3] & 0xFF);
        this.data = new byte[packet.length - 4];
        System.arraycopy(packet, 4, this.data, 0, this.data.length);
    }

    public ICMPPacket(Type type, int code, byte[] data) {
        this.type = type;
        this.code = code;
        this.data = data;
        updateChecksum();
    }

    public ICMPPacket updateChecksum() {
        this.checksum = ChecksumUtil.icmpChecksum(type.value, code, 0, data);
        return this;
    }

    public byte[] build() {
        byte[] packet = new byte[4 + data.length];
        packet[0] = (byte) type.value;
        packet[1] = (byte) code;
        packet[2] = (byte) (checksum >> 8);
        packet[3] = (byte) (checksum & 0xFF);
        System.arraycopy(data, 0, packet, 4, data.length);
        return packet;
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        ECHO_REPLY(0),
        DESTINATION_UNREACHABLE(3),
        SOURCE_QUENCH(4),
        REDIRECT(5),
        ECHO(8),
        ROUTER_ADVERTISEMENT(9),
        ROUTER_SELECTION(10),
        TIME_EXCEEDED(11),
        PARAMETER_PROBLEM(12),
        TIMESTAMP(13),
        TIMESTAMP_REPLY(14),
        EXTENDED_ECHO(42),
        EXTENDED_ECHO_REPLY(43);
        final int value;

        public static Type fromValue(int value) {
            for(Type type : values()) {
                if(type.value == value)
                    return type;
            }
            return null;
        }

    }

}
