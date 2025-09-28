package com.zkhm.exam.ai.workflow.definition;

import com.zkhm.exam.ai.workflow.dag.Node;
import java.util.*;

/**
 * 工作流定义：表示一个有向无环图（DAG）
 * 负责管理节点和依赖关系
 */
public class WorkflowDefinition {

    // 存储所有节点：id -> Node
    private final Map<String, Node> nodes = new LinkedHashMap<>();

    // 邻接表：记录每个节点的下游依赖者（用于触发通知）
    private final Map<String, Set<String>> dependentsMap = new HashMap<>();

    /**
     * 添加一个节点到工作流中
     */
    public void addNode(Node node) {
        if (node == null) throw new IllegalArgumentException("Node cannot be null");

        String nodeId = node.getId();
        if (nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException("Node already exists: " + nodeId);
        }

        nodes.put(nodeId, node);

        // 建立反向依赖：为每个依赖项注册本节点为其“被依赖者”
        for (String dependency : node.getDependencies()) {
            dependentsMap.computeIfAbsent(dependency, k -> new HashSet<>()).add(nodeId);
        }
    }

    /**
     * 获取所有节点（用于拓扑排序）
     */
    public Collection<Node> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    /**
     * 获取某个节点的直接下游节点（即依赖它的节点）
     */
    public Set<String> getDependents(String nodeId) {
        return dependentsMap.getOrDefault(nodeId, Set.of());
    }

    /**
     * 根据 ID 获取节点
     */
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * 获取节点总数
     */
    public int size() {
        return nodes.size();
    }

    /**
     * 检查是否包含某个节点
     */
    public boolean containsNode(String nodeId) {
        return nodes.containsKey(nodeId);
    }
}
