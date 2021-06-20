package eu.bebendorf.jtuntap.util;

public class ChecksumUtil {

    public static int checksum16Bit1Complement(byte[] data) {
        int checksum = 0;
        for(int i=0; i < data.length; i+=2)
            checksum += ((data[i] & 0xFF) << 8) | (data[i+1] & 0xFF);
        if(data.length % 2 != 0)
            checksum += (data[data.length-1] & 0xFF) << 8;
        checksum = (checksum >> 16 & 0xFFFF) + (checksum & 0xFFFF);
        checksum = (checksum >> 16 & 0xFFFF) + (checksum & 0xFFFF);
        return (~checksum) & 0xFFFF;
    }

    public static int icmpChecksum(int type, int code, int checksum, byte[] data) {
        byte[] newData = new byte[data.length + 4];
        newData[0] = (byte) type;
        newData[1] = (byte) code;
        newData[2] = (byte) (checksum >> 8);
        newData[3] = (byte) (checksum & 0xFF);
        System.arraycopy(data, 0, newData, 4, data.length);
        return checksum16Bit1Complement(newData);
    }

}
