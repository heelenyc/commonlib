package heelenyc.commonlib;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class IpUtils {
    private static Logger logger = Logger.getLogger(IpUtils.class);

    public static final String IP_LAN = getIpLAN();
    
    public static final String IP_WAN = getIpWAN();

    public static final String HOST_NAME;

    static {
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            logger.error(e);
        }
        HOST_NAME = hostname;
    }

    /**
     * support multi network-card, useful in linux env
     */
    private static String getIpLAN() {
        try {
            Enumeration<NetworkInterface> netInterfaces = null;
            netInterfaces = NetworkInterface.getNetworkInterfaces();  
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();  
                Enumeration<InetAddress> ips = ni.getInetAddresses();  
                while (ips.hasMoreElements()) {  
                    String ip = ips.nextElement().getHostAddress();
                    if (ip.startsWith("192.168.1.") || ip.startsWith("127.")) 
                        return ip;
                }  
            }
        } catch (Exception e) {
            LogUtils.error(logger, e, "getIpLAN error!");
        }
        return "127.0.0.1";
    }

    /**
     * support multi network-card, useful in linux env
     */
    private static String getIpWAN() {
        try {
            Enumeration<NetworkInterface> netInterfaces = null;
            netInterfaces = NetworkInterface.getNetworkInterfaces();  
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();  
                Enumeration<InetAddress> ips = ni.getInetAddresses();  
                while (ips.hasMoreElements()) {  
                    String ip = ips.nextElement().getHostAddress();
                    if (ip.startsWith("127."))
                        return ip;
                }  
            }
        } catch (Exception e) {
            LogUtils.error(logger, e, "getIpWAN error!");
        }
        return "127.0.0.1";
    }
    
    public static void main(String[] args) {
        System.out.println(getIpLAN());

    }

}
