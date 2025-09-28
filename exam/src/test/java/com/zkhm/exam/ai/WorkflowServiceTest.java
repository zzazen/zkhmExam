package com.zkhm.exam.ai;

import com.zkhm.exam.ai.agents.Agent1;
import com.zkhm.exam.ai.agents.Agent2;
import com.zkhm.exam.ai.agents.Agent3;
import com.zkhm.exam.ai.service.WorkflowService;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import com.zkhm.exam.ai.workflow.definition.WorkflowDefinition;
import com.zkhm.exam.ai.workflow.engine.WorkflowEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: zkhmExam
 * @description:
 * @author: Mr.Zhang
 * @create: 2025-09-27 18:16
 **/

@SpringBootTest
class WorkflowServiceTest {
    @Autowired
    private WorkflowService workflowService;

    @Test
    void testExecuteAgentWorkflow() {
        String query = "办公室印刷机死机怎么办?";
        WorkflowContext workflowContext = workflowService.executeAgentWorkflow(query);
        String s = workflowContext.get("final_result", String.class);
        System.out.println(s);

    }



}
