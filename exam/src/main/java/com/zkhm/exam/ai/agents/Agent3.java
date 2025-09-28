package com.zkhm.exam.ai.agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkhm.exam.ai.workflow.content.WorkflowContext;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.output.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @program: zkhmExam
 * @description: Agent3
 * @author: Mr.Zhang
 * @create: 2025-09-27 13:02
 **/

@Component
public class Agent3 implements Callable<Void> {
    @Autowired
    private MergeAgent mergeAgent;

    private WorkflowContext context;

    private static final ObjectMapper mapper = new ObjectMapper();

    public Agent3 setContext(WorkflowContext context) {
        this.context = context;
        return this;
    }

    @Override
    public Void call() {
        String query = context.get("user_query", String.class);
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("user_query is required");
        }

        try {
            // 动态读取：不关心内部字段名
            Map<String, Object> agent1Output = context.get("agent1_result", Map.class);
            Map<String, Object> agent2Output = context.get("agent2_result", Map.class);

            // 构造输入给最终 Agent
            String inputPrompt = buildInputPrompt(query, agent1Output, agent2Output);


            // 调用最终生成服务
            String merge = mergeAgent.merge(inputPrompt);

            // 写回上下文
            context.put("agent3_result", merge); // 或 context.put("final_result", finalResult.toMap())

        } catch (Exception e) {
            throw new RuntimeException("Agent3 execution failed", e);
        }
        return null;
    }

    /**
     * 根据两个 Agent 的原始输出构造输入提示
     */
    private String buildInputPrompt(
            String query,
            Map<String, Object> agent1Output,
            Map<String, Object> agent2Output
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户问题：").append(query).append("\n\n");

        if (agent1Output != null && !agent1Output.isEmpty()) {
            sb.append("【来自公网的信息】\n");
            appendMap(sb, agent1Output);
        }

        if (agent2Output != null && !agent2Output.isEmpty()) {
            sb.append("【来自企业内部知识库的信息】\n");
            appendMap(sb, agent2Output);
        }

        sb.append("\n请整合以上信息。");

        return sb.toString();
    }

    private void appendMap(StringBuilder sb, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }
        sb.append("\n");
    }
}
