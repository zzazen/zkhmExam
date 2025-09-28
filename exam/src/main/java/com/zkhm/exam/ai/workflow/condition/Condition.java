package com.zkhm.exam.ai.workflow.condition;

import com.zkhm.exam.ai.workflow.content.WorkflowContext;

@FunctionalInterface
public interface Condition {
    boolean evaluate(WorkflowContext context);
}
