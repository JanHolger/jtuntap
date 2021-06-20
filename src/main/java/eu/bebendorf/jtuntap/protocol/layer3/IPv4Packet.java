package eu.bebendorf.jtuntap.protocol.layer3;

import eu.bebendorf.jtuntap.protocol.Layer4Protocol;
import eu.bebendorf.jtuntap.util.ChecksumUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IPv4Packet extends IPPacket {

    int identification;
    boolean dfFlag;
    boolean mfFlag;
    int fragmentOffset;
    int headerChecksum;
    byte[] options;

    public IPv4Packet(byte[] packet) {
        super((byte) 4);
        byte ihl = (byte) (packet[0] & 0xF);
        int totalLength = ((packet[2] & 0xFF) << 8) | (packet[3] & 0xFF);
        this.identification = ((packet[4] & 0xFF) << 8) | (packet[5] & 0xFF);
        this.dfFlag = (packet[6] & 0b01000000) > 0;
        this.mfFlag = (packet[6] & 0b00100000) > 0;
        this.fragmentOffset = ((packet[6] & 0b00011111) << 8) | (packet[7] & 0xFF);
        this.headerChecksum = ((packet[10] & 0xFF) << 8) | (packet[11] & 0xFF);
        this.tos = packet[1] & 0xFF;
        this.ttl = packet[8] & 0xFF;
        this.protocol = Layer4Protocol.fromValue(packet[9] & 0xFF);
        this.sourceAddress = new byte[4];
        System.arraycopy(packet, 12, this.sourceAddress, 0, 4);
        this.destinationAddress = new byte[4];
        System.arraycopy(packet, 16, this.destinationAddress, 0, 4);
        this.options = new byte[(ihl * 4) - 20];
        System.arraycopy(packet, 20, this.options, 0, this.options.length);
        this.payload = new byte[totalLength - (ihl * 4)];
        System.arraycopy(packet, ihl * 4, this.payload, 0, this.payload.length);
    }

    public IPv4Packet(Layer4Protocol protocol) {
        super((byte) 4);
        this.ttl = 30;
        this.protocol = protocol;
        this.sourceAddress = new byte[4];
        this.destinationAddress = new byte[4];
        this.payload = new byte[0];
        this.options = new byte[0];
        this.dfFlag = true;
    }

    public byte[] buildHeader() {
        int totalLength = payload.length + options.length + 20;
        byte[] header = new byte[options.length + 20];
        int ihl = 5 + (options.length / 4);
        header[0] = 0x40;
        header[0] |= ihl;
        header[1] = (byte) tos;
        header[2] = (byte) (totalLength >> 8);
        header[3] = (byte) (totalLength & 0xFF);
        header[4] = (byte) (identification >> 8);
        header[5] = (byte) (identification & 0xFF);
        header[6] = (byte) (fragmentOffset & 0x1F);
        if(dfFlag)
            header[6] |= 1 << 6;
        if(mfFlag)
            header[6] |= 1 << 5;
        header[7] = (byte) (fragmentOffset & 0xFF);
        header[8] = (byte) ttl;
        header[9] = (byte) protocol.getValue();
        header[10] = (byte) (headerChecksum >> 8);
        header[11] = (byte) (headerChecksum & 0xFF);
        System.arraycopy(sourceAddress, 0, header, 12, 4);
        System.arraycopy(destinationAddress, 0, header, 16, 4);
        System.arraycopy(options, 0, header, 20, options.length);
        return header;
    }

    public IPv4Packet setSourceAddress(byte[] address) {
        super.setSourceAddress(address);
        return this;
    }

    public IPv4Packet setDestinationAddress(byte[] address) {
        super.setDestinationAddress(address);
        return this;
    }

    public IPv4Packet setPayload(byte[] address) {
        super.setPayload(address);
        return this;
    }

    public IPv4Packet setTtl(int ttl) {
        super.setTtl(ttl);
        return this;
    }

    public IPv4Packet updateChecksum() {
        this.headerChecksum = 0;
        this.headerChecksum = ChecksumUtil.checksum16Bit1Complement(buildHeader());
        return this;
    }

    public byte[] build() {
        byte[] header = buildHeader();
        byte[] packet = new byte[header.length + payload.length];
        System.arraycopy(header, 0, packet, 0, header.length);
        System.arraycopy(payload, 0, packet, 20 + options.length, payload.length);
        return packet;
    }

}
