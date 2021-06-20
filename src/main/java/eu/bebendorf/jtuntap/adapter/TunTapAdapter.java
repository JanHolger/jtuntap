package eu.bebendorf.jtuntap.adapter;

import lombok.experimental.Delegate;

import java.io.IOException;
import java.util.Locale;

public class TunTapAdapter implements InterfaceAdapter {

    @Delegate
    final InterfaceAdapter parent;

    public TunTapAdapter(Type type, String name) throws IOException {
        String os = System.getProperty("os.name");
        switch (os.toLowerCase(Locale.ROOT)) {
            case "linux": {
                if(type == Type.TUN) {
                    parent = new LinuxTunAdapter(name);
                    return;
                }
                break;
            }
        }
        throw new UnsupportedOperationException(type.name() + "-Adapters are not supported on " + os);
    }

}
