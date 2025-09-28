package com.zkhm.exam.ai.workflow.condition.impl;

import com.zkhm.exam.ai.workflow.condition.Condition;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;

/**
 * @program: zkhmExam
 * @description: And
 * @author: Mr.Zhang
 * @create: 2025-09-27 12:51
 **/

public class AndCondition implements Condition {
    private final Condition[] conditions;
    public AndCondition(Condition... conditions) {
        this.conditions = conditions;
    }
    @Override
    public boolean evaluate(WorkflowContext context) {
        if (conditions == null || conditions.length == 0) {
            throw new IllegalArgumentException("Conditions cannot be null or empty");
        }
        for (Condition c : conditions) {
            if (!c.evaluate(context)) return false;
        }
        return true;
    }
}
