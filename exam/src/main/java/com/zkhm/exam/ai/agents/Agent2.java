package com.zkhm.exam.ai.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @program: zkhmExam
 * @description: Agent2
 * @author: Mr.Zhang
 * @create: 2025-09-27 13:02
 **/

@Component
@Slf4j
public class Agent2 implements Callable<Void> {
    @Autowired
    private AddBGAgent addBGAgent;

    private WorkflowContext context;

    private static final ObjectMapper mapper = new ObjectMapper();

    public Agent2 setContext(WorkflowContext context) {
        this.context = context;
        return this;
    }

    @Override
    public Void call() {
        String query = context.get("user_query", String.class);
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("user_query is required");
        }
        Response<AiMessage> response = addBGAgent.addBG(query);
        String jsonText = response.content().text().trim();
        // 使用 Jackson 或 Gson 解析 JSON
        Map<String, Object> result = null;
        try {
            result = mapper.readValue(jsonText, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + jsonText, e);
        }
        context.put("agent2_result", result);
        log.info("Agent2 result: {}", result);
        return null;
    }
}


















