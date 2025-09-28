package com.zkhm.exam.ai.agents;

import com.zkhm.exam.ai.tools.KnowledgeRetrievalTool;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

/**
 * @program: zkhmExam
 * @description: Agent工厂类
 * @author: Mr.Zhang
 * @create: 2025-09-26 23:22
 **/

@Configuration
public class AgentServiceFactory {
    @Autowired
    private ChatModel qwenChatModel;

    @Autowired
    private McpToolProvider mcpToolProvider;

    @Autowired
    ContentRetriever myContentRetriever;  // RAG检索增强

    // Agent1，SearchAgent，由 通义 model + 智谱 web-serch tool，配合 system-prompt-search 实现
    @Bean
    public SearchAgent searchAgent(){
        // 会话记忆1
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(UUID.randomUUID().toString())
                .maxMessages(10)
                .build();
        return AiServices.builder(SearchAgent.class)
                .chatMemory(chatMemory)  // 会话记忆1
                .toolProvider(mcpToolProvider)  // 智谱 web-serch tool
                .chatModel(qwenChatModel)  // qwen-max
                .build();
    }

    // Agent2，AddBGAgent，由 通义 model + 智谱 web-serch tool，配合 system-prompt-addbg 实现
    @Bean
    public AddBGAgent addBGAgent(){
        // 会话记忆2
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(UUID.randomUUID().toString())
                .maxMessages(10)
                .build();
        return AiServices.builder(AddBGAgent.class)
                .chatModel(qwenChatModel)  // qwen-vl-max model
                .chatMemory(chatMemory)  // 会话记忆2
                .contentRetriever(myContentRetriever)  // RAG检索增强
                .tools(new KnowledgeRetrievalTool(myContentRetriever))  // 工具进一步增强检索
                .build();
    }

    // Agent3，MergeAgent，由 通义 model ，配合 system-prompt-merge 实现
    @Bean
    public MergeAgent mergeAgent(){
        // 会话记忆3
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(UUID.randomUUID().toString())
                .maxMessages(10)
                .build();
        return AiServices.builder(MergeAgent.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)  // 会话记忆3
                .build();
    }

}
