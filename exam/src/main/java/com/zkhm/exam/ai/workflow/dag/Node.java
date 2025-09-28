package com.zkhm.exam.ai.workflow.dag;

import com.zkhm.exam.ai.workflow.content.WorkflowContext;

import java.util.Set;
import java.util.concurrent.Callable;

public interface Node {
    String getId();
    Set<String> getDependencies();
    Callable<Void> getTask(WorkflowContext context);
}
