package heelenyc.commonlib.hash;

import java.util.List;

public interface IHashLocator {

    /**
     * 根据HashKey获取节点。
     */
    String getNodeByKey(String hashKey);

    /**
     * 添加节点。
     */
    void addNode(String node);

    /**
     * 移除节点。
     */
    void removeNode(String node);

    /**
     * 更新节点列表。
     */
    void updateNodes(List<String> nodes);

}
