package heelenyc.commonlib;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HostPort {

    private String host;
    private int port;

    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Ping host:port to check its alive.
     */
    public void ping() throws Exception {
        Socket s = null;
        try {
            // s = new Socket(host, port);
            s = new Socket();
            SocketAddress endpoint = new InetSocketAddress(host, port);
            s.connect(endpoint, 1000); // timeout 1000ms
        } catch (Exception e) {
            throw e;
        } finally {
            if (s != null)
                s.close();
        }
    }

    /**
     * @param hostport
     *            192.168.1.1:6359
     */
    public static HostPort parse(String hostport) {
        return new HostPort(hostport.split(":")[0], Integer.valueOf(hostport.split(":")[1]));
    }

    /**
     * @param hostports
     *            [192.168.1.1:6359, 192.168.1.2:6359]
     */
    public static List<HostPort> parse(Collection<String> hostports) {
        List<HostPort> list = new ArrayList<HostPort>();
        for (String hostport : hostports) {
            list.add(parse(hostport));
        }
        return list;
    }

    public static List<String> toString(Collection<HostPort> hostports) {
        List<String> list = new ArrayList<String>();
        if (hostports != null) {
            for (HostPort hostport : hostports) {
                list.add(hostport.toString());
            }
        }
        return list;
    }

    /**
     * Do not modify it!
     */
    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HostPort other = (HostPort) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

}
