package heelenyc.commonlib.hash;

import heelenyc.commonlib.LogUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public class ModLocator implements IHashLocator {
    private static Logger logger = Logger.getLogger(ModLocator.class);

    private List<String> allNodes; // 所有节点的列表，一旦设置不可以进行修改。用于记录每个节点的位置。
    private List<String> availableNodes; // 当前可用的节点列表。
    private final ReentrantLock lock = new ReentrantLock();

    public ModLocator(List<String> nodes) {
        this.allNodes = Collections.unmodifiableList(nodes);
        this.availableNodes = new CopyOnWriteArrayList<String>(nodes);
    }

    /**
     * <tt>(key % size)</tt> must be less than <tt>size</tt>.
     */
    @Override
    public String getNodeByKey(String hashKey) {
        long key = Math.abs(Long.valueOf(hashKey));

        if (availableNodes.size() == 0) {
            LogUtils.error(logger, "Get node error: All nodes are unavailable!");
            return null;
        }

        int index = (int) (key % availableNodes.size());
        return availableNodes.get(index);
    }

    /**
     * 添加节点。<br />
     * 尽量保证原始的List顺序。
     */
    @Override
    public void addNode(String node) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (allNodes.contains(node) && !availableNodes.contains(node)) {
                int index = allNodes.indexOf(node);
                if (index < availableNodes.size()) {
                    availableNodes.add(index, node);
                } else { // Avoid IndexOutOfBoundsException.
                    availableNodes.add(node);
                }

                LogUtils.warn(logger, "Add node: {0} into available nodes: {1} OK.", node, availableNodes);
                return;
            }
        } finally {
            lock.unlock();
        }

        LogUtils.warn(logger, "Add node: {0} into available nodes: {1} failed!", node, availableNodes);
    }

    @Override
    public void removeNode(String node) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (availableNodes.size() > 1) {
                LogUtils.warn(logger, "Skip the remove operation because there is only one node left.");
                return;
            }

            if (availableNodes.contains(node)) {
                availableNodes.remove(node);

                LogUtils.warn(logger, "Remove node: {0} from available nodes: {1} OK.", node, availableNodes);
                return;
            }
        } finally {
            lock.unlock();
        }

        LogUtils.warn(logger, "Remove node: {0} from available nodes: {1} failed!", node, availableNodes);
    }

    @Override
    public void updateNodes(List<String> nodes) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            allNodes.clear();
            allNodes = Collections.unmodifiableList(nodes);

            availableNodes.clear();
            availableNodes.addAll(nodes);

            LogUtils.warn(logger, "Update nodes: {0} -> {1}", this.allNodes, nodes);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return "ModLocator [availableNodes=" + availableNodes + "]";
    }

    @Override
    public void removeNode(String node, boolean noPing) {
        // TODO Auto-generated method stub
        
    }

}
