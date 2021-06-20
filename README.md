# jtuntap
TUN/TAP adapter library for Java

## Usage
```java
TunTapAdapter adapter = new TunTapAdapter(Type.TUN, "test"); // name is optional
adapter.setAddress("10.16.0.1", "255.255.0.0", "10.16.255.255");
adapter.up();
InputStream in = adapter.getInputStream();
OutputStream out = adapter.getOutputStream();
IPPacket packet;
byte[] buffer = new byte[1500];
while(true) {
    in.read(buffer);
    packet = IPPacket.from(buffer);
    System.out.println(packet.getProtocol().name() + " packet to " + packet.getDestinationAddressString());
}
```

## OS Support
&nbsp; | Linux | OSX   | Windows
------ | ----- | ----- | -------
TUN    | Yes   | No    | No
TAP    | No    | No    | No

## Native Libraries
This library depends on [linuxio](https://github.com/JanHolger/linuxio) for native io operations.