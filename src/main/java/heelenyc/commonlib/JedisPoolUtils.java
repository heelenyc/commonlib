package heelenyc.commonlib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author mixueqiang yangjianjun
 * @since Oct 18, 2012
 * 
 */
public final class JedisPoolUtils {
    private static final Logger logger = Logger.getLogger(JedisPoolUtils.class);

    private static final int SOCK_FAIL_RETRY_NUM = 3;

    public static final JedisPoolConfig CONFIG_LARGE = new JedisPoolConfig();
    public static final JedisPoolConfig CONFIG_MEDIUM = new JedisPoolConfig();
    public static final JedisPoolConfig CONFIG_SMALL = new JedisPoolConfig();

    static {
        CONFIG_LARGE.setMaxTotal(2000);
        CONFIG_LARGE.setMaxIdle(200);
        CONFIG_LARGE.setMaxWaitMillis(2000);
        CONFIG_LARGE.setTestOnBorrow(true);

        CONFIG_MEDIUM.setMaxTotal(100);
        CONFIG_MEDIUM.setMaxIdle(50);
        CONFIG_MEDIUM.setMaxWaitMillis(2000);
        CONFIG_MEDIUM.setTestOnBorrow(true);

        CONFIG_SMALL.setMaxTotal(5);
        CONFIG_SMALL.setMaxIdle(5);
        CONFIG_SMALL.setMaxWaitMillis(2000);
        CONFIG_SMALL.setTestOnBorrow(true);
    }

    public static synchronized JedisPool getJedisPool(String hostPort, JedisPoolConfig poolConfig) {
        String host = StringUtils.substringBefore(hostPort, ":");
        int port = Integer.parseInt(StringUtils.substringAfter(hostPort, ":"));

        int i = 0;
        while (i < SOCK_FAIL_RETRY_NUM) {
            Socket socket = null;
            try {
                socket = new Socket();
                SocketAddress endpoint = new InetSocketAddress(host, port);
                socket.connect(endpoint, 1000);
                logger.info("Connected to redis [" + hostPort + "] OK.");
                return new JedisPool(poolConfig, host, port);
            } catch (Throwable t) {
                i++;
                logger.info("fail to Connected to redis [" + hostPort + "] the try num is:" + i);
            } finally {
                if (socket != null)
                    try {
                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return null;
    }

    private JedisPoolUtils() {
    }

}
