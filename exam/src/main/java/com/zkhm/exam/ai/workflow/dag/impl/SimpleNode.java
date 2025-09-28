package com.zkhm.exam.ai.workflow.dag.impl;

import com.zkhm.exam.ai.workflow.condition.Condition;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import com.zkhm.exam.ai.workflow.dag.Node;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @program: zkhmExam
 * @description: 具体节点实现
 * @author: Mr.Zhang
 * @create: 2025-09-27 12:55
 **/

public class SimpleNode implements Node {
    private final String id;
    private final Set<String> dependencies;
    private final Condition enableCondition;
    private final Callable<Void> task;

    public SimpleNode(String id, Set<String> dependencies, Condition enableCondition, Callable<Void> task) {
        this.id = id;
        this.dependencies = dependencies;
        this.enableCondition = enableCondition;
        this.task = task;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public Callable<Void> getTask(WorkflowContext context) {
        return enableCondition.evaluate(context) ? task : () -> null;
    }
}
