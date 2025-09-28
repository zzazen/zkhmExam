package com.zkhm.exam.ai.workflow.engine;

import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import com.zkhm.exam.ai.workflow.dag.Node;
import com.zkhm.exam.ai.workflow.definition.WorkflowDefinition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * @program: zkhmExam
 * @description: Workflow执行引擎
 * @author: Mr.Zhang
 * @create: 2025-09-27 12:56
 **/

@Component
public class WorkflowEngine {

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public WorkflowContext execute(WorkflowDefinition workflowDef, WorkflowContext context) {
        // 执行前检测循环依赖
        if (!isAcyclic(workflowDef)) {
            throw new IllegalArgumentException("Workflow contains cycle, not a DAG");
        }
        Set<String> completed = ConcurrentHashMap.newKeySet();
        int nodeCount = workflowDef.size();

        try {
            while (completed.size() < nodeCount) {
                List<Future<String>> batch = new ArrayList<>();

                for (Node node : workflowDef.getNodes()) {
                    String id = node.getId();
                    if (completed.contains(id)) continue;

                    // 检查依赖和条件
                    if (canExecute(node, completed) ) {
                        Callable<String> task = () -> {
                            try {
                                node.getTask(context).call(); // 执行实际逻辑
                                return id;
                            } catch (Exception e) {
                                throw new RuntimeException("Task failed: " + id, e);
                            }
                        };
                        batch.add(executor.submit(task));
                    }
                }

                if (batch.isEmpty()) {
                    throw new IllegalStateException("Deadlock or cycle detected");
                }

                // 等待本批次完成（可加超时）
                for (Future<String> future : batch) {
                    String finishedNodeId = future.get(120, TimeUnit.SECONDS);
                    completed.add(finishedNodeId); // 主线程确认完成
                }
            }
        } catch (Exception e) {
            context.put("workflow_error", "程序异常");
            throw new RuntimeException("Workflow execution failed because :", e);
        }
        return context;
    }

    private boolean canExecute(Node node, Set<String> completed) {
        return node.getDependencies().stream().allMatch(completed::contains);
    }

    // 使用拓扑排序思想检测是否有环
    private boolean isAcyclic(WorkflowDefinition workflowDef) {
        Map<String, Integer> inDegree = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();

        // 构建邻接表和入度
        for (Node node : workflowDef.getNodes()) {
            String id = node.getId();
            inDegree.putIfAbsent(id, 0);
            graph.putIfAbsent(id, new ArrayList<>());

            for (String dep : node.getDependencies()) {
                graph.computeIfAbsent(dep, k -> new ArrayList<>()).add(id);
                inDegree.merge(id, 1, Integer::sum);
            }
        }

        // 拓扑排序：BFS
        Queue<String> queue = new LinkedList<>();
        inDegree.forEach((k, v) -> {
            if (v == 0) queue.offer(k);
        });

        int visited = 0;
        while (!queue.isEmpty()) {
            String node = queue.poll();
            visited++;

            for (String neighbor : graph.getOrDefault(node, List.of())) {
                inDegree.merge(neighbor, -1, Integer::sum);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return visited == workflowDef.size(); // 所有节点都被访问 → 无环
    }
}