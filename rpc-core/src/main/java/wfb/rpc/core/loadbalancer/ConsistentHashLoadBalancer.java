package wfb.rpc.core.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

// 一致性hash算法
public class ConsistentHashLoadBalancer implements LoadBalancer {
    //使用TreeMap定义一个哈希环
    private final TreeMap<Integer, Instance> hashCircle = new TreeMap<>();

    public ConsistentHashLoadBalancer(List<Instance> instances) {
        for (Instance instance : instances) {
            int hash = hash(instance.getIp());
            hashCircle.put(hash, instance);
        }
    }

    public Instance select(List<Instance> instances) {
        return select(instances,"127.0.0.1");
    }

    //一致性hash算法的核心实现
    @Override
    public Instance select(List<Instance> instances, String clientIp) {
        if (hashCircle.isEmpty()) {
            return null;
        }
        // 计算客户端IP值的哈希
        int hash = hash(clientIp);
        // 如果没有找到匹配的节点，则搜索哈希环中的下一个节点
        if (!hashCircle.containsKey(hash)) {
            SortedMap<Integer, Instance> tailMap = hashCircle.tailMap(hash);
            hash = tailMap.isEmpty() ? hashCircle.firstKey() : tailMap.firstKey();
        }
        return hashCircle.get(hash);
    }

    // 使用简单的哈希函数作为示例
    private int hash(String key) {
        return key.hashCode();
    }

}

