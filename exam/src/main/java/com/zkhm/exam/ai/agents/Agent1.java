package com.zkhm.exam.ai.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: zkhmExam
 * @description: Agent1
 * @author: Mr.Zhang
 * @create: 2025-09-27 13:02
 **/

@Component
@Slf4j
public class Agent1 implements Callable<Void> {
    @Autowired
    private SearchAgent searchAgent;

    private static final ObjectMapper mapper = new ObjectMapper();

    private WorkflowContext context;

    public Agent1 setContext(WorkflowContext context) {
        this.context = context;
        return this;
    }

    @Override
    public Void call() {
        String query = context.get("user_query", String.class);
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("user_query is required");
        }
        Response<AiMessage> response = searchAgent.search(query);
        String jsonText = response.content().text().trim();
        // 使用 Jackson 或 Gson 解析 JSON
        Map<String, Object> result = null;
        try {
            result = extractJsonFromText(jsonText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + jsonText, e);
        }
        context.put("agent1_result", result);
        log.info("Agent1 result: " + result);
        return null;
    }

    public Map<String, Object> extractJsonFromText(String text) {
        // 匹配 ```json{...}``` 或 ```{...}```
        Pattern pattern = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)\\s*```", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text.trim());

        String jsonExtracted = null;
        if (matcher.find()) {
            jsonExtracted = matcher.group(1).trim();
        } else {
            // 如果没有代码块，尝试直接解析整个文本
            jsonExtracted = text.trim();
        }

        try {
            return mapper.readValue(jsonExtracted, Map.class);
        } catch (JsonProcessingException e) {
            // 关键：记录原始错误文本，便于调试
            log.error("Failed to parse JSON from text: \n---\n{}\n---", text);
            throw new RuntimeException("Invalid JSON format from LLM", e);
        }
    }

}
