package com.zkhm.exam.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: zkhmExam
 * @description:
 * @create: 2025-09-27 09:02
 **/

@Slf4j
@Component // 如果使用 Spring，便于自动注入
public class KnowledgeRetrievalTool {
    /**
     * 知识检索工具：基于 RAG 从向量数据库中检索与关键词相关的知识片段，
     * 适用于技术文档、项目说明、内部 FAQ 等场景
     */

    @Resource
    private ContentRetriever myContentRetriever;

    @Tool(value = """
            Searches the internal knowledge base using semantic similarity to find relevant information.
            Use this tool when the user asks about technical details, project documentation,
            system design, or any topic that might be covered in the company's knowledge base.
            Always provide a clear and specific query term.
            """)
    public String retrieveKnowledge(
            @P("the search query or question to look up in the knowledge base") String query,
            @P("maximum number of results to return, typically 3") int maxResults) {

        try {
            // 调用你已配置好的 ContentRetriever（已在 RagConfig 中定义）
            List<Content> contents = myContentRetriever.retrieve(new Query(query));

            if (contents.isEmpty()) {
                log.info("No relevant knowledge found for query: {}", query);
                return "未在知识库中找到与 '" + query + "' 相关的内容。";
            }

            // 格式化输出：包含文本、来源文件、相似度分数
            List<String> results = contents.stream()
                    .map(content -> {
                        TextSegment segment = content.textSegment();
                        String text = segment.text().trim();
                        String source = segment.metadata().getString("file_name");

                        return "【来源】" + source + "\n" +
                                "【内容】" + text + "\n";
                    })
                    .collect(Collectors.toList());

            log.debug("Retrieved {} documents for query: {}", results.size(), query);
            return String.join("\n---\n", results);

        } catch (Exception e) {
            log.error("Failed to retrieve knowledge for query: {}", query, e);
            return "知识检索失败：" + e.getMessage();
        }
    }
}
