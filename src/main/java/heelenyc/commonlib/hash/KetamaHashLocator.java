package heelenyc.commonlib.hash;

import heelenyc.commonlib.LogUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class KetamaHashLocator implements IHashLocator {
    private static Logger logger = Logger.getLogger(KetamaHashLocator.class);
    private static final int NUM_REPS = 160;

    private final HashAlgorithm hashAlg;
    private final Set<String> nodes = Collections.synchronizedSet(new HashSet<String>());
    private transient volatile TreeMap<Long, String> ketamaNodes = new TreeMap<Long, String>();

    public KetamaHashLocator(Collection<String> nodes) {
        this.hashAlg = HashAlgorithm.KETAMA_HASH;
        this.nodes.addAll(nodes);

        buildMap();
    }

    @Override
    public String getNodeByKey(String hashKey) {
        long hash = this.hashAlg.hash(hashKey);
        return getNodeByHash(hash);
    }

    @Override
    public synchronized void addNode(String node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            buildMap();

            LogUtils.info(logger, "KetamaHashLocator add node: {0}, nodes: {1}", node, nodes.toString());
        }
    }

    @Override
    public synchronized void removeNode(String node) {
        if (nodes.size() <= 1) {
            LogUtils.warn(logger, "Skip the remove operation because there is only one node left.");
            return;
        }

        if (nodes.contains(node)) {
            nodes.remove(node);
            buildMap();

            LogUtils.info(logger, "KetamaHashLocator remove node: {0}, nodes: {1}", node, nodes.toString());
        }
    }

    public synchronized void removeNodeIgnoreSize(String node) {

        if (nodes.contains(node)) {
            nodes.remove(node);
            buildMap();

            LogUtils.info(logger, "KetamaHashLocator remove node: {0}, nodes: {1}", node, nodes.toString());
        }
    }

    @Override
    public synchronized void updateNodes(List<String> nodes) {
        this.nodes.clear();
        this.nodes.addAll(nodes);
        buildMap();

        LogUtils.info(logger, "KetamaHashLocator update nodes: {0} -> {1}", this.nodes.toString(), nodes.toString());
    }

    private synchronized void buildMap() {
        LogUtils.info(logger, "KetamaHashLocator initializing, nodes: {0}", nodes.toString());

        TreeMap<Long, String> nodeMap = new TreeMap<Long, String>();
        for (String node : nodes) {
            /** Duplicate 160 X weight references */
            if (hashAlg == HashAlgorithm.KETAMA_HASH) {
                for (int i = 0; i < NUM_REPS / 4; i++) {
                    byte[] digest = HashAlgorithm.computeMd5(node.replace("_s1", "").replace("_s2", "") + "-" + i);
                    for (int h = 0; h < 4; h++) {
                        long key = (long) (digest[3 + h * 4] & 0xFF) << 24 | (long) (digest[2 + h * 4] & 0xFF) << 16 | (long) (digest[1 + h * 4] & 0xFF) << 8 | digest[h * 4] & 0xFF;
                        nodeMap.put(key, node);
                    }
                }
            } else {
                for (int i = 0; i < NUM_REPS; i++) {
                    long key = hashAlg.hash(node + "-" + i);
                    nodeMap.put(key, node);
                }
            }
        }

        LogUtils.warn(logger, "KetamaHashLocator nodeMap rebuild: {0} -> {1}", this.ketamaNodes.size(), nodeMap.size());

        this.ketamaNodes = nodeMap;
    }

    @Override
    public String toString() {
        return "KetamaHashLocator [nodes=" + nodes + "]";
    }

    private String getNodeByHash(long hash) {
        // in case doing buildMap again
        TreeMap<Long, String> nodeMap = this.ketamaNodes;
        if (nodeMap == null || nodeMap.size() == 0) {
            return null;
        }

        Long resultHash = hash;
        if (!nodeMap.containsKey(hash)) {
            // Java 1.6 adds a ceilingKey method, but xmemcached is compatible
            // with jdk5,So use tailMap method to do this.
            SortedMap<Long, String> tailMap = nodeMap.tailMap(hash);
            if (tailMap.isEmpty()) {
                resultHash = nodeMap.firstKey();
            } else {
                resultHash = tailMap.firstKey();
            }
        }
        return nodeMap.get(resultHash);
    }

    public static void main(String[] args) {
        // KetamaHashLocator k = new
        // KetamaHashLocator(CollectionsUtils.toList(new String[]{"1"}));
        //
        // System.out.println(k.getNodeByKey("123"));
        // k.removeNodeIgnoreSize("1");
        // System.out.println(k.getNodeByKey("123"));
    }

}
