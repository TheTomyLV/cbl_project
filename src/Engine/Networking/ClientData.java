package Engine.Networking;

import java.net.InetAddress;
import java.util.UUID;

import Engine.GameObject;

public class ClientData {
    private final InetAddress address;
    private final int port;
    private final UUID clientId;

    ClientData(InetAddress address, int port, UUID clientId) {
        this.clientId = clientId;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        return clientId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ClientData that = (ClientData) obj;
        return clientId.equals(that.clientId);
    }

}
