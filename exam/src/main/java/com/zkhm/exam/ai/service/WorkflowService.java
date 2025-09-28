package com.zkhm.exam.ai.service;

import com.zkhm.exam.ai.agents.Agent1;
import com.zkhm.exam.ai.agents.Agent2;
import com.zkhm.exam.ai.agents.Agent3;
import com.zkhm.exam.ai.workflow.condition.Condition;
import com.zkhm.exam.ai.workflow.condition.impl.AndCondition;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import com.zkhm.exam.ai.workflow.dag.Node;
import com.zkhm.exam.ai.workflow.dag.impl.SimpleNode;
import com.zkhm.exam.ai.workflow.definition.WorkflowDefinition;
import com.zkhm.exam.ai.workflow.engine.WorkflowEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @program: zkhmExam
 * @description: workflow服务
 * @author: Mr.Zhang
 * @create: 2025-09-27 16:37
 **/

@Service
@Slf4j
public class WorkflowService {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private Agent1 agent1;
    @Autowired
    private Agent2 agent2;
    @Autowired
    private Agent3 agent3;

    /**
     * 构建 DAG 工作流：Agent1 AND Agent2 → Agent3
     */
    public WorkflowContext executeAgentWorkflow(String userQuery) {
        // 创建上下文
        WorkflowContext context = new WorkflowContext();
        context.put("user_query", userQuery);

        // 创建工作流定义（DAG 容器）
        WorkflowDefinition workflow = new WorkflowDefinition();

        // Step 1: 创建条件 —— Agent1 和 Agent2 都完成
        Condition agent1Completed = context1 -> context1.contains("agent1_result");
        Condition agent2Completed = context2 -> context2.contains("agent2_result");
        Condition bothCompleted = new AndCondition(agent1Completed, agent2Completed);

        // Step 2: 创建节点
        Node node1 = new SimpleNode(
                "agent1",
                Set.of(), // 无依赖
                ctx -> true, // 总是执行
                () -> {
                    agent1.setContext(context);
                    agent1.call();
                    log.info("agent1被调用");
                    return null;
                }
        );

        Node node2 = new SimpleNode(
                "agent2",
                Set.of(), // 无依赖
                ctx -> true,
                () -> {
                    agent2.setContext(context);
                    agent2.call();
                    log.info("agent2被调用");
                    return null;
                }
        );

        Node node3 = new SimpleNode(
                "agent3",
                Set.of("agent1", "agent2"), // 依赖 node1 和 node2
                bothCompleted, // 条件：两者都完成
                () -> {
                    agent3.setContext(context);
                    agent3.call();
                    log.info("agent3被调用");
                    return null;
                }
        );

        // Step 3: 构建图（这里可以封装成 WorkflowDefinition）
        workflow.addNode(node1);
        workflow.addNode(node2);
        workflow.addNode(node3);

        // Step 4: 执行工作流
        return workflowEngine.execute(workflow, context);
    }
}
