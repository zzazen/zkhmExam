package com.zkhm.exam.ai.workflow.condition.impl;

import com.zkhm.exam.ai.workflow.condition.Condition;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;

/**
 * @program: zkhmExam
 * @description: Not
 * @author: Mr.Zhang
 * @create: 2025-09-27 12:52
 **/

public class NotCondition implements Condition {
    private final Condition condition;
    public NotCondition(Condition condition) {
        this.condition = condition;
    }
    @Override
    public boolean evaluate(WorkflowContext context) {
        return !condition.evaluate(context);
    }
}
