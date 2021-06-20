package eu.bebendorf.jtuntap.adapter;

import eu.bebendorf.linuxio.IOCTLRequest;
import eu.bebendorf.linuxio.LinuxIO;
import eu.bebendorf.linuxio.iface.InterfaceFlag;
import eu.bebendorf.linuxio.iface.InterfaceRequest;
import eu.bebendorf.linuxio.socket.AddressFamily;
import eu.bebendorf.linuxio.socket.SockAddrIn;
import eu.bebendorf.linuxio.socket.SocketType;
import lombok.Getter;

import java.io.*;

public class LinuxTunAdapter implements InterfaceAdapter {

    @Getter
    String name;
    final RandomAccessFile raf;
    final InputStream inputStream;
    final OutputStream outputStream;

    public LinuxTunAdapter(String name) throws IOException {
        this.name = name;
        this.raf = new RandomAccessFile("/dev/net/tun", "rw");
        this.inputStream = new FileInputStream(raf.getFD());
        this.outputStream = new FileOutputStream(raf.getFD());
        int fd = LinuxIO.getFileDescriptor(raf);
        InterfaceRequest request = new InterfaceRequest()
                .setName(name)
                .setFlags(InterfaceFlag.TUN | InterfaceFlag.NO_PI);
        int err = LinuxIO.ioctl(fd, IOCTLRequest.TUNSETIFF, request);
        if(err < 0) {
            raf.close();
            throw new IOException("ioctl(TUNSETIFF) failed with error code " + err);
        }
        this.name = request.getName();
    }

    public Type getType() {
        return Type.TUN;
    }

    public void setAddress(String address, String netmask, String broadcast) throws IOException {
        int fd = LinuxIO.socket(AddressFamily.INET, SocketType.DGRAM, 0);
        InterfaceRequest request = new InterfaceRequest()
                .setName(name)
                .setSockAddrIn(new SockAddrIn(address, 0));
        int err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCSIFADDR, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCSIFADDR) failed with error code " + err);
        }
        request = new InterfaceRequest()
                .setName(name)
                .setSockAddrIn(new SockAddrIn(netmask, 0));
        err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCSIFNETMASK, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCSIFNETMASK) failed with error code " + err);
        }
        request = new InterfaceRequest()
                .setName(name)
                .setSockAddrIn(new SockAddrIn(broadcast, 0));
        err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCSIFBRDADDR, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCSIFBRDADDR) failed with error code " + err);
        }
        LinuxIO.close(fd);
    }

    public void up() throws IOException {
        int fd = LinuxIO.socket(AddressFamily.INET, SocketType.DGRAM, 0);
        InterfaceRequest request = new InterfaceRequest()
                .setName(name);
        int err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCGIFFLAGS, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCGIFFLAGS) failed with error code " + err);
        }
        request.addFlags(InterfaceFlag.UP | InterfaceFlag.LOWER_UP | InterfaceFlag.RUNNING);
        request.removeFlags(InterfaceFlag.NOARP | InterfaceFlag.POINTOPOINT);
        err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCSIFFLAGS, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCSIFFLAGS) failed with error code " + err);
        }
        LinuxIO.close(fd);
    }

    public void down() throws IOException {
        int fd = LinuxIO.socket(AddressFamily.INET, SocketType.DGRAM, 0);
        InterfaceRequest request = new InterfaceRequest()
                .setName(name);
        int err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCGIFFLAGS, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCGIFFLAGS) failed with error code " + err);
        }
        request.addFlags(InterfaceFlag.NOARP);
        request.removeFlags(InterfaceFlag.LOWER_UP | InterfaceFlag.UP | InterfaceFlag.RUNNING);
        err = LinuxIO.ioctl(fd, IOCTLRequest.SIOCSIFFLAGS, request);
        if(err < 0) {
            LinuxIO.close(fd);
            raf.close();
            throw new IOException("ioctl(SIOCSIFFLAGS) failed with error code " + err);
        }
        LinuxIO.close(fd);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void close() throws IOException {
        raf.close();
    }

}
